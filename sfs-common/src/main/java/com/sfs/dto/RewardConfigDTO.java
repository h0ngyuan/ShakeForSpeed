package com.sfs.dto;

import lombok.Data;

@Data
public class RewardConfigDTO {
    private Integer rankStart;
    private Integer rankEnd;
    private String rewardType;
    private String rewardName;
    private String rewardValue;
    private String rewardImg;
    private Integer quantity;
}
