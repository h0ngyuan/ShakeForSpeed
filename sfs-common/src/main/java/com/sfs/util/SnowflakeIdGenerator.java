package com.sfs.util;

import cn.hutool.core.util.IdUtil;

public class SnowflakeIdGenerator {
    private static final SnowflakeIdGenerator INSTANCE = new SnowflakeIdGenerator();
    private final cn.hutool.core.lang.Snowflake snowflake;

    private SnowflakeIdGenerator() {
        this.snowflake = IdUtil.getSnowflake(1, 1);
    }

    public static SnowflakeIdGenerator getInstance() {
        return INSTANCE;
    }

    public long nextId() {
        return snowflake.nextId();
    }

    public String nextUid() {
        return String.valueOf(snowflake.nextId());
    }
}
