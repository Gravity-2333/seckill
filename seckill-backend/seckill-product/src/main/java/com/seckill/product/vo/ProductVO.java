package com.seckill.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品返回对象（VO）
 */
@Data
public class ProductVO {

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品描述
     */
    private String productDesc;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品图片URL
     */
    private String imgUrl;
}
