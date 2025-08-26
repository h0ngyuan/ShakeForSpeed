package com.sfs.model.enums;

import lombok.Getter;

@Getter
public enum ActivityState {

    CREATED(1,"活动刚创建"),
    PENDING(2,"活动未开始"),
    ACTIVE(3,"活动进行中"),
    FINISHED(4,"活动已结束");


    int code;
    String activityState;
    ActivityState(int code,String activityState){
        this.code=code;
        this.activityState=activityState;
    }


}
