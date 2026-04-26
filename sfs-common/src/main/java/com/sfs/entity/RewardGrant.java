package com.sfs.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RewardGrant {
    private Long id;
    private Long userId;
    private Long activityId;
    private Long rewardConfigId;
    private Integer rank;
    private Integer grantStatus;
    private Integer claimStatus;
    private LocalDateTime grantTime;
    private LocalDateTime claimTime;
    private LocalDateTime expireTime;
    private String transactionNo;
}
