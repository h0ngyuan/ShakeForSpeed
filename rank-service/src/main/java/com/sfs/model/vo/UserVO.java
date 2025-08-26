package com.sfs.model.vo;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVO {
    private Long userId;
    private String username;
    private String role;
    private String token;

}
