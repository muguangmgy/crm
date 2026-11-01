package com.platform.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 分布式锁服务（基于 Redisson）
 * <p>
 * 支持可重入、自动续期（Watchdog）、安全释放。
 * leaseSeconds &lt;= 0 时启用 Watchdog 自动续期；&gt; 0 时使用固定租约。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final RedissonClient redissonClient;

    /**
     * 尝试获取锁
     *
     * @param lockKey      锁 key
     * @param waitSeconds  等待时间（秒）
     * @param leaseSeconds 持锁时间（秒），&lt;=0 表示启用 Watchdog 自动续期
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitSeconds, long leaseSeconds) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean acquired;
            if (leaseSeconds <= 0) {
                // Watchdog 模式：持锁期间自动续期
                acquired = lock.tryLock(waitSeconds, TimeUnit.SECONDS);
            } else {
                acquired = lock.tryLock(waitSeconds, leaseSeconds, TimeUnit.SECONDS);
            }
            if (acquired) {
                log.debug("获取锁成功: {}, leaseSeconds={}", lockKey, leaseSeconds);
            } else {
                log.debug("获取锁超时: {}", lockKey);
            }
            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("获取锁被中断: {}", lockKey);
            return false;
        }
    }

    /**
     * 释放当前线程持有的锁
     *
     * @param lockKey 锁 key
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.debug("释放锁成功: {}", lockKey);
        } else {
            log.warn("释放锁跳过（非持有者或锁已失效）: {}", lockKey);
        }
    }

    /**
     * 在锁保护下执行任务
     *
     * @param lockKey      锁 key
     * @param waitSeconds  等待时间（秒）
     * @param leaseSeconds 持锁时间（秒），&lt;=0 表示启用 Watchdog 自动续期
     * @param supplier     要执行的任务
     * @return 任务执行结果，获取锁失败返回 null
     */
    public <T> T executeWithLock(String lockKey, long waitSeconds, long leaseSeconds, Supplier<T> supplier) {
        if (!tryLock(lockKey, waitSeconds, leaseSeconds)) {
            return null;
        }
        try {
            return supplier.get();
        } catch (Exception e) {
            log.error("锁内业务执行异常: {}", lockKey, e);
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 在 Watchdog 锁保护下执行任务（自动续期，适合耗时不确定的业务）
     *
     * @param lockKey     锁 key
     * @param waitSeconds 等待时间（秒）
     * @param supplier    要执行的任务
     * @return 任务执行结果，获取锁失败返回 null
     */
    public <T> T executeWithLock(String lockKey, long waitSeconds, Supplier<T> supplier) {
        return executeWithLock(lockKey, waitSeconds, -1, supplier);
    }
}
