package com.platform.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Redisson 配置
 * 独立于 Spring Data Redis（Lettuce），仅用于分布式锁等场景
 */
@Configuration
public class RedissonConfig {

    /**
     * 锁看门狗超时时间（毫秒），默认 30 秒
     * 持锁期间会按该时间的 1/3 自动续期
     */
    private static final long LOCK_WATCHDOG_TIMEOUT_MS = 30_000L;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        config.setLockWatchdogTimeout(LOCK_WATCHDOG_TIMEOUT_MS);

        boolean ssl = redisProperties.getSsl() != null && redisProperties.getSsl().isEnabled();
        String scheme = ssl ? "rediss://" : "redis://";
        String address = scheme + redisProperties.getHost() + ":" + redisProperties.getPort();

        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(address)
                .setDatabase(redisProperties.getDatabase());

        if (StringUtils.hasText(redisProperties.getUsername())) {
            serverConfig.setUsername(redisProperties.getUsername());
        }
        if (StringUtils.hasText(redisProperties.getPassword())) {
            serverConfig.setPassword(redisProperties.getPassword());
        }
        if (redisProperties.getTimeout() != null) {
            int timeoutMs = (int) redisProperties.getTimeout().toMillis();
            serverConfig.setTimeout(timeoutMs);
            serverConfig.setConnectTimeout(timeoutMs);
        }

        return Redisson.create(config);
    }
}
