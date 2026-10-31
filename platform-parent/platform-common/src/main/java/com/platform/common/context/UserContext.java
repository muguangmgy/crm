package com.platform.common.context;

import com.platform.common.constants.PlatformConstants;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserContext {

    private Long userId;
    private String userCode;
    private Long tenantId;
    private String tenantCode;
    private Long deptId;
    private Long roleId;
    private String roleCode;
    /**
     * 数据权限范围：1全部 2租户 3部门 4部门及下级 5本人 6自定义
     */
    private Integer dataScope;
    /**
     * 部门ID及子部门ID列表
     */
    private String deptIds;


    /**
     * 判断是否平台角色
     */
    public boolean isPlatformRole() {
        return roleCode != null && roleCode.toUpperCase().startsWith(PlatformConstants.PLATFORM_ROLE_CODE_PREFIX);
    }
}