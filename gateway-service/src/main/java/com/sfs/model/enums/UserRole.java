package com.sfs.model.enums;

import org.apache.kafka.common.protocol.types.Field;

public enum UserRole {

    ADMIN(1,"管理�?),
    MERCHANT(2,"商家"),
    PARTICIPANT(3,"参与�?);

    int code;
    String roleName;
    UserRole(int code,String roleName){
        this.code=code;
        this.roleName=roleName;
    }
    public int getCode(){
        return code;
    }
    public String getRoleName(){
        return roleName;
    }
}
