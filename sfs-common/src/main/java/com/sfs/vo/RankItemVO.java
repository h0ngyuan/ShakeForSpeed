package com.sfs.vo;

import lombok.Data;

@Data
public class RankItemVO {
    private Integer rank;
    private String userId;
    private String nickname;
    private String avatarUrl;
    private Long score;
    private Boolean isCheat;
}
