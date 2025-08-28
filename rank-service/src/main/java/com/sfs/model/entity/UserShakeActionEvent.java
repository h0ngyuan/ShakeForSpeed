package com.sfs.model.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserShakeActionEvent {

    private String activityId;
    private Long userId;
    private Integer count;

}
