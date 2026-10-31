package com.platform.server.system.role.service;

import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.role.dto.RoleQueryDTO;
import com.platform.server.system.role.dto.RoleSaveDTO;
import com.platform.server.system.role.vo.RoleVO;

import java.util.List;

public interface SysRoleService {

    PageVO<RoleVO> getRolePage(RoleQueryDTO query);

    RoleVO getRoleById(Long id);

    Long saveRole(RoleSaveDTO dto);

    void updateRole(RoleSaveDTO dto);

    void deleteRole(Long id);

    void updateStatus(UpdateStatusDto dto);

    List<RoleVO> getRoleOptions();

    /**
     * 获取角色已分配的菜单ID列表
     */
    List<Long> getMenuIds(Long roleId);

    /**
     * 分配角色菜单权限
     */
    void assignMenus(Long roleId, List<Long> menuIds);
}