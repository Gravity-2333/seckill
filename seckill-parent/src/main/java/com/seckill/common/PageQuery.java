package com.seckill.common;

import lombok.Data;

@Data
public class PageQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}