package com.platform.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.common.utils.IpUtil;
import com.platform.common.utils.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 请求日志AOP切面
 * <p>
 * 执行顺序：在FastDfsAntiStealAspect之后执行，确保日志记录的是带防盗链token的完整URL
 */
@Aspect
@Component
@Order(2)
@Slf4j
public class RequestLogAspect {

    private final ObjectMapper objectMapper = JsonUtil.getObjectMapper();

    @Pointcut("execution(* com.platform..controller..*.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Object result = null;
        int status = 200;
        try {
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            status = 500;
            throw e;
        } finally {
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                long responseTime = System.currentTimeMillis() - startTime;
                String clientIp = IpUtil.getClientIp(request);
                String requestUrl = request.getRequestURI();
                String requestMethod = request.getMethod();

                log.info("[{}] {} {} - {}ms",
                        requestMethod,
                        requestUrl,
                        clientIp,
                        responseTime);


                // 记录请求参数（限制长度避免过大）
                Object[] args = point.getArgs();
                if (args != null && args.length > 0) {
                    String params = serializeArgs(args);
                    log.debug(">>>> --- 请求报文：{}", params);
                    if (params.length() > 2000) {
                        params = params.substring(0, 2000);
                    }
                }

                // 记录响应（限制长度避免过大）
                if (result != null) {
                    String responseBody = objectMapper.writeValueAsString(result);
                    log.debug("<<<< --- 返回报文：{}", responseBody);
                }


            }
        }
    }

    private String serializeArgs(Object[] args) {
        try {
            // 过滤掉HttpServletRequest/Response等不可序列化的参数
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object arg : args) {
                if (arg instanceof HttpServletRequest
                        || arg instanceof jakarta.servlet.http.HttpServletResponse) {
                    continue;
                }
                if (!first) {
                    sb.append(",");
                }
                sb.append(objectMapper.writeValueAsString(arg));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

}
