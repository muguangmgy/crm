package com.platform.server.aspect;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.annotation.OperLog;
import com.platform.common.context.UserContextHolder;
import com.platform.common.service.SpelKeyResolver;
import com.platform.common.utils.IpUtil;
import com.platform.common.utils.JsonUtil;
import com.platform.server.system.log.oper.po.SysOperLog;
import com.platform.server.system.log.oper.service.SysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 * Order=10，在其他业务切面之后执行，确保记录完整的操作结果
 */
@Slf4j
@Aspect
@Component
@Order(10)
@RequiredArgsConstructor
public class OperLogAspect {

    private final SysOperLogService sysOperLogService;
    private final SpelKeyResolver spelKeyResolver;

    private static final int MAX_REQUEST_DATA_LENGTH = 2000;

    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint point, OperLog operLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();

        SysOperLog logEntity = new SysOperLog();
        logEntity.setModule(operLog.module());
        logEntity.setOperation(operLog.operation());
        logEntity.setTargetType(operLog.targetType());
        logEntity.setOperTime(LocalDateTime.now());

        // 设置用户信息
        logEntity.setTenantId(UserContextHolder.getTenantId());
        logEntity.setUserCode(UserContextHolder.getUserCode());

        // 设置IP地址
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logEntity.setIpAddress(IpUtil.getClientIp(request));
        }

        // 序列化请求参数
        String requestData = serializeArgs(args);
        if (requestData.length() > MAX_REQUEST_DATA_LENGTH) {
            requestData = requestData.substring(0, MAX_REQUEST_DATA_LENGTH) + "...";
        }
        logEntity.setRequestData(requestData);

        Object result = null;
        try {
            result = point.proceed();
            logEntity.setResponseStatus(1);

            // 解析 targetId（从返回结果或参数中提取）
            if (!operLog.targetIdExpr().isEmpty()) {
                resolveTargetId(logEntity, operLog.targetIdExpr(), result, method, args);
            }
        } catch (Throwable e) {
            logEntity.setResponseStatus(0);
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logEntity.setDuration((int) duration);

            // 异步保存日志
            try {
                sysOperLogService.saveOperLog(logEntity);
            } catch (Exception e) {
                log.error("操作日志保存失败: module={}, operation={}", operLog.module(), operLog.operation(), e);
            }
        }

        return result;
    }

    /**
     * 序列化请求参数
     */
    private String serializeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        try {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object arg : args) {
                if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                    continue;
                }
                if (!first) {
                    sb.append(",");
                }
                sb.append(JsonUtil.toJson(arg));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            log.warn("请求参数序列化失败", e);
            return "[序列化失败]";
        }
    }

    /**
     * 解析 targetId
     */
    private void resolveTargetId(SysOperLog logEntity, String expr, Object result, Method method, Object[] args) {
        try {
            Object targetId;
            if (expr.startsWith("#result")) {
                // 从返回结果提取
                targetId = resolveFromResult(expr, result);
            } else {
                // 从参数提取
                String targetIdStr = spelKeyResolver.resolve(expr, method, args);
                targetId = targetIdStr;
            }

            if (targetId instanceof Long) {
                logEntity.setTargetId((Long) targetId);
            } else if (targetId instanceof Number) {
                logEntity.setTargetId(((Number) targetId).longValue());
            } else if (targetId != null) {
                try {
                    logEntity.setTargetId(Long.parseLong(targetId.toString()));
                } catch (NumberFormatException e) {
                    log.warn("targetId非数字类型: {}", targetId);
                }
            }
        } catch (Exception e) {
            log.warn("targetId SpEL表达式解析失败: {}", expr, e);
        }
    }

    /**
     * 从返回结果解析 SpEL 表达式
     */
    private Object resolveFromResult(String expr, Object result) {
        try {
            // 支持 #result.data 或 #result.id 格式
            String propertyPath = expr.substring("#result.".length());
            return BeanUtil.getProperty(result, propertyPath);
        } catch (Exception e) {
            log.warn("从返回结果解析失败: expr={}, result={}", expr, result, e);
            return null;
        }
    }
}