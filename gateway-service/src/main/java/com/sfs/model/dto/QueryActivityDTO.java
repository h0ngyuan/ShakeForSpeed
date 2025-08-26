package com.sfs.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QueryActivityDTO {
    private String activityNameOrId;
    private LocalDateTime rangeTimeBefore;
    private LocalDateTime rangeTimeAfter;
    private UserRole role;
    private Integer pageIndex;
    private Integer pageSize;
}
