package com.platform.server.system.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "角色保存请求")
public class RoleSaveDTO {

    @Schema(description = "角色ID（修改时必填）")
    private Long id;

    @Schema(description = "租户ID（平台用户创建角色时必填）", example = "1")
    private Long tenantId;

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理员")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称最多50字符")
    private String roleName;

    @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 30, message = "角色编码最多30字符")
    private String roleCode;

    @Schema(description = "数据范围：1全部 2租户 3部门 4部门及下级 5本人", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "数据范围不能为空")
    private Integer dataScope;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "系统管理员角色")
    @Size(max = 200, message = "备注最多200字符")
    private String remark;
}