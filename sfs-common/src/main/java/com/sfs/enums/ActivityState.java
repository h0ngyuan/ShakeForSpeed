package com.sfs.enums;

import lombok.Getter;

@Getter
public enum ActivityState {
    DRAFT(1, "草稿"),
    PENDING(2, "待开始"),
    RUNNING(3, "进行中"),
    FINISHED(4, "已结束");

    private final int code;
    private final String desc;

    ActivityState(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ActivityState of(int code) {
        for (ActivityState state : values()) {
            if (state.code == code) return state;
        }
        throw new IllegalArgumentException("Unknown ActivityState code: " + code);
    }
}
