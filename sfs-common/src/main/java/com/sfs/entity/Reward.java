package com.sfs.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Reward {
    private Long id;
    private Long activityId;
    private Integer rankStart;
    private Integer rankEnd;
    private String rewardType;
    private String rewardName;
    private String rewardValue;
    private String rewardImg;
    private Integer quantity;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
