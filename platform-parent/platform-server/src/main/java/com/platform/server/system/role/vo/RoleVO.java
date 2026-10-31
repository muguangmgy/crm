package com.platform.server.system.role.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "角色信息响应")
public class RoleVO {

    @Schema(description = "角色ID", example = "1")
    private Long id;

    @Schema(description = "租户ID", example = "1")
    private Long tenantId;

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    @Schema(description = "角色编码", example = "admin")
    private String roleCode;

    @Schema(description = "数据范围：1全部 2租户 3部门 4部门及下级 5本人 6自定义", example = "2")
    private Integer dataScope;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}