package com.platform.server.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 拦截器配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "interceptor.login")
public class InterceptorConfig {

    /**
     * 是否启用单点登录控制
     */
    private Boolean singleLogin;

    /**
     * accessToekn过期时间 (分钟)
     */
    private Integer accessTokenExpireTime;

    /**
     * refreshToekn过期时间 (天)
     */
    private Integer refreshTokenExpireTime;

    /**
     * 拦截路径
     */
    private List<String> pathPatterns;

    /**
     * 排除路径
     */
    private List<String> excludePatterns;
}