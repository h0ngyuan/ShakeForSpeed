package com.sfs.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RankPageVO {
    private List<RankItemVO> items;
    private Long total;
}
