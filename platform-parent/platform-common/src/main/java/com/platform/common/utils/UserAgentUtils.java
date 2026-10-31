package com.platform.common.utils;

/**
 * @ClassName: UserAgentUtils
 * @Description: userAgent工具类
 * @Author: waynevip@foxmail.com
 * @Date: 2026/04/21 20:46
 */

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

public class UserAgentUtils {

    private static final UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer
            .newBuilder()
            .hideMatcherLoadStats()
            .withCache(10000)
            .build();

    /**
     * 解析浏览器名称
     */
    public static String parseBrowser(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }
        UserAgent agent = userAgentAnalyzer.parse(userAgent);
        return agent.getValue(UserAgent.AGENT_NAME);
    }

    /**
     * 解析操作系统
     */
    public static String parseOs(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }
        UserAgent agent = userAgentAnalyzer.parse(userAgent);
        return agent.getValue(UserAgent.OPERATING_SYSTEM_NAME);
    }
}