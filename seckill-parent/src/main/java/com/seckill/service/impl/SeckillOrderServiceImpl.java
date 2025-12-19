package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.BusinessException;
import com.seckill.common.PageQuery;
import com.seckill.common.PageResult;
import com.seckill.dto.SeckillOrderDTO;
import com.seckill.entity.SeckillActivity;
import com.seckill.entity.SeckillOrder;
import com.seckill.enums.ActivityStatusEnum;
import com.seckill.mapper.SeckillActivityMapper;
import com.seckill.mapper.SeckillOrderMapper;
import com.seckill.service.SeckillOrderService;
import com.seckill.util.RedisUtil;
import com.seckill.vo.SeckillOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀订单服务实现类（使用Redisson分布式锁）
 */
@Slf4j
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
    
    @Autowired
    private SeckillOrderMapper orderMapper;
    
    @Autowired
    private SeckillActivityMapper activityMapper;
    
    @Autowired
    private RedisUtil redisUtil;
    
    private static final String ACTIVITY_LOCK_PREFIX = "seckill:activity:lock:";
    private static final String ACTIVITY_STOCK_PREFIX = "seckill:activity:stock:";
    
    @Override
    @Transactional
    public Long createSeckillOrder(SeckillOrderDTO orderDTO) {
        Long activityId = orderDTO.getActivityId();
        Long userId = orderDTO.getUserId();
        
        // 构建锁的key
        String lockKey = ACTIVITY_LOCK_PREFIX + activityId;
        
        // 使用Redisson分布式锁执行秒杀业务逻辑
        return redisUtil.executeWithLock(lockKey, 10L, java.util.concurrent.TimeUnit.SECONDS, () -> {
            log.info("用户 {} 开始秒杀活动 {}", userId, activityId);
            
            // 1. 检查活动状态
            SeckillActivity activity = activityMapper.selectById(activityId);
            if (activity == null) {
                throw new BusinessException("秒杀活动不存在");
            }
            
            if (!activity.getStatus().equals(ActivityStatusEnum.IN_PROGRESS.getCode())) {
                throw new BusinessException("秒杀活动未进行中");
            }
            
            if (activity.getEndTime().isBefore(LocalDateTime.now())) {
                throw new BusinessException("秒杀活动已结束");
            }
            
            // 2. 从Redis获取或初始化库存
            String stockKey = ACTIVITY_STOCK_PREFIX + activityId;
            Integer remainingStock = (Integer) redisUtil.get(stockKey);
            
            if (remainingStock == null) {
                // 首次访问，从数据库加载库存到Redis
                remainingStock = activity.getSeckillStock();
                redisUtil.set(stockKey, remainingStock, 24 * 60 * 60, java.util.concurrent.TimeUnit.SECONDS);
                log.info("初始化活动 {} 库存到Redis: {}", activityId, remainingStock);
            }
            
            // 3. 检查库存
            if (remainingStock <= 0) {
                throw new BusinessException("商品已售罄");
            }
            
            // 4. 检查用户是否已经下单（防止重复购买）
            QueryWrapper<SeckillOrder> userOrderWrapper = new QueryWrapper<>();
            userOrderWrapper.eq("user_id", userId)
                           .eq("activity_id", activityId)
                           .ne("status", 2) // 排除已取消的订单
                           .eq("deleted", 0);
            SeckillOrder existingOrder = orderMapper.selectOne(userOrderWrapper);
            if (existingOrder != null) {
                throw new BusinessException("您已经参与过此秒杀活动");
            }
            
            // 5. 预扣减库存（Redis）
            Long newStock = redisUtil.decrement(stockKey);
            if (newStock < 0) {
                // 库存不足，回滚Redis库存
                redisUtil.increment(stockKey);
                throw new BusinessException("商品已售罄");
            }
            
            try {
                // 6. 创建订单
                SeckillOrder order = new SeckillOrder();
                BeanUtils.copyProperties(orderDTO, order);
                order.setOrderNo(generateOrderNo());
                order.setProductName(activity.getActivityName());
                order.setSeckillPrice(activity.getSeckillPrice());
                order.setTotalAmount(activity.getSeckillPrice().multiply(BigDecimal.valueOf(orderDTO.getQuantity())));
                order.setStatus(0); // 待支付
                order.setCreateTime(LocalDateTime.now());
                order.setUpdateTime(LocalDateTime.now());
                
                int result = orderMapper.insert(order);
                if (result <= 0) {
                    // 订单创建失败，回滚Redis库存
                    redisUtil.increment(stockKey);
                    throw new BusinessException("创建订单失败");
                }
                
                log.info("用户 {} 秒杀成功，订单ID: {}", userId, order.getId());
                return order.getId();
                
            } catch (Exception e) {
                // 异常情况回滚Redis库存
                redisUtil.increment(stockKey);
                log.error("创建订单异常，回滚库存", e);
                throw new BusinessException("创建订单失败");
            }
        });
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        String lockKey = "seckill:order:cancel:" + orderId;
        
        // 使用try-with-resources模式管理锁
        if (redisUtil.tryLock(lockKey, 5L, java.util.concurrent.TimeUnit.SECONDS)) {
            try {
                SeckillOrder order = orderMapper.selectById(orderId);
                if (order == null) {
                    throw new BusinessException("订单不存在");
                }
                
                if (!order.getStatus().equals(0)) {
                    throw new BusinessException("只能取消待支付订单");
                }
                
                // 更新订单状态
                order.setStatus(2); // 已取消
                order.setUpdateTime(LocalDateTime.now());
                orderMapper.updateById(order);
                
                // 回滚Redis库存
                String stockKey = ACTIVITY_STOCK_PREFIX + order.getActivityId();
                redisUtil.increment(stockKey);
                
                log.info("取消订单成功，订单ID: {}, 回滚库存", orderId);
            } finally {
                redisUtil.unlock(lockKey);
            }
        } else {
            throw new BusinessException("获取锁失败，请稍后重试");
        }
    }
    
    @Override
    @Transactional
    public void payOrder(Long orderId) {
        SeckillOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getStatus().equals(0)) {
            throw new BusinessException("订单状态异常");
        }
        
        // 更新订单状态为已支付
        order.setStatus(1);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 扣减数据库库存
        String lockKey = "seckill:stock:update:" + order.getActivityId();
        if (redisUtil.tryLock(lockKey, 5L, java.util.concurrent.TimeUnit.SECONDS)) {
            try {
                SeckillActivity activity = activityMapper.selectById(order.getActivityId());
                activity.setSeckillStock(activity.getSeckillStock() - order.getQuantity());
                activityMapper.updateById(activity);
            } finally {
                redisUtil.unlock(lockKey);
            }
        } else {
            throw new BusinessException("获取锁失败，请稍后重试");
        }
        
        log.info("订单支付成功，订单ID: {}", orderId);
    }
    
    @Override
    public SeckillOrderVO getOrderDetail(Long orderId) {
        SeckillOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        SeckillOrderVO vo = new SeckillOrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusDesc(getOrderStatusDesc(order.getStatus()));
        
        return vo;
    }
    
    @Override
    public List<SeckillOrderVO> getUserOrders(Long userId, Integer status) {
        QueryWrapper<SeckillOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.eq("deleted", 0)
               .orderByDesc("create_time");
        
        List<SeckillOrder> orders = orderMapper.selectList(wrapper);
        
        List<SeckillOrderVO> voList = new ArrayList<>();
        for (SeckillOrder order : orders) {
            SeckillOrderVO vo = new SeckillOrderVO();
            BeanUtils.copyProperties(order, vo);
            vo.setStatusDesc(getOrderStatusDesc(order.getStatus()));
            voList.add(vo);
        }
        
        return voList;
    }
    
    @Override
    public PageResult<SeckillOrderVO> getOrderList(PageQuery pageQuery, Integer status) {
        Page<SeckillOrder> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        IPage<SeckillOrder> orderPage = orderMapper.selectOrderPage(page, status);
        
        // 转换为VO
        List<SeckillOrderVO> voList = new ArrayList<>();
        for (SeckillOrder order : orderPage.getRecords()) {
            SeckillOrderVO vo = new SeckillOrderVO();
            BeanUtils.copyProperties(order, vo);
            vo.setStatusDesc(getOrderStatusDesc(order.getStatus()));
            voList.add(vo);
        }
        
        return new PageResult<>(voList, orderPage.getTotal(), pageQuery.getPageNum(), pageQuery.getPageSize());
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "SK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * 获取订单状态描述
     */
    private String getOrderStatusDesc(Integer status) {
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已取消";
            default: return "未知状态";
        }
    }
}