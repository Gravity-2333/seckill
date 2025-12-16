package com.seckill.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类表实体
 */
@Data
@TableName("t_product_category")
public class ProductCategory {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父分类ID，0表示一级分类
     */
    private Long parentId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    // 插入前自动填充创建时间
    public void setCreateTime() {
        this.createTime = LocalDateTime.now();
    }
}
