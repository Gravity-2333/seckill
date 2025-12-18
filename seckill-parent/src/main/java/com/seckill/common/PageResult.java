package com.seckill.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private Long total;
    private List<T> records;

    public static <T> PageResult<T> of(Long total, List<T> records) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setRecords(records);
        return pageResult;
    }
}