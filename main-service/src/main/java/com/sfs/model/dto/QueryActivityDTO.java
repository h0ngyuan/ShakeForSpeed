package com.sfs.model.dto;

import com.sfs.model.entity.Page;
import com.sfs.model.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QueryActivityDTO extends Page {
    private String activityNameOrId;
    private LocalDateTime rangeTimeBefore;
    private LocalDateTime rangeTimeAfter;
    private UserRole role;
    private Integer pageIndex;
    private Integer pageSize;
}
