package com.sfs.vo;

import lombok.Data;

@Data
public class TokenVO {
    private String token;
    private Long expireAt;
}
