package com.sfs.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAccountVO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String uid;
    private String nickname;
    private String avatarUrl;        // → 映射 avatar_url
    private Integer gender;
    private LocalDateTime birthday;
    private String country;
    private String province;
    private String city;
    private String language;
    private String timezone;
    private String registerPlatform; // → 映射 register_platform
    private String registerIp;       // → 映射 register_ip
    private LocalDateTime registerTime;
    private LocalDateTime lastLoginTime;
    private Integer loginCount;
    private Integer status;
    private LocalDateTime banUtil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
