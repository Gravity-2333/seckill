package com.seckill.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.product.entity.Product;
import com.seckill.product.mapper.ProductMapper;
import com.seckill.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 商品 Service 实现
 */
@Service
public class ProductServiceImpl
        extends ServiceImpl<ProductMapper, Product>
        implements ProductService {

    @Override
    public boolean addProduct(Product product) {
        if (product == null) {
            return false;
        }
        // 最基本参数校验（防脏数据）
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            return false;
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        if (product.getStock() == null || product.getStock() < 0) {
            return false;
        }
        if (product.getCategoryId() == null) {
            return false;
        }
        if (product.getStatus() == null) {
            product.setStatus(1); // 默认上架
        }

        product.setCreateTime();
        return this.save(product);
    }

    @Override
    public boolean updateProduct(Product product) {
        if (product == null || product.getId() == null) {
            return false;
        }
        return this.updateById(product);
    }

    @Override
    public boolean deleteProduct(Long id) {
        if (id == null) {
            return false;
        }
        return this.removeById(id);
    }

    @Override
    public IPage<Product> listProducts(int page, int size, Long categoryId) {
        Page<Product> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<>();
        // 匿名/普通用户：只查询上架商品
        qw.eq(Product::getStatus, 1);

        if (Objects.nonNull(categoryId)) {
            qw.eq(Product::getCategoryId, categoryId);
        }

        qw.orderByDesc(Product::getId);
        return this.page(pageParam, qw);
    }
}
