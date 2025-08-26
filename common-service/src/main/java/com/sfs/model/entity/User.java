package com.sfs.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long userId;
    private String role;
    private String username;
    private String password;
    private LocalDateTime updateTime;
    private String banFlag;
    private String deleteFlag;
    private String token;
}
