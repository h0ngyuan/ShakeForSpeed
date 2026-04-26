package com.sfs.enums;

import lombok.Getter;

@Getter
public enum RewardType {
    COUPON("COUPON", "优惠券"),
    VIRTUAL_ITEM("VIRTUAL_ITEM", "虚拟物品"),
    BADGE("BADGE", "徽章"),
    CASH("CASH", "现金");

    private final String code;
    private final String desc;

    RewardType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
