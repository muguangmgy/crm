package com.platform.common.aspect;

import com.platform.common.annotation.BloomCheck;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.common.service.BloomFilterService;
import com.platform.common.service.SpelKeyResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 布隆过滤器检查切面
 * Order=3，在Idempotent之后执行
 */
@Slf4j
@Aspect
@Component
@Order(3)
@RequiredArgsConstructor
public class BloomCheckAspect {

    private final SpelKeyResolver spelKeyResolver;
    private final BloomFilterService bloomFilterService;

    @Around("@annotation(bloomCheck)")
    public Object around(ProceedingJoinPoint point, BloomCheck bloomCheck) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();

        String bloomName = bloomCheck.bloomName();
        String value = spelKeyResolver.resolve(bloomCheck.valueExpression(), method, args);

        boolean exists = bloomFilterService.mightContain(bloomName, value);

        if (!exists) {
            log.warn("布隆过滤器拦截: bloomName={}, value={}", bloomName, value);
            throw new BusinessException(ResultCode.BLOOM_FILTER_ERROR, bloomCheck.message());
        }

        log.debug("布隆过滤器校验通过: bloomName={}, value={}", bloomName, value);

        return point.proceed();
    }
}