package com.sfs.vo;

import lombok.Data;

@Data
public class RoomStatusVO {
    private String roomCode;
    private String activityName;
    private Integer state;
    private Long onlineCount;
    private Long maxParticipants;
}
