package com.sfs.vo;

import lombok.Data;

@Data
public class RewardConfigVO {
    private Long id;
    private Integer rankStart;
    private Integer rankEnd;
    private String rewardType;
    private String rewardName;
    private String rewardValue;
    private String rewardImg;
    private Integer quantity;
}
