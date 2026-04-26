package com.sfs.exception;

import com.sfs.enums.AppHttpCodeEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(AppHttpCodeEnum codeEnum) {
        super(codeEnum.getMsg());
        this.code = codeEnum.getCode();
    }

    public BusinessException(AppHttpCodeEnum codeEnum, String msg) {
        super(msg);
        this.code = codeEnum.getCode();
    }

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
    }
}
