package com.seckill.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.product.entity.Product;

/**
 * 商品 Service
 */
public interface ProductService extends IService<Product> {

    /**
     * 新增商品（自动填充创建时间）
     */
    boolean addProduct(Product product);

    /**
     * 修改商品（按ID更新）
     */
    boolean updateProduct(Product product);

    /**
     * 删除商品
     */
    boolean deleteProduct(Long id);

    /**
     * 商品分页列表（前台可匿名访问）
     * @param page 页码（从1开始）
     * @param size 每页条数
     * @param categoryId 分类ID（可选）
     */
    IPage<Product> listProducts(int page, int size, Long categoryId);
}
