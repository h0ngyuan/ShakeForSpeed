package com.sfs.util;

import cn.hutool.core.util.RandomUtil;

public class RoomCodeGenerator {
    private static final String CHARS = "0123456789";

    public static String generate() {
        return RandomUtil.randomString(CHARS, 6);
    }

    public static String generatePassword() {
        return RandomUtil.randomString(CHARS, 6);
    }
}
