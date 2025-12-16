package com.seckill.product.vo;

import lombok.Data;

/**
 * 商品分类返回对象（VO）
 */
@Data
public class ProductCategoryVO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父分类ID，0表示一级分类
     */
    private Long parentId;
}
