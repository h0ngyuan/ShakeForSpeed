package com.sfs.vo;

import lombok.Data;

@Data
public class ActivityVO {
    private Long id;
    private String activityNo;
    private String name;
    private String type;
    private Integer state;
    private String roomCode;
    private String description;
    private String coverImg;
    private java.time.LocalDateTime beginTime;
    private java.time.LocalDateTime endTime;
}
