package com.platform.server.auth.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.platform.common.constants.RedisConstants;
import com.platform.common.enums.ResultCode;
import com.platform.common.enums.StatusEnum;
import com.platform.common.exception.BusinessException;
import com.platform.common.utils.JsonUtil;
import com.platform.server.common.config.InterceptorConfig;
import com.platform.server.system.dept.mapper.SysDeptMapper;
import com.platform.server.system.dept.po.SysDept;
import com.platform.server.system.menu.mapper.SysMenuMapper;
import com.platform.server.system.role.mapper.SysRoleMapper;
import com.platform.server.system.role.mapper.SysRoleMenuMapper;
import com.platform.server.system.role.mapper.SysUserRoleMapper;
import com.platform.server.system.role.po.SysRole;
import com.platform.server.system.role.po.SysRoleMenu;
import com.platform.server.system.role.po.SysUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户权限加载服务
 */
@Service
@RequiredArgsConstructor
public class UserPermissionService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysDeptMapper sysDeptMapper;
    private final StringRedisTemplate redisTemplate;
    private final InterceptorConfig interceptorConfig;

    /**
     * 获取角色所有权限标识（优先从缓存读取）
     */
    public Set<String> getUserPermissions(Long userId) {
        // 先从缓存读取
        String cacheKey = RedisConstants.REDIS_USER_PERMISSIONS_KEY + userId;
        Set<String> cachedPermissions = redisTemplate.opsForSet().members(cacheKey);

        if (cachedPermissions != null && !cachedPermissions.isEmpty()) {
            return cachedPermissions;
        }

        // 缓存不存在，从数据库加载
        Set<String> permissions = loadUserPermissionsFromDB(userId);

        // 存入缓存，与Token同步过期
        if (!permissions.isEmpty()) {
            redisTemplate.opsForSet().add(cacheKey, permissions.toArray(new String[0]));
            redisTemplate.expire(cacheKey, interceptorConfig.getAccessTokenExpireTime(), TimeUnit.MINUTES);
        }

        return permissions;
    }

    /**
     * 从数据库加载用户权限
     */
    private Set<String> loadUserPermissionsFromDB(Long userId) {
        // 查询用户所有角色菜单的权限标识
        List<String> perms = sysMenuMapper.selectPermissionByUserId(userId);
        return buildMenuPermissions(perms);
    }


    /**
     * 获取角色所有权限标识（优先从缓存读取）
     */
    public Set<String> getRolePermissions(Long roleId) {
        // 先从缓存读取
        String cacheKey = RedisConstants.REDIS_ROLE_PERMISSIONS_KEY + roleId;
        Set<String> cachedPermissions = redisTemplate.opsForSet().members(cacheKey);

        if (cachedPermissions != null && !cachedPermissions.isEmpty()) {
            return cachedPermissions;
        }

        // 缓存不存在，从数据库加载
        Set<String> permissions = loadRolePermissionsFromDB(roleId);

        // 存入缓存
        if (!permissions.isEmpty()) {
            redisTemplate.opsForSet().add(cacheKey, permissions.toArray(new String[0]));
            redisTemplate.expire(cacheKey, RedisConstants.ROLE_PERMISSIONS_EXPIRE_HOURS, TimeUnit.HOURS);
        }
        return permissions;
    }

    /**
     * 从数据库加载角色权限
     */
    private Set<String> loadRolePermissionsFromDB(Long roleId) {
        Set<String> permissions = new HashSet<>();
        // 查询角色菜单的权限标识
        List<String> perms = sysMenuMapper.selectPermissionByRoleId(roleId);
        return buildMenuPermissions(perms);
    }

    /**
     * 构建菜单权限标识
     */
    private static Set<String> buildMenuPermissions(List<String> perms) {
        Set<String> permissions = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.hasText(perm)) {
                // perms可能包含多个权限，用逗号分隔
                String[] permsArr = perm.split(",");
                for (String permStr : permsArr) {
                    if (!permStr.trim().isEmpty()) {
                        permissions.add(permStr.trim());
                    }
                }
            }
        }
        return permissions;
    }


    /**
     * 获取用户所有角色列表（完整信息）
     */
    public List<SysRole> getUserRoles(Long userId) {
        return sysRoleMapper.getUserRoles(userId);
    }

    /**
     * 获取用户当前角色信息
     */
    public SysRole getUserCurrentRole(Long userId) {
        String currentRole = redisTemplate.opsForValue().get(RedisConstants.REDIS_USER_ROLE_KEY + userId);
        SysRole role = null;
        if (StringUtils.hasText(currentRole)) {
            role = JsonUtil.fromJson(currentRole, SysRole.class);
        } else {
            List<SysRole> roles = getUserRoles(userId);
            if (roles == null || roles.isEmpty()) {
                throw new BusinessException(ResultCode.USER_ROLE_NOT_EXIST);
            }
            role = roles.get(0);
            redisTemplate.opsForValue().set(RedisConstants.REDIS_USER_ROLE_KEY + userId, JsonUtil.toJson(role),
                    interceptorConfig.getAccessTokenExpireTime(), TimeUnit.MINUTES);
        }
        return role;
    }

    /**
     * 清除指定角色的权限缓存
     */
    public void clearRolePermissionCache(Long roleId) {
        String cacheKey = RedisConstants.REDIS_ROLE_PERMISSIONS_KEY + roleId;
        redisTemplate.delete(cacheKey);
    }

    /**
     * 清除用户权限缓存
     */
    public void clearUserPermissionCache(Long userId) {
        String permissionsKey = RedisConstants.REDIS_USER_PERMISSIONS_KEY + userId;
        String rolesKey = RedisConstants.REDIS_USER_ROLE_KEY + userId;
        redisTemplate.delete(permissionsKey);
        redisTemplate.delete(rolesKey);
    }


    /**
     * 获取部门及子部门ID
     */
    public String getDeptIds(Long deptId) {
        if (deptId == null) {
            return null;
        }

        // 先从缓存读取
        String cacheKey = RedisConstants.REDIS_DEPT_IDS_KEY + deptId;
        String deptIds = redisTemplate.opsForValue().get(cacheKey);

        if (deptIds != null && !deptIds.isEmpty()) {
            return deptIds;
        }

        // 缓存不存在，从数据库加载
        SysDept dept = sysDeptMapper.selectById(deptId);

        if (dept != null) {
            //查询所有子部门
            LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysDept::getTenantId, dept.getTenantId());
            wrapper.eq(SysDept::getStatus, StatusEnum.ENABLED.getCode());
            wrapper.likeRight(SysDept::getDeptPath, dept.getDeptPath());
            List<SysDept> depts = sysDeptMapper.selectList(wrapper);
            //将所有子部门ID拼接成字符串
            deptIds = depts.stream()
                    .map(SysDept::getId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

        }

        // 存入缓存
        if (deptIds != null) {
            redisTemplate.opsForValue().set(cacheKey, deptIds, RedisConstants.DEPT_IDS_EXPIRE_MILLERS, TimeUnit.MINUTES);
        }
        return deptIds;
    }

    /**
     * 清除指定角色下所有用户的权限缓存
     */
    public void clearRoleUsersPermissionCache(Long roleId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getRoleId, roleId);
        List<Long> userIds = sysUserRoleMapper.selectList(wrapper).stream()
                .map(SysUserRole::getUserId)
                .collect(Collectors.toList());

        for (Long userId : userIds) {
            clearUserPermissionCache(userId);
        }
    }

    /**
     * 清除指定菜单关联角色的用户权限缓存
     */
    public void clearMenuUsersPermissionCache(Long menuId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getMenuId, menuId);
        List<Long> roleIds = sysRoleMenuMapper.selectList(wrapper).stream()
                .map(SysRoleMenu::getRoleId)
                .distinct()
                .collect(Collectors.toList());

        for (Long roleId : roleIds) {
            clearRoleUsersPermissionCache(roleId);
        }
    }
}