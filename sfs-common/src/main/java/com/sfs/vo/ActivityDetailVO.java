package com.sfs.vo;

import lombok.Data;
import java.util.List;

@Data
public class ActivityDetailVO {
    private Long id;
    private String activityNo;
    private String name;
    private String type;
    private Integer state;
    private String roomCode;
    private String roomPwd;
    private String description;
    private String coverImg;
    private String locationName;
    private Double lng;
    private Double lat;
    private java.time.LocalDateTime beginTime;
    private java.time.LocalDateTime endTime;
    private Long durationMs;
    private Integer maxParticipants;
    private List<RewardConfigVO> rewards;
}
