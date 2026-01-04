package com.sfs.model.dto;

import lombok.Getter;

@Getter
public class LoginQuery {
    private String account;
    private String password;
    private String code;
}
