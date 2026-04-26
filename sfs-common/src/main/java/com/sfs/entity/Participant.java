package com.sfs.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Participant {
    private Long id;
    private Long activityId;
    private Long userId;
    private LocalDateTime joinTime;
    private Integer status;
    private String deviceId;
    private String platform;
    private Boolean isReady;
}
