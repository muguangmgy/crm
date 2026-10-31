package com.platform.common.aspect;

import com.platform.common.annotation.Idempotent;
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
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性控制切面
 * Order=2，在RepeatLimit之后执行
 * 使用 Lua 脚本保证分布式环境下原子性
 * 写入顺序：先缓存结果，再更新状态，确保看到done时结果一定存在
 * <p>
 * 请求体取值示例
 *
 * @Idempotent(keyExpression = "#loginDTO.userCode", expireSeconds = 5, message = "登录频繁")
 * <p>
 * 请求头取值示例
 * @Idempotent(keyExpression = "#authorization", expireSeconds = 30, message = "登录频繁")
 * @Idempotent(keyExpression = "#headerMap['authorization']", expireSeconds = 30, message = "登录频繁")
 */
@Slf4j
@Aspect
@Component
@Order(2)
@RequiredArgsConstructor
public class IdempotentAspect {

    private final SpelKeyResolver spelKeyResolver;
    private final StringRedisTemplate stringRedisTemplate;
    /**
     * 使用 GenericJackson2JsonRedisSerializer 保证类型信息完整
     */
    private final GenericJackson2JsonRedisSerializer jsonSerializer;

    private static final String RESULT_SUFFIX = ":result";

    /**
     * Lua脚本：原子性设置结果和状态
     * 返回值：1=成功，0=失败
     */
    private static final String SET_RESULT_SCRIPT =
            "local r1 = redis.call('SET', KEYS[2], ARGV[2], 'NX', 'EX', tonumber(ARGV[1])) " +
                    "if not r1 then return 0 end " +
                    "local r2 = redis.call('SET', KEYS[1], ARGV[3], 'EX', tonumber(ARGV[1])) " +
                    "if r2 then return 1 else return 0 end";

    /**
     * Lua脚本：原子性删除锁和结果
     */
    private static final String DELETE_SCRIPT =
            "redis.call('DEL', KEYS[1], KEYS[2])";

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint point, Idempotent idempotent) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();

        String spelKey = spelKeyResolver.resolve(idempotent.keyExpression(), method, args);
        String lockKey = spelKeyResolver.buildRedisKey(RedisConstants.IDEMPOTENT_KEY_PREFIX, spelKey);
        String resultKey = lockKey + RESULT_SUFFIX;

        // 尝试获取幂等锁（SETNX原子操作）
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "processing", idempotent.expireSeconds(), TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(success)) {
            log.debug("幂等key设置成功: key={}, expire={}s", lockKey, idempotent.expireSeconds());
            try {
                Object result = point.proceed();
                Object objResult = result;
                if (objResult == null) {
                    objResult = RedisConstants.NULL_PLACEHOLDER;
                }
                // 使用Lua脚本原子性设置结果和状态
                byte[] resultBytes = jsonSerializer.serialize(objResult);
                String resultJson = new String(resultBytes);
                DefaultRedisScript<Long> script = new DefaultRedisScript<>(SET_RESULT_SCRIPT, Long.class);
                Long luaResult = stringRedisTemplate.execute(script,
                        Arrays.asList(lockKey, resultKey),
                        String.valueOf(idempotent.expireSeconds()),
                        resultJson,
                        "done");
                if (luaResult == null || luaResult != 1) {
                    throw new BusinessException(ResultCode.IDEMPOTENT_ERROR, "幂等结果缓存失败");
                }
                log.debug("幂等结果缓存成功: key={}", lockKey);
                return result;
            } catch (Throwable e) {
                // 执行失败，Lua脚本原子性删除锁和结果
                DefaultRedisScript<Void> delScript = new DefaultRedisScript<>(DELETE_SCRIPT);
                stringRedisTemplate.execute(delScript, Arrays.asList(lockKey, resultKey));
                throw e;
            }
        }

        // 第二次请求：检查是否需要返回缓存结果
        if (idempotent.returnCached()) {
            // 等待第一次请求完成（最多等待waitSeconds秒）
            Object cached = waitForDoneResult(lockKey, resultKey, idempotent.waitSeconds());
            if (cached != null) {
                log.debug("幂等返回缓存结果: key={}", lockKey);
                if (RedisConstants.isNullPlaceholder(cached)) {
                    return null;
                }
                return cached;
            }
            log.warn("幂等等待超时，结果获取失败: key={}", lockKey);
        }

        log.warn("幂等校验拦截: key={}", lockKey);
        throw new BusinessException(ResultCode.IDEMPOTENT_ERROR, idempotent.message());
    }

    /**
     * 轮询等待第一次请求完成并获取结果
     * Lua脚本保证看到done时结果一定存在
     */
    private Object waitForDoneResult(String lockKey, String resultKey, int maxWaitSeconds) {
        long startTime = System.currentTimeMillis();
        long waitMs = maxWaitSeconds * 1000;

        while (System.currentTimeMillis() - startTime < waitMs) {
            String status = stringRedisTemplate.opsForValue().get(lockKey);
            if ("done".equals(status)) {
                // 状态为done，获取JSON结果并反序列化（保留类型信息）
                String resultJson = stringRedisTemplate.opsForValue().get(resultKey);
                if (resultJson != null) {
                    try {
                        return jsonSerializer.deserialize(resultJson.getBytes());
                    } catch (Exception e) {
                        log.error("幂等结果反序列化失败: key={}", lockKey, e);
                        throw new BusinessException(ResultCode.IDEMPOTENT_ERROR, "幂等结果获取失败");
                    }
                }
            }
            // 等待100ms后重试
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }
}