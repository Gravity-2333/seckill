package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.BusinessException;
import com.seckill.common.PageQuery;
import com.seckill.common.PageResult;
import com.seckill.dto.SeckillActivityDTO;
import com.seckill.entity.Product;
import com.seckill.entity.SeckillActivity;
import com.seckill.enums.ActivityStatusEnum;
import com.seckill.mapper.ProductMapper;
import com.seckill.mapper.SeckillActivityMapper;
import com.seckill.service.SeckillActivityService;
import com.seckill.vo.SeckillActivityVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {
    
    @Autowired
    private SeckillActivityMapper activityMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Transactional
    public void saveActivity(SeckillActivityDTO activityDTO) {
        // 参数校验
        validateActivityParam(activityDTO);
        
        // 检查商品是否存在
        Product product = productMapper.selectById(activityDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 检查是否已有该商品的秒杀活动
        QueryWrapper<SeckillActivity> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", activityDTO.getProductId())
               .eq("deleted", 0)
               .ge("end_time", LocalDateTime.now());
        SeckillActivity existActivity = activityMapper.selectOne(wrapper);
        if (existActivity != null) {
            throw new BusinessException("该商品已有进行中或未开始的秒杀活动");
        }
        
        // 创建秒杀活动
        SeckillActivity activity = new SeckillActivity();
        BeanUtils.copyProperties(activityDTO, activity);
        
        // 设置活动状态
        LocalDateTime now = LocalDateTime.now();
        if (activityDTO.getStartTime().isBefore(now)) {
            activity.setStatus(ActivityStatusEnum.IN_PROGRESS.getCode());
        } else {
            activity.setStatus(ActivityStatusEnum.NOT_STARTED.getCode());
        }
        
        activityMapper.insert(activity);
        log.info("创建秒杀活动成功：{}", activity.getId());
    }
    
    @Override
    @Transactional
    public void updateActivity(Long id, SeckillActivityDTO activityDTO) {
        // 参数校验
        validateActivityParam(activityDTO);
        
        // 检查活动是否存在
        SeckillActivity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("秒杀活动不存在");
        }
        
        // 检查商品是否存在
        Product product = productMapper.selectById(activityDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 如果修改了商品，需要检查是否冲突
        if (!activity.getProductId().equals(activityDTO.getProductId())) {
            QueryWrapper<SeckillActivity> wrapper = new QueryWrapper<>();
            wrapper.eq("product_id", activityDTO.getProductId())
                   .eq("deleted", 0)
                   .ne("id", id)
                   .ge("end_time", LocalDateTime.now());
            SeckillActivity existActivity = activityMapper.selectOne(wrapper);
            if (existActivity != null) {
                throw new BusinessException("该商品已有进行中或未开始的秒杀活动");
            }
        }
        
        // 更新活动信息
        BeanUtils.copyProperties(activityDTO, activity);
        activity.setId(id);
        
        // 如果是进行中的活动，需要重新判断状态
        if (activity.getStatus().equals(ActivityStatusEnum.IN_PROGRESS.getCode())) {
            LocalDateTime now = LocalDateTime.now();
            if (activity.getEndTime().isBefore(now)) {
                activity.setStatus(ActivityStatusEnum.ENDED.getCode());
            }
        }
        
        activityMapper.updateById(activity);
        log.info("更新秒杀活动成功：{}", id);
    }
    
    @Override
    @Transactional
    public void updateActivityStatus(Long id, Integer status) {
        // 检查活动是否存在
        SeckillActivity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("秒杀活动不存在");
        }
        
        // 检查状态是否有效
        if (ActivityStatusEnum.getByCode(status) == null) {
            throw new BusinessException("无效的活动状态");
        }
        
        activity.setStatus(status);
        activityMapper.updateById(activity);
        log.info("更新秒杀活动状态成功：{}, 新状态：{}", id, status);
    }
    
    @Override
    public SeckillActivityVO getActivityById(Long id) {
        // 查询活动信息
        SeckillActivity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("秒杀活动不存在");
        }
        
        // 查询商品信息
        Product product = productMapper.selectById(activity.getProductId());
        if (product == null) {
            throw new BusinessException("关联商品不存在");
        }
        
        // 组装返回信息
        SeckillActivityVO vo = new SeckillActivityVO();
        BeanUtils.copyProperties(activity, vo);
        vo.setProductName(product.getProductName());
        vo.setProductDesc(product.getProductDesc());
        vo.setOriginalPrice(product.getPrice());
        vo.setImgUrl(product.getImgUrl());
        vo.setStatusDesc(ActivityStatusEnum.getByCode(activity.getStatus()).getDesc());
        
        return vo;
    }
    
    @Override
    public IPage<SeckillActivityVO> getActivityList(PageQuery pageQuery, Integer status) {
        // 分页查询活动
        Page<SeckillActivity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        IPage<SeckillActivity> activityPage = activityMapper.selectActivityPage(page, status);
        
        // 转换为VO
        IPage<SeckillActivityVO> voPage = new Page<>();
        BeanUtils.copyProperties(activityPage, voPage);
        
        List<SeckillActivityVO> voList = new ArrayList<>();
        for (SeckillActivity activity : activityPage.getRecords()) {
            SeckillActivityVO vo = new SeckillActivityVO();
            BeanUtils.copyProperties(activity, vo);
            
            // 查询商品信息
            Product product = productMapper.selectById(activity.getProductId());
            if (product != null) {
                vo.setProductName(product.getProductName());
                vo.setOriginalPrice(product.getPrice());
                vo.setImgUrl(product.getImgUrl());
            }
            
            vo.setStatusDesc(ActivityStatusEnum.getByCode(activity.getStatus()).getDesc());
            voList.add(vo);
        }
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    @Transactional
    public void updateActivityStatusAutomatically() {
        LocalDateTime now = LocalDateTime.now();
        
        // 更新应该开始的活动
        QueryWrapper<SeckillActivity> startWrapper = new QueryWrapper<>();
        startWrapper.eq("status", ActivityStatusEnum.NOT_STARTED.getCode())
                   .le("start_time", now)
                   .gt("end_time", now)
                   .eq("deleted", 0);
        
        List<SeckillActivity> shouldStartActivities = activityMapper.selectList(startWrapper);
        for (SeckillActivity activity : shouldStartActivities) {
            activity.setStatus(ActivityStatusEnum.IN_PROGRESS.getCode());
            activityMapper.updateById(activity);
            log.info("自动开始秒杀活动：{}", activity.getId());
        }
        
        // 更新应该结束的活动
        QueryWrapper<SeckillActivity> endWrapper = new QueryWrapper<>();
        endWrapper.eq("status", ActivityStatusEnum.IN_PROGRESS.getCode())
                 .lt("end_time", now)
                 .eq("deleted", 0);
        
        List<SeckillActivity> shouldEndActivities = activityMapper.selectList(endWrapper);
        for (SeckillActivity activity : shouldEndActivities) {
            activity.setStatus(ActivityStatusEnum.ENDED.getCode());
            activityMapper.updateById(activity);
            log.info("自动结束秒杀活动：{}", activity.getId());
        }
    }
    
    @Override
    public List<SeckillActivityVO> getActiveActivities() {
        // 查询所有活跃的活动（进行中和未开始）
        QueryWrapper<SeckillActivity> wrapper = new QueryWrapper<>();
        wrapper.in("status", ActivityStatusEnum.NOT_STARTED.getCode(), ActivityStatusEnum.IN_PROGRESS.getCode())
               .gt("end_time", LocalDateTime.now())
               .eq("deleted", 0)
               .orderByAsc("start_time");
        
        List<SeckillActivity> activities = activityMapper.selectList(wrapper);
        
        List<SeckillActivityVO> voList = new ArrayList<>();
        for (SeckillActivity activity : activities) {
            SeckillActivityVO vo = new SeckillActivityVO();
            BeanUtils.copyProperties(activity, vo);
            
            // 查询商品信息
            Product product = productMapper.selectById(activity.getProductId());
            if (product != null) {
                vo.setProductName(product.getProductName());
                vo.setProductDesc(product.getProductDesc());
                vo.setOriginalPrice(product.getPrice());
                vo.setImgUrl(product.getImgUrl());
            }
            
            vo.setStatusDesc(ActivityStatusEnum.getByCode(activity.getStatus()).getDesc());
            voList.add(vo);
        }
        
        return voList;
    }
    
    /**
     * 校验秒杀活动参数
     */
    private void validateActivityParam(SeckillActivityDTO activityDTO) {
        if (activityDTO == null) {
            throw new BusinessException("活动信息不能为空");
        }
        
        if (!StringUtils.hasText(activityDTO.getActivityName())) {
            throw new BusinessException("活动名称不能为空");
        }
        
        if (activityDTO.getProductId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
        
        if (activityDTO.getSeckillPrice() == null || activityDTO.getSeckillPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("秒杀价格必须大于0");
        }
        
        if (activityDTO.getSeckillStock() == null || activityDTO.getSeckillStock() <= 0) {
            throw new BusinessException("秒杀库存必须大于0");
        }
        
        if (activityDTO.getStartTime() == null) {
            throw new BusinessException("开始时间不能为空");
        }
        
        if (activityDTO.getEndTime() == null) {
            throw new BusinessException("结束时间不能为空");
        }
        
        if (activityDTO.getStartTime().isAfter(activityDTO.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        
        if (activityDTO.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("结束时间不能早于当前时间");
        }
    }
}