package com.seckill.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.product.entity.ProductCategory;
import com.seckill.product.mapper.ProductCategoryMapper;
import com.seckill.product.service.ProductCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类 Service 实现
 */
@Service
public class ProductCategoryServiceImpl
        extends ServiceImpl<ProductCategoryMapper, ProductCategory>
        implements ProductCategoryService {

    @Override
    public boolean addCategory(ProductCategory category) {
        if (category == null) {
            return false;
        }
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            return false;
        }
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }

        category.setCreateTime();
        return this.save(category);
    }

    @Override
    public List<ProductCategory> listCategories(Long parentId) {
        LambdaQueryWrapper<ProductCategory> qw = new LambdaQueryWrapper<>();

        // 不传 parentId：默认查一级分类
        if (parentId == null) {
            qw.eq(ProductCategory::getParentId, 0L);
        } else {
            qw.eq(ProductCategory::getParentId, parentId);
        }

        qw.orderByDesc(ProductCategory::getId);
        return this.list(qw);
    }
}
