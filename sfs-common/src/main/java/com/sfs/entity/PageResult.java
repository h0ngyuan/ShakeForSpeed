package com.sfs.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PageResult<T> {
    private Long total;
    private Integer page;
    private Integer size;
    private java.util.List<T> records;

    public static <T> PageResult<T> of(Long total, Integer page, Integer size, java.util.List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        result.setRecords(records);
        return result;
    }
}
