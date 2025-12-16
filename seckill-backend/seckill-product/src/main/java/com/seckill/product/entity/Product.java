package com.seckill.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品表实体
 */
@Data
@TableName("t_product")
public class Product {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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

    /**
     * 状态：1-上架/0-下架
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    // 插入前自动填充创建时间
    public void setCreateTime() {
        this.createTime = LocalDateTime.now();
    }
}
