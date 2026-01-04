package com.sfs.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordDTO {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}
