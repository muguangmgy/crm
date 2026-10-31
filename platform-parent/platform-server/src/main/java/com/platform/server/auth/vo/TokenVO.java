package com.platform.server.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token响应")
public class TokenVO {

    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "过期时间戳（毫秒）", example = "1712345678900")
    private Long expireTime;

    @Schema(description = "租户ID", example = "1")
    private Long tenantId;
}