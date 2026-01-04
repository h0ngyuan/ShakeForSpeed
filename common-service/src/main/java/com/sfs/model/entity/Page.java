package com.sfs.model.entity;

import lombok.Data;

@Data
public class Page {
    //页位置
    private Integer current = 1;
    //页大小
    private Integer pageSize = 10;
}
