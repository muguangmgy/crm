package com.platform.server.crm.common.aspect;

import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.server.crm.common.enums.PoolTypeEnum;
import com.platform.server.crm.customer.mapper.CustomerMapper;
import com.platform.server.crm.customer.po.CrmCustomer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 客户归属权限切面
 * 校验私有池客户是否属于当前用户
 */
@Aspect
@Component
@Order(5)
@RequiredArgsConstructor
public class CustomerOwnerAspect {

    private final CustomerMapper customerMapper;

    /**
     * 在标记了@CustomerOwner注解的方法执行前校验客户归属
     */
    @Before("@annotation(customerOwner)")
    public void checkOwner(JoinPoint point, CustomerOwner customerOwner) {
        Long customerId = extractCustomerId(point);
        if (customerId == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "客户ID不能为空");
        }

        Long currentUserId = UserContextHolder.getUserId();
        if (currentUserId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户未登录");
        }

        CrmCustomer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "客户不存在");
        }

        // 只有私有池客户需要校验归属
        if (customer.getPoolType() == PoolTypeEnum.PRIVATE.getCode()) {
            // 需要校验是否是负责人
            if (customerOwner.requireOwner()) {
                if (!currentUserId.equals(customer.getOwnerId())) {
                    throw new BusinessException("无权限操作此客户");
                }
            }
        }
    }

    /**
     * 从方法参数中提取客户ID
     */
    private Long extractCustomerId(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = point.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName();
            // 尝试从参数名中识别客户ID
            if (paramName.equals("customerId") || paramName.equals("id")) {
                Object arg = args[i];
                if (arg instanceof Long) {
                    return (Long) arg;
                }
            }
            // 从DTO中提取客户ID
            if (args[i] != null) {
                try {
                    Method getIdMethod = args[i].getClass().getMethod("getId");
                    Object id = getIdMethod.invoke(args[i]);
                    if (id instanceof Long) {
                        return (Long) id;
                    }
                } catch (Exception ignored) {
                }
                try {
                    Method getCustomerIdMethod = args[i].getClass().getMethod("getCustomerId");
                    Object customerId = getCustomerIdMethod.invoke(args[i]);
                    if (customerId instanceof Long) {
                        return (Long) customerId;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }
}