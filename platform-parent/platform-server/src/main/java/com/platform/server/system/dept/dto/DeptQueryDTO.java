package com.platform.server.system.dept.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门查询条件")
public class DeptQueryDTO extends PageQuery {

    @Schema(description = "租户ID（平台管理员可指定）")
    private Long tenantId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}