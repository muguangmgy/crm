package com.platform.server.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.enums.StatusEnum;
import com.platform.common.exception.BusinessException;
import com.platform.server.auth.security.UserPermissionService;
import com.platform.server.common.constants.SecurityConstants;
import com.platform.server.common.converter.SystemConverter;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.role.dto.RoleQueryDTO;
import com.platform.server.system.role.dto.RoleSaveDTO;
import com.platform.server.system.role.mapper.SysRoleMapper;
import com.platform.server.system.role.mapper.SysRoleMenuMapper;
import com.platform.server.system.role.mapper.SysUserRoleMapper;
import com.platform.server.system.role.po.SysRole;
import com.platform.server.system.role.po.SysRoleMenu;
import com.platform.server.system.role.service.SysRoleService;
import com.platform.server.system.role.vo.RoleVO;
import com.platform.server.system.tenant.mapper.SysTenantMapper;
import com.platform.server.system.tenant.po.SysTenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SystemConverter systemConverter;
    private final UserPermissionService userPermissionService;
    private final SysTenantMapper sysTenantMapper;

    @Override
    public PageVO<RoleVO> getRolePage(RoleQueryDTO query) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (query.getTenantId() != null) {
            wrapper.eq(SysRole::getTenantId, query.getTenantId());
        }
        if (query.getRoleName() != null) {
            wrapper.like(SysRole::getRoleName, query.getRoleName());
        }
        if (query.getRoleCode() != null) {
            wrapper.like(SysRole::getRoleCode, query.getRoleCode());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(SysRole::getId);

        Page<SysRole> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SysRole> result = sysRoleMapper.selectPage(page, wrapper);

        List<RoleVO> list = systemConverter.toRoleVOList(result.getRecords());
        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public RoleVO getRoleById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        return systemConverter.toRoleVO(role);
    }

    @Override
    public Long saveRole(RoleSaveDTO dto) {
        boolean isPlatformRole = UserContextHolder.isPlatformRole();

        // 非平台用户不能选择全部数据权限(1)
        if (!isPlatformRole && dto.getDataScope() == 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "非平台角色用户不能选择全部数据权限");
        }

        // 租户ID：平台用户可选择租户，非平台用户使用当前用户的租户ID
        Long tenantId;
        if (isPlatformRole && dto.getTenantId() != null) {
            tenantId = dto.getTenantId();
        } else {
            tenantId = UserContextHolder.getTenantId();
            if (tenantId == null) {
                tenantId = SecurityConstants.DEFAULT_TENANT_ID;
            }
        }

        // 查询租户编码，拼接角色编码前缀
        SysTenant tenant = sysTenantMapper.selectById(tenantId);
        if (tenant == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }
        String tenantCode = tenant.getTenantCode();
        String roleCodePrefix = tenantCode + "_";

        // 自动拼接角色编码前缀
        String roleCode = dto.getRoleCode();
        if (!roleCode.startsWith(roleCodePrefix)) {
            roleCode = roleCodePrefix + roleCode;
        }

        SysRole role = new SysRole();
        role.setTenantId(tenantId);
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(roleCode);
        role.setDataScope(dto.getDataScope());
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        role.setRemark(dto.getRemark());
        sysRoleMapper.insert(role);
        return role.getId();
    }

    @Override
    public void updateRole(RoleSaveDTO dto) {
        boolean isPlatformRole = UserContextHolder.isPlatformRole();

        // 非平台用户不能选择全部数据权限(1)
        if (!isPlatformRole && dto.getDataScope() == 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "非平台角色用户不能选择全部数据权限");
        }

        // 查询租户编码，拼接角色编码前缀
        SysTenant tenant = sysTenantMapper.selectById(dto.getTenantId());
        if (tenant == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }
        String tenantCode = tenant.getTenantCode();
        String roleCodePrefix = tenantCode + "_";

        // 自动拼接角色编码前缀
        String roleCode = dto.getRoleCode();
        if (!roleCode.startsWith(roleCodePrefix)) {
            roleCode = roleCodePrefix + roleCode;
        }

        SysRole role = new SysRole();
        role.setId(dto.getId());
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(roleCode);
        role.setDataScope(dto.getDataScope());
        role.setStatus(dto.getStatus());
        role.setRemark(dto.getRemark());
        sysRoleMapper.updateById(role);
    }

    @Override
    public void deleteRole(Long id) {
        // 清除该角色下所有用户的权限缓存
        userPermissionService.clearRoleUsersPermissionCache(id);
        sysRoleMapper.deleteById(id);
    }

    @Override
    public void updateStatus(UpdateStatusDto dto) {
        SysRole role = new SysRole();
        role.setId(dto.getId());
        role.setStatus(dto.getStatus());
        sysRoleMapper.updateById(role);
        // 禁用角色时清除缓存
        if (dto.getStatus() == 0) {
            userPermissionService.clearRoleUsersPermissionCache(dto.getId());
        }
    }

    @Override
    public List<RoleVO> getRoleOptions() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, StatusEnum.ENABLED.getCode());
        wrapper.orderByAsc(SysRole::getId);
        return systemConverter.toRoleVOList(sysRoleMapper.selectList(wrapper));
    }

    @Override
    public List<Long> getMenuIds(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        return sysRoleMenuMapper.selectList(wrapper).stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 删除旧的菜单关联
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        sysRoleMenuMapper.delete(wrapper);
        // 新增菜单关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<SysRoleMenu> roleMenus = menuIds.stream()
                    .map(menuId -> new SysRoleMenu(roleId, menuId))
                    .collect(Collectors.toList());
            roleMenus.forEach(sysRoleMenuMapper::insert);
        }
        // 清除该角色的权限缓存
        userPermissionService.clearRolePermissionCache(roleId);
        // 清除该角色下所有用户的权限缓存
        userPermissionService.clearRoleUsersPermissionCache(roleId);
    }
}