package com.sfs.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String uid;
    private String nickname;
    private String avatarUrl;
    private Integer gender;
}
