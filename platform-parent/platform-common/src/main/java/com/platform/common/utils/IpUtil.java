package com.platform.common.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP资源类
 */
public final class IpUtil {


    /**
     * 获取设备信息（简化版本）
     */
    public static String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            return "unknown";
        }
        // 截取前100字符，避免过长
        return userAgent.length() > 100 ? userAgent.substring(0, 100) : userAgent;
    }

    /**
     * 获取客户端真实IP
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isBlank(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isBlank(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isBlank(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        // IPv6本地回环地址转换为IPv4格式
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    /**
     * 检查IP是否匹配（支持通配符，如 192.168.1.*）
     */
    public static boolean ipMatch(String pattern, String ip) {
        if (pattern == null || ip == null) {
            return false;
        }
        if (pattern.equals(ip)) {
            return true;
        }
        // 支持通配符匹配
        if (pattern.contains("*")) {
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            return ip.matches(regex);
        }
        return false;
    }

    private static boolean isBlank(String str) {
        return str == null || str.isEmpty() || "unknown".equalsIgnoreCase(str);
    }
}
