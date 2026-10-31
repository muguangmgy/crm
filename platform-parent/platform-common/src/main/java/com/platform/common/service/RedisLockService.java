package com.platform.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis分布式锁服务
 * 基于SETNX + Lua脚本实现，支持自动过期和防误删
 * 使用 StringRedisTemplate 保证 Lua 脚本参数正确传递
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 锁value前缀，用于标识锁持有者
     */
    private static final String LOCK_VALUE_PREFIX = "lock:";

    /**
     * 释放锁的Lua脚本：只有锁持有者才能释放
     */
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "    return redis.call('del', KEYS[1]) " +
                    "else " +
                    "    return 0 " +
                    "end";

    /**
     * 尝试获取锁
     *
     * @param lockKey      锁key
     * @param waitSeconds  等待时间（秒）
     * @param leaseSeconds 持锁时间（秒）
     * @return 锁标识（成功返回UUID，失败返回null）
     */
    public String tryLock(String lockKey, long waitSeconds, long leaseSeconds) {
        String lockValue = LOCK_VALUE_PREFIX + UUID.randomUUID().toString();
        long waitMs = waitSeconds * 1000;
        long leaseMs = leaseSeconds * 1000;
        long startTime = System.currentTimeMillis();
        int retryCount = 0;
        while (true) {
            // 尝试设置key（SETNX）
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockValue, leaseMs, TimeUnit.MILLISECONDS);

            if (Boolean.TRUE.equals(success)) {
                log.debug("获取锁成功: {}, value: {}", lockKey, lockValue);
                return lockValue;
            }

            // 检查是否超时
            if (System.currentTimeMillis() - startTime >= waitMs) {
                log.debug("获取锁超时: {}", lockKey);
                return null;
            }

            // 等待一段时间后重试
            try {
                retryCount++;
                long sleepTime = Math.min(50 * retryCount, 500);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }

    /**
     * 释放锁
     *
     * @param lockKey   锁key
     * @param lockValue 锁标识（用于验证持有者）
     */
    public void unlock(String lockKey, String lockValue) {
        if (lockValue == null) {
            return;
        }

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long result = redisTemplate.execute(script, Collections.singletonList(lockKey), lockValue);

        if (result != null && result == 1) {
            log.debug("释放锁成功: {}", lockKey);
        } else {
            log.warn("释放锁失败（非持有者或锁已过期）: {}", lockKey);
        }
    }

    /**
     * 在锁保护下执行任务
     *
     * @param lockKey      锁key
     * @param waitSeconds  等待时间
     * @param leaseSeconds 持锁时间
     * @param supplier     要执行的任务
     * @return 任务执行结果，获取锁失败返回null
     * @throws RuntimeException 业务执行异常
     */
    public <T> T executeWithLock(String lockKey, long waitSeconds, long leaseSeconds, Supplier<T> supplier) {
        String lockValue = tryLock(lockKey, waitSeconds, leaseSeconds);
        if (lockValue == null) {
            return null;
        }

        try {
            return supplier.get();
        } catch (Exception e) {
            log.error("锁内业务执行异常: {}, 锁标识: {}", lockKey, lockValue, e);
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        } finally {
            unlock(lockKey, lockValue);
        }
    }
}