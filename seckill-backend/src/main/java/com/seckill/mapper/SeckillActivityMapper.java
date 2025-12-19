package com.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.entity.SeckillActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeckillActivityMapper extends BaseMapper<SeckillActivity> {

    IPage<SeckillActivity> selectActivityPage(Page<SeckillActivity> page, @Param("status") Integer status);

    List<SeckillActivity> selectActiveActivities();

    // 删去以下两个带@Update注解的方法（保留XML中的定义）
    int updateActivityStatusToInProgress();

    int updateActivityStatusToEnded();
}