package com.platform.server.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "验证码响应")
public class CaptchaVO {

    @Schema(description = "验证码图片Base64", example = "data:image/svg+xml;base64,...")
    private String captchaImage;
}