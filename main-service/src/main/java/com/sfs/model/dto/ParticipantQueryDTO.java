package com.sfs.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantQueryDTO {
    private Long activityId;
    private Integer pageIndex;
    private Integer pageSize;
}
