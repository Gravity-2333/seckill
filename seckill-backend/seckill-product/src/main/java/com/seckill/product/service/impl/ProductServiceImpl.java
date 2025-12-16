package com.seckill.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.product.entity.Product;
import com.seckill.product.mapper.ProductMapper;
import com.seckill.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        // 只查询上架商品
        qw.eq(Product::getStatus, 1);

        if (Objects.nonNull(categoryId)) {
            qw.eq(Product::getCategoryId, categoryId);
        }

        qw.orderByDesc(Product::getId);
        return this.page(pageParam, qw);
    }
}
