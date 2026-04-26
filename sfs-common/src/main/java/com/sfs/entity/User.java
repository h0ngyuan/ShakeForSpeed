package com.sfs.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String uid;
    private String nickname;
    private String avatarUrl;
    private Integer gender;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
