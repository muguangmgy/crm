package com.platform.server.system.user.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询条件")
public class UserQueryDTO extends PageQuery {

    @Schema(description = "租户ID（平台管理员可指定）")
    private Long tenantId;

    @Schema(description = "用户名")
    private String userCode;

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "部门路径")
    private String deptPath;

    @Schema(description = "创建时间起始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间截止")
    private LocalDateTime createTimeEnd;
}