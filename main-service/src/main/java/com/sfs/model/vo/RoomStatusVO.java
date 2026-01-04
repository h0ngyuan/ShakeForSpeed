package com.sfs.model.vo;

import com.sfs.model.enums.ActivityState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomStatusVO {
    private Long activityId;
    private ActivityState state;
    private Integer participantsCount;
}
