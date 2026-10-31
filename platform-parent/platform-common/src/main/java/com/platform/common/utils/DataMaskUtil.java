package com.platform.common.utils;

/**
 * 数据脱敏工具类
 */
public class DataMaskUtil {

    private DataMaskUtil() {
        // 私有构造函数，防止实例化
    }

    /**
     * 邮箱脱敏
     * 格式：a***@example.com
     *
     * @param email 邮箱地址
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email;
        }

        String name = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        String maskedName;
        if (name.length() <= 1) {
            maskedName = name.charAt(0) + "***";
        } else if (name.length() <= 3) {
            maskedName = name.charAt(0) + "***";
        } else {
            maskedName = name.charAt(0) + "***" + name.charAt(name.length() - 1);
        }

        return maskedName + domain;
    }

    /**
     * 手机号脱敏
     * 格式：138****8888
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }

        // 移除所有非数字字符
        String digits = phone.replaceAll("[^0-9]", "");

        if (digits.length() < 7) {
            return phone;
        }

        if (digits.length() == 11) {
            return digits.substring(0, 3) + "****" + digits.substring(7);
        }

        // 其他长度的数字串
        int prefixLen = Math.min(3, digits.length() - 4);
        return digits.substring(0, prefixLen) + "****" + digits.substring(digits.length() - 4);
    }

    /**
     * 姓名脱敏
     * 格式：张** 或 张*三
     *
     * @param name 姓名
     * @return 脱敏后的姓名
     */
    public static String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        if (name.length() == 1) {
            return name.charAt(0) + "*";
        } else if (name.length() == 2) {
            return name.charAt(0) + "*";
        } else {
            StringBuilder masked = new StringBuilder();
            masked.append(name.charAt(0));
            for (int i = 1; i < name.length() - 1; i++) {
                masked.append("*");
            }
            masked.append(name.charAt(name.length() - 1));
            return masked.toString();
        }
    }

    /**
     * 身份证号脱敏
     * 格式：110***********1234
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return idCard;
        }

        if (idCard.length() < 8) {
            return idCard;
        }

        int prefixLen = 3;
        int suffixLen = 4;

        StringBuilder masked = new StringBuilder();
        masked.append(idCard.substring(0, prefixLen));
        for (int i = 0; i < idCard.length() - prefixLen - suffixLen; i++) {
            masked.append("*");
        }
        masked.append(idCard.substring(idCard.length() - suffixLen));

        return masked.toString();
    }

    /**
     * 银行卡号脱敏
     * 格式：6222****1234
     *
     * @param bankCard 银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.isEmpty()) {
            return bankCard;
        }

        // 移除所有空格
        String digits = bankCard.replace(" ", "");

        if (digits.length() < 8) {
            return bankCard;
        }

        return digits.substring(0, 4) + "****" + digits.substring(digits.length() - 4);
    }

    /**
     * 地址脱敏
     * 只保留省市，隐藏详细地址
     *
     * @param address 地址
     * @return 脱敏后的地址
     */
    public static String maskAddress(String address) {
        if (address == null || address.isEmpty()) {
            return address;
        }

        // 查找省市
        int cityIndex = Math.max(
                address.indexOf("市"),
                address.indexOf("县")
        );

        if (cityIndex > 0 && cityIndex < address.length() - 1) {
            return address.substring(0, cityIndex + 1) + "****";
        }

        // 找不到省市，隐藏后半部分
        int hideStart = address.length() / 2;
        return address.substring(0, hideStart) + "****";
    }

    /**
     * IP地址脱敏
     * 格式：192.168.*.*
     *
     * @param ip IP地址
     * @return 脱敏后的IP
     */
    public static String maskIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return ip;
        }

        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return ip;
        }

        return parts[0] + "." + parts[1] + ".*.*";
    }

    /**
     * 通用字符串脱敏
     * 保留首尾，中间用*替代
     *
     * @param str    原始字符串
     * @param prefix 保留的前缀长度
     * @param suffix 保留的后缀长度
     * @return 脱敏后的字符串
     */
    public static String mask(String str, int prefix, int suffix) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        if (str.length() <= prefix + suffix) {
            return str;
        }

        StringBuilder masked = new StringBuilder();
        if (prefix > 0) {
            masked.append(str.substring(0, prefix));
        }
        for (int i = 0; i < str.length() - prefix - suffix; i++) {
            masked.append("*");
        }
        if (suffix > 0) {
            masked.append(str.substring(str.length() - suffix));
        }

        return masked.toString();
    }
}