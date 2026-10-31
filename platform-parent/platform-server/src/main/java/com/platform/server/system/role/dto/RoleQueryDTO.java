package com.platform.server.system.role.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色查询条件")
public class RoleQueryDTO extends PageQuery {

    @Schema(description = "租户ID（平台管理员可指定）")
    private Long tenantId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}