package com.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.entity.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 秒杀订单Mapper接口
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
    
    /**
     * 分页查询订单列表
     * @param page 分页参数
     * @param status 订单状态
     * @return 分页结果
     */
    IPage<SeckillOrder> selectOrderPage(Page<SeckillOrder> page, @Param("status") Integer status);
}