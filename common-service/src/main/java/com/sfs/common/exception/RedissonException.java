package com.sfs.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedissonException extends RuntimeException {

    private int code; // 错误码
    private String message; // 错误信息

    public RedissonException(int code, String message) {
        super(message); // 调用父类构造函数
        this.code = code;
        this.message = message;
    }

    public RedissonException(String message) {
        super(message);
        this.code = 500; // 默认错误码
        this.message = message;
    }

}