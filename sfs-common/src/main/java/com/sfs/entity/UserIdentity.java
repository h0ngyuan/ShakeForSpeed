package com.sfs.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserIdentity {
    private Long id;
    private Long userId;
    private String identityType;
    private String identifier;
    private String credential;
    private String wxOpenid;
    private String wxUnionid;
    private Boolean verified;
    private Boolean isPrimary;
    private LocalDateTime bindTime;
}
