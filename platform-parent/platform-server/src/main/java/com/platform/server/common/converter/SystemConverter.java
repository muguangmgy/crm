package com.platform.server.common.converter;

import com.platform.server.auth.vo.RoleInfoVO;
import com.platform.server.system.config.po.SysConfig;
import com.platform.server.system.config.vo.ConfigVO;
import com.platform.server.system.dept.po.SysDept;
import com.platform.server.system.dept.vo.DeptVO;
import com.platform.server.system.dictdata.po.SysDictData;
import com.platform.server.system.dictdata.vo.DictDataVO;
import com.platform.server.system.dicttype.po.SysDictType;
import com.platform.server.system.dicttype.vo.DictTypeVO;
import com.platform.server.system.menu.po.SysMenu;
import com.platform.server.system.menu.vo.MenuVO;
import com.platform.server.system.role.po.SysRole;
import com.platform.server.system.role.vo.RoleVO;
import com.platform.server.system.tenant.po.SysTenant;
import com.platform.server.system.tenant.vo.TenantVO;
import com.platform.server.system.user.po.SysUser;
import com.platform.server.system.user.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 系统模块统一转换器
 */
@Mapper(componentModel = "spring")
public interface SystemConverter {

    // ========== 用户模块 ==========
    UserVO toUserVO(SysUser user);

    List<UserVO> toUserVOList(List<SysUser> users);

    // ========== 角色模块 ==========
    RoleVO toRoleVO(SysRole role);

    List<RoleVO> toRoleVOList(List<SysRole> roles);

    // SysRole.id -> RoleInfoVO.roleId 映射
    @Mapping(source = "id", target = "roleId")
    RoleInfoVO toRoleInfoVO(SysRole role);

    List<RoleInfoVO> toRoleInfoVOList(List<SysRole> roles);

    // ========== 菜单模块 ==========
    MenuVO toMenuVO(SysMenu menu);

    List<MenuVO> toMenuVOList(List<SysMenu> menus);

    // ========== 部门模块 ==========
    DeptVO toDeptVO(SysDept dept);

    List<DeptVO> toDeptVOList(List<SysDept> depts);

    // ========== 租户模块 ==========
    TenantVO toTenantVO(SysTenant tenant);

    List<TenantVO> toTenantVOList(List<SysTenant> tenants);

    // ========== 字典类型模块 ==========
    DictTypeVO toDictTypeVO(SysDictType dictType);

    List<DictTypeVO> toDictTypeVOList(List<SysDictType> dictTypes);

    // ========== 字典项模块 ==========
    DictDataVO toDictDataVO(SysDictData dictData);

    List<DictDataVO> toDictDataVOList(List<SysDictData> dictDatas);

    // ========== 参数模块 ==========
    ConfigVO toConfigVO(SysConfig config);

    List<ConfigVO> toConfigVOList(List<SysConfig> configs);
}