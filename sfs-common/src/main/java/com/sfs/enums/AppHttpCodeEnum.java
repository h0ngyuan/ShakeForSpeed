package com.sfs.enums;

import lombok.Getter;

@Getter
public enum AppHttpCodeEnum {
    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    AUTH_CODE_INVALID(1001, "微信code无效"),
    USER_NOT_FOUND(1002, "用户不存在"),
    TOKEN_EXPIRED(1003, "Token已过期"),
    TOKEN_INVALID(1004, "Token无效"),

    ACTIVITY_NOT_FOUND(2001, "活动不存在"),
    ACTIVITY_NOT_RUNNING(2002, "活动未进行中"),
    ROOM_CODE_INVALID(2003, "房间码错误"),
    ROOM_PWD_INVALID(2004, "房间密码错误"),
    ROOM_FULL(2005, "房间已满"),

    RANK_NOT_FOUND(3001, "排行榜不存在"),

    REWARD_NOT_FOUND(4001, "奖励不存在"),
    REWARD_EXPIRED(4002, "奖励已过期"),
    REWARD_ALREADY_CLAIMED(4003, "奖励已领取");

    private final int code;
    private final String msg;

    AppHttpCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
