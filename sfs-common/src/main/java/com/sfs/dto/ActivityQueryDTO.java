package com.sfs.dto;

import lombok.Data;

@Data
public class ActivityQueryDTO {
    private Integer state;
    private String type;
    private Long creatorId;
    private Integer page = 1;
    private Integer size = 10;
}
