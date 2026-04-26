package com.sfs.vo;

import lombok.Data;

@Data
public class RewardGrantVO {
    private Long id;
    private Long activityId;
    private Long rewardConfigId;
    private String activityName;
    private String rewardName;
    private String rewardType;
    private String rewardValue;
    private Integer rank;
    private Integer grantStatus;
    private Integer claimStatus;
    private java.time.LocalDateTime grantTime;
    private java.time.LocalDateTime claimTime;
    private java.time.LocalDateTime expireTime;
}
