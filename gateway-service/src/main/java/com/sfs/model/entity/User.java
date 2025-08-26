package com.sfs.model.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long userId;
    private String role;
    private String username;
    private String password;
    private LocalDateTime updateTime;
    private String banFlag;
    @TableLogic
    private String deleteFlag;
    @TableField(exist = false)
    private String token;
}
