package com.seckill.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_product")
public class Product {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String productName;
    
    private String productDesc;
    
    private BigDecimal price;
    
    private Integer stock;
    
    private Long categoryId;
    
    private String imgUrl;
    
    private Integer status; // 1-上架 0-下架
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}