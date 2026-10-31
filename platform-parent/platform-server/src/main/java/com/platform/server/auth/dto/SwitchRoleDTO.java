package com.platform.server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "角色切换请求")
public class SwitchRoleDTO {

    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
}