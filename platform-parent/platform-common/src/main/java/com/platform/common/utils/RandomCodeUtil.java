package com.platform.common.utils;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * 随机码生成工具类
 */
public class RandomCodeUtil {

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CHARS_LENGTH = CHARS.length();
    private static final SecureRandom RANDOM = new SecureRandom();

    // 用于检测冲突的重试次数
    private static final int MAX_RETRIES = 100;

    /**
     * 生成指定长度的随机码
     *
     * @param length 长度（建议6-10位）
     * @return 随机码
     */
    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS_LENGTH)));
        }
        return sb.toString();
    }

    /**
     * 生成6位随机码（默认）
     */
    public static String generate() {
        return generate(6);
    }

    /**
     * 生成不重复的随机码
     *
     * @param length      长度
     * @param existingSet 已存在的随机码集合
     * @return 随机码
     */
    public static String generateUnique(int length, Set<String> existingSet) {
        if (existingSet == null) {
            existingSet = new HashSet<>();
        }

        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = generate(length);
            if (!existingSet.contains(code)) {
                return code;
            }
        }

        // 如果重试次数用尽，增加一位长度
        return generate(length + 1);
    }

    /**
     * 验证随机码格式是否有效
     *
     * @param code 随机码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        for (char c : code.toCharArray()) {
            if (CHARS.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }
}