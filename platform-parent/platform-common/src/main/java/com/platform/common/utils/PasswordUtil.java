package com.platform.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码资源类
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 验证密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 主方法 - 用于生成密码哈希
     */
    public static void main(String[] args) {
        String password = "admin123";
        String encoded = encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + encoded);
        System.out.println("验证结果: " + matches(password, encoded));
    }
}