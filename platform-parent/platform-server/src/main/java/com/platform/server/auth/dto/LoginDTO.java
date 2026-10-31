package com.platform.server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户登录请求")
public class LoginDTO {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String userCode;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "abcd")
    @NotBlank(message = "验证码不能为空")
    private String captcha;

    @Schema(description = "记住我", example = "false")
    private Boolean rememberMe = false;
}