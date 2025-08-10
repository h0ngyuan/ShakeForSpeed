package com.sfs.shakeforspeed.utils.result;

import com.sfs.shakeforspeed.model.enums.AppHttpCodeEnum;
import lombok.Data;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    //状态码
    private Integer code;
    //响应数据
    private T data;
    //响应信息
    private String msg;

    public Result(){this.code=200;}

    public Result(Integer code,T data){
        this.code=code;
        this.data=data;
    }

    public Result(Integer code,T data,String msg){
        this.code=code;
        this.data=data;
        this.msg=msg;
    }

    public Result(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public static Result successResult(int code,String msg){
        Result result=new Result();
        return result.success(code,msg);
    }

    public static Result successResult(Object data){
        Result result = setCodeEnum(AppHttpCodeEnum.SUCCESS,AppHttpCodeEnum.SUCCESS.getErrorMessage());
        if (data!=null){
            result.setData(data);
        }
        return result;
    }

    public static Result successResult(int code,Object data,String msg){
        Result result = new Result();
        return result.success(code,data,msg);
    }

    public static Result failResult(int code,String msg){
        Result result = new Result();
        return result.fail(code,msg);
    }

    public static Result failResult(AppHttpCodeEnum enums){
        return setCodeEnum(enums);
    }

    public static Result failResult(AppHttpCodeEnum enums, String errorMessage){
        return setCodeEnum(enums,errorMessage);
    }

    public Result<T> fail(Integer code,String msg){
        this.code=code;
        this.msg=msg;
        return this;
    }

    public Result<T> success(Integer code,T data,String msg){
        this.code=code;
        this.data=data;
        this.msg=msg;
        return this;
    }

    public Result<T> success(Integer code,T data){
        this.code=code;
        this.data=data;
        return this;
    }

    public Result<T> success(T data){
        this.data=data;
        return this;
    }

    public static Result setCodeEnum(AppHttpCodeEnum enums){
        return successResult(enums.getCode(),enums.getErrorMessage());
    }

    public static Result setCodeEnum(AppHttpCodeEnum enums,String msg){
        return successResult(enums.getCode(),msg);
    }
}
