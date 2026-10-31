package com.platform.server.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "当前用户信息响应")
public class UserInfoVO {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String userCode;

    @Schema(description = "姓名", example = "管理员")
    private String userName;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "租户ID", example = "1")
    private Long tenantId;

    @Schema(description = "租户编码", example = "PLATFORM")
    private String tenantCode;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "角色列表")
    private List<RoleInfoVO> roles;

    @Schema(description = "当前角色ID", example = "1")
    private Long currentRoleId;

    @Schema(description = "权限标识列表")
    private List<String> permissions;

    @Schema(description = "是否平台角色")
    private Boolean isPlatformRole;
}