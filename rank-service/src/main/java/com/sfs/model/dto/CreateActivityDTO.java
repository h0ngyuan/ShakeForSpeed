package com.sfs.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class CreateActivityDTO {
    private String activityName;
    private LocalDateTime beginTime;
    private Long durTime;
    private List<RewardDTO> rewards;
    private Double longitude;
    private Double latitude;
}
