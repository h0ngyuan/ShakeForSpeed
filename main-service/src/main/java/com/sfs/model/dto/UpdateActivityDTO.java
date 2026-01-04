package com.sfs.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateActivityDTO {
    private Long id;
    private String activityName;
    private LocalDateTime beginTime;
    private Long durTime;
    private Double longitude;
    private Double latitude;
}
