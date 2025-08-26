package com.sfs.model.enums;

public enum AppHttpCodeEnum {

    // 成功段固定为200
    SUCCESS(200,"操作成功"),
    // 登录�?~50
    NEED_LOGIN(1,"需要登录后操作"),
    LOGIN_ERROR(2,"登录失败"),
    LOGIN_OUT(3,"登出成功"),
    // TOKEN50~100
    TOKEN_INVALID(50,"无效的TOKEN"),
    TOKEN_EXPIRE(51,"TOKEN已过�?),
    TOKEN_REQUIRE(52,"TOKEN是必须的"),
    // SIGN验签 100~120
    SIGN_INVALID(100,"无效的SIGN"),
    SIG_TIMEOUT(101,"SIGN已过�?),
    // 参数错误 500~1000
    PARAM_REQUIRE(500,"缺少参数"),
    PARAM_INVALID(501,"无效参数"),
    PARAM_IMAGE_FORMAT_ERROR(502,"图片格式有误"),
    SERVER_ERROR(503,"服务器内部错�?),
    // 数据错误 1000~2000
    DATA_EXIST(1000,"数据已经存在"),
    USER_DATA_NOT_EXIST(1001,"User数据不存�?),
    DATA_NOT_EXIST(1002,"数据不存�?),
    // 数据错误 3000~3500
    NO_OPERATOR_AUTH(3000,"无权限操�?),
    NEED_ADMIND(3001,"需要管理员权限");

    int code;
    String errorMessage;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
