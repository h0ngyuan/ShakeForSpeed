package com.sfs.model.enums;

import lombok.Getter;

@Getter
    public enum ActivityType{
        SFS(1,"拼手速摇一摇"),
        SFR(2,"拼手速抢红包");
        int activityTypeId;
        String activityTypeName;
        ActivityType(int activityTypeId, String activityTypeName){
            this.activityTypeId=activityTypeId;
            this.activityTypeName=activityTypeName;
        }
    }