package com.sfs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("reward_config")
public class Reward {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Integer rankStart;
    private Integer rankEnd;
    private String rewardType;
    private String rewardName;
    private String rewardValue;
    private String rewardImg;
    private Integer quantity;
    private Integer validDays;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
