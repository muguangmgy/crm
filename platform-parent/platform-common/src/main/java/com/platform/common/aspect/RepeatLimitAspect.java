package com.platform.common.aspect;

import com.platform.common.annotation.RepeatLimit;
import com.platform.common.constants.RedisConstants;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.common.service.SpelKeyResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 防重复提交切面
 * Order=1，最先执行
 * 使用 StringRedisTemplate 保证原子性
 */
@Slf4j
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class RepeatLimitAspect {

    private final SpelKeyResolver spelKeyResolver;
    private final StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(repeatLimit)")
    public Object around(ProceedingJoinPoint point, RepeatLimit repeatLimit) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();

        String spelKey = spelKeyResolver.resolve(repeatLimit.keyExpression(), method, args);
        String redisKey = spelKeyResolver.buildRedisKey(RedisConstants.REPEAT_LIMIT_KEY_PREFIX, spelKey);

        // 使用 setIfAbsent 原子性操作，避免竞态条件
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(redisKey, "1", repeatLimit.intervalSeconds(), TimeUnit.SECONDS);

        if (!Boolean.TRUE.equals(success)) {
            log.warn("重复提交拦截: key={}", redisKey);
            throw new BusinessException(ResultCode.REPEAT_LIMIT_ERROR, repeatLimit.message());
        }

        log.debug("防重复提交key设置成功: key={}, expire={}s", redisKey, repeatLimit.intervalSeconds());
        return point.proceed();
    }
}