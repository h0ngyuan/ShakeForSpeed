package com.sfs.model.dto;

import com.sfs.model.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRoleDTO {
    private Long userId;
    private UserRole role;
}
