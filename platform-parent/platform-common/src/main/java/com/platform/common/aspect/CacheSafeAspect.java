package com.platform.common.aspect;

import com.platform.common.annotation.CacheSafe;
import com.platform.common.constants.RedisConstants;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.common.service.RedisLockService;
import com.platform.common.service.SpelKeyResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 缓存防护切面
 * 防止缓存击穿和雪崩
 * Order=4，最后执行
 */
@Slf4j
@Aspect
@Component
@Order(4)
@RequiredArgsConstructor
public class CacheSafeAspect {

    private final SpelKeyResolver spelKeyResolver;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLockService redisLockService;

    @Around("@annotation(cacheSafe)")
    public Object around(ProceedingJoinPoint point, CacheSafe cacheSafe) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();

        String keySuffix = spelKeyResolver.resolve(cacheSafe.keyExpression(), method, args);
        String cacheKey = cacheSafe.cacheKeyPrefix() + ":" + keySuffix;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("缓存命中: key={}", cacheKey);
            return deserializeCachedValue(cached);
        }

        String lockKey = RedisConstants.CACHE_LOCK_KEY_PREFIX + cacheKey;

        LockResult lockResult = new LockResult();
        Object result = redisLockService.executeWithLock(
                lockKey,
                cacheSafe.lockWaitSeconds(),
                cacheSafe.lockLeaseSeconds(),
                () -> {
                    try {
                        Object value = redisTemplate.opsForValue().get(cacheKey);
                        if (value != null) {
                            log.debug("双重检查缓存命中: key={}", cacheKey);
                            lockResult.setDoubleCheckHit(true);
                            return deserializeCachedValue(value);
                        }

                        // 执行业务
                        Object data = point.proceed();

                        // 缓存值
                        cacheValue(cacheKey, data, cacheSafe);

                        // 返回结果
                        lockResult.setData(data);
                        return data;
                    } catch (BusinessException e) {
                        throw e;
                    } catch (Throwable e) {
                        log.error("缓存防护业务执行异常: key={}", cacheKey, e);
                        throw new RuntimeException(e);
                    }
                }
        );

        if (result == null && !lockResult.isDoubleCheckHit()) {
            Object fallback = redisTemplate.opsForValue().get(cacheKey);
            if (fallback != null) {
                log.debug("锁失败后缓存命中: key={}", cacheKey);
                return deserializeCachedValue(fallback);
            }

            if (lockResult.getData() != null) {
                return lockResult.getData();
            }

            log.info("缓存获取锁失败: key={}", cacheKey);
            throw new BusinessException(ResultCode.CACHE_LOCK_ERROR, "系统繁忙，请稍后重试");
        }

        return result;
    }

    /**
     * 缓存值
     *
     * @param cacheKey  缓存key
     * @param data      缓存数据
     * @param cacheSafe 缓存防护注解
     */
    private void cacheValue(String cacheKey, Object data, CacheSafe cacheSafe) {
        long baseTtl = cacheSafe.baseTtlSeconds();
        long offset = cacheSafe.ttlOffsetSeconds();

        if (offset < 0) {
            offset = 0;
        }

        long ttl;
        if (data == null || RedisConstants.isNullPlaceholder(data)) {
            ttl = Math.min(baseTtl, 60);
            redisTemplate.opsForValue().set(cacheKey, RedisConstants.NULL_PLACEHOLDER, ttl, TimeUnit.SECONDS);
            log.debug("缓存空值: key={}, ttl={}s", cacheKey, ttl);
        } else {
            ttl = baseTtl + ThreadLocalRandom.current().nextLong(offset + 1);
            redisTemplate.opsForValue().set(cacheKey, data, ttl, TimeUnit.SECONDS);
            log.debug("缓存写入: key={}, ttl={}s", cacheKey, ttl);
        }
    }

    /**
     * 反序列化缓存值
     *
     * @param cached 缓存值
     * @return 反序列化后的值
     */
    private Object deserializeCachedValue(Object cached) {
        if (RedisConstants.isNullPlaceholder(cached)) {
            return null;
        }
        return cached;
    }

    /**
     * 锁结果
     */
    private static class LockResult {
        private boolean doubleCheckHit = false;
        private Object data;

        public boolean isDoubleCheckHit() {
            return doubleCheckHit;
        }

        public void setDoubleCheckHit(boolean doubleCheckHit) {
            this.doubleCheckHit = doubleCheckHit;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}
