package com.platform.common.exception;

import com.platform.common.enums.ResultCode;
import lombok.Getter;

/**
 * 认证异常（未登录或登录过期）
 */
@Getter
public class AuthenticationException extends RuntimeException {

    private final int code;

    public AuthenticationException(String message) {
        super(message);
        this.code = 401;
    }

    public AuthenticationException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}