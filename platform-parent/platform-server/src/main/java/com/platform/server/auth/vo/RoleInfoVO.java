package com.platform.server.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色信息")
public class RoleInfoVO {

    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    @Schema(description = "角色编码", example = "admin")
    private String roleCode;

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    @Schema(description = "数据范围：1全部 2租户 3部门 4部门及下级 5本人", example = "2")
    private Integer dataScope;
}