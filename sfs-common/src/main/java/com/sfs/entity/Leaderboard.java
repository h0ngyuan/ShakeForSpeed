package com.sfs.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Leaderboard {
    private Long id;
    private Long activityId;
    private Long userId;
    private Long score;
    private Integer rank;
    private Boolean isCheat;
    private LocalDateTime snapshotTime;
}
