package com.seckill.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.product.entity.ProductCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类 Mapper
 */
@Mapper
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {
}
