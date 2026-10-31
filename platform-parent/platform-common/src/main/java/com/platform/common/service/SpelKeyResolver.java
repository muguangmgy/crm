package com.platform.common.service;

import com.platform.common.context.UserContextHolder;
import com.platform.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SpEL表达式解析器
 * 用于解析注解中的SpEL表达式生成Redis key
 */
@Slf4j
@Component
public class SpelKeyResolver {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>(256);

    /**
     * 解析SpEL表达式
     *
     * @param expression SpEL表达式，如 "#order.orderNo"
     * @param method     当前执行的方法
     * @param args       方法参数值
     * @return 解析后的key值
     */
    public String resolve(String expression, Method method, Object[] args) {
        if (expression == null || expression.isEmpty()) {
            return "";
        }

        try {
            StandardEvaluationContext context = new StandardEvaluationContext();

            // 获取参数名并设置到上下文
            String[] paramNames = getParameterNames(method);
            for (int i = 0; i < args.length; i++) {
                if (paramNames != null && i < paramNames.length) {
                    context.setVariable(paramNames[i], args[i]);
                }
            }

            // 注入登录身份变量，便于缓存 key 按租户/用户/角色隔离
            context.setVariable("tenantId", UserContextHolder.getTenantId());
            context.setVariable("userId", UserContextHolder.getUserId());
            context.setVariable("roleId", UserContextHolder.getRoleId());
            context.setVariable("dataScope", UserContextHolder.getDataScope());
            context.setVariable("deptId", UserContextHolder.getDeptId());

            // 添加请求头变量支持，使用场景少
            //addRequestHeadersToContext(context);

            String[] expressions = expression.split(":");
            StringJoiner joiner = new StringJoiner(":");

            for (String expr : expressions) {
                String trimmedExpr = expr.trim();
                if (trimmedExpr.isEmpty()) {
                    continue;
                }
                Expression exp = expressionCache.computeIfAbsent(trimmedExpr, parser::parseExpression);
                Object value = exp.getValue(context);
                if (value != null) {
                    joiner.add(value.toString());
                }
            }

            return joiner.toString();
        } catch (Exception e) {
            log.error("SpEL表达式解析失败: {}, 表达式: {}", e.getMessage(), expression);
            throw new BusinessException("SpEL表达式解析失败: " + expression);
        }
    }

    /**
     * 将请求头添加到SpEL上下文
     * 使用方式: #headers['X-Device-Id'] 或 #header('Authorization')
     */
    private void addRequestHeadersToContext(StandardEvaluationContext context) {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // 将所有请求头放入Map中，方便访问
                Map<String, String> headerMap = new ConcurrentHashMap<>();
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    headerMap.put(headerName.toLowerCase(), request.getHeader(headerName));
                }
                // 通过 #headerMap 访问请求头（不区分大小写）
                context.setVariable("headerMap", headerMap);
            }
        } catch (Exception e) {
            log.debug("添加请求头到SpEL上下文失败: {}", e.getMessage());
        }
    }

    /**
     * 获取指定请求头的值（用于SpEL函数调用）
     */
    public static String getHeader(HttpServletRequest request, String headerName) {
        return request != null ? request.getHeader(headerName) : null;
    }


    /**
     * 生成完整的Redis key
     *
     * @param prefix  key前缀
     * @param spelKey SpEL解析后的key
     * @return 完整key，如 "platform:idempotent:1001:order_123456"
     */
    public String buildRedisKey(String prefix, String spelKey) {
        StringJoiner joiner = new StringJoiner(":");
        joiner.add(prefix);
        if (spelKey != null && !spelKey.isEmpty()) {
            joiner.add(spelKey);
        }
        return joiner.toString();
    }

    /**
     * 获取方法参数名
     */
    private String[] getParameterNames(Method method) {
        try {
            // Java 8+ 编译时加 -parameters 可直接获取
            return Arrays.stream(method.getParameters())
                    .map(p -> p.getName())
                    .toArray(String[]::new);
        } catch (Exception e) {
            log.warn("获取参数名失败: {}", e.getMessage());
            return null;
        }
    }
}