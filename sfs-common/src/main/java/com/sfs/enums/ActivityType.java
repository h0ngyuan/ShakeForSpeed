package com.sfs.enums;

import lombok.Getter;

@Getter
public enum ActivityType {
    SHAKE_COUNT("SHAKE_COUNT", "摇动计数"),
    SHAKE_REDBAG("SHAKE_REDBAG", "摇动红包");

    private final String code;
    private final String desc;

    ActivityType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
