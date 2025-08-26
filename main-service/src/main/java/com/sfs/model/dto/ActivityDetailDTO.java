package com.sfs.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityDetailDTO {
    private Long id;
    private String activityName;
    private String activityType;
    private String creatorRole;
    private LocalDateTime beginTime;
    private LocalDateTime createTime;
    private Long creatorId;
    private Long durTime;
    private Integer roomPwd;
    private Double longitude;
    private Double latitude;
    private List<RewardDTO> rewards;

}
