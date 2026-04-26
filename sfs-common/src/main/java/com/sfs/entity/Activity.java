package com.sfs.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Activity {
    private Long id;
    private String activityNo;
    private String name;
    private String type;
    private Long creatorId;
    private Integer state;
    private String roomCode;
    private String roomPwd;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Long durationMs;
    private Integer maxParticipants;
    private String description;
    private String coverImg;
    private String locationName;
    private Double lng;
    private Double lat;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
