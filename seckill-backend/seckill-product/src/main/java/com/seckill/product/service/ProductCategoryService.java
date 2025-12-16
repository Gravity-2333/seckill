package com.seckill.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.product.entity.ProductCategory;

import java.util.List;

/**
 * 商品分类 Service
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 新增分类（会自动填充创建时间）
     */
    boolean addCategory(ProductCategory category);

    /**
     * 查询分类列表
     * @param parentId 父分类ID；传 null 表示查询全部
     */
    List<ProductCategory> listCategories(Long parentId);
}
