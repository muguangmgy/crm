package com.platform.server.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.enums.ResultCode;
import com.platform.common.enums.StatusEnum;
import com.platform.common.exception.BusinessException;
import com.platform.common.utils.PasswordUtil;
import com.platform.server.auth.security.UserPermissionService;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.dept.mapper.SysDeptMapper;
import com.platform.server.system.dept.po.SysDept;
import com.platform.server.system.role.mapper.SysUserRoleMapper;
import com.platform.server.system.role.po.SysUserRole;
import com.platform.server.system.user.dto.ResetPasswordDTO;
import com.platform.server.system.user.dto.UserQueryDTO;
import com.platform.server.system.user.dto.UserSaveDTO;
import com.platform.server.system.user.mapper.SysUserMapper;
import com.platform.server.system.user.po.SysUser;
import com.platform.server.system.user.service.SysUserService;
import com.platform.server.system.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final UserPermissionService userPermissionService;

    @Override
    public SysUser getByUserCode(String userCode) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserCode, userCode);
        return sysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    @Override
    public PageVO<UserVO> getUserPage(UserQueryDTO query) {
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<UserVO> result = sysUserMapper.selectUserPage(page, query);
        return PageVO.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public UserVO getUserById(Long id) {
        return sysUserMapper.selectUserById(id);
    }

    @Override
    public Long saveUser(UserSaveDTO dto) {
        // 部门必须选择
        if (dto.getDeptId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "部门不能为空");
        }

        // 获取部门信息，用户的租户与部门一致
        SysDept dept = sysDeptMapper.selectById(dto.getDeptId());
        if (dept == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "选择的部门不存在");
        }
        Long tenantId = dept.getTenantId();

        SysUser user = new SysUser();
        user.setTenantId(tenantId);
        user.setUserCode(dto.getUserCode());
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        user.setGender(dto.getGender());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        user.setDeptId(dto.getDeptId());
        sysUserMapper.insert(user);
        // 分配角色
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), dto.getRoleIds());
        }
        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(UserSaveDTO dto) {
        SysUser user = new SysUser();
        user.setId(dto.getId());
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        user.setGender(dto.getGender());
        user.setStatus(dto.getStatus());
        user.setDeptId(dto.getDeptId());
        sysUserMapper.updateById(user);
        // 更新角色
        assignRoles(dto.getId(), dto.getRoleIds());
    }

    @Override
    public void deleteUser(Long id) {
        sysUserMapper.deleteById(id);
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        SysUser user = new SysUser();
        user.setId(dto.getId());
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        sysUserMapper.updateById(user);
    }

    @Override
    public void updateStatus(UpdateStatusDto dto) {
        SysUser user = new SysUser();
        user.setId(dto.getId());
        user.setStatus(dto.getStatus());
        sysUserMapper.updateById(user);
    }

    @Override
    public List<Long> getRoleIds(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        return sysUserRoleMapper.selectList(wrapper).stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 删除旧的角色关联
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleMapper.delete(wrapper);
        // 新增角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = roleIds.stream()
                    .map(roleId -> new SysUserRole(userId, roleId))
                    .collect(Collectors.toList());
            userRoles.forEach(sysUserRoleMapper::insert);
        }
        // 清除用户权限缓存
        userPermissionService.clearUserPermissionCache(userId);
    }

    @Override
    public List<UserVO> getUserOptions() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getStatus, StatusEnum.ENABLED.getCode());
        wrapper.orderByAsc(SysUser::getId);
        return sysUserMapper.selectList(wrapper).stream()
                .map(user -> {
                    UserVO vo = new UserVO();
                    vo.setId(user.getId());
                    vo.setUserCode(user.getUserCode());
                    vo.setUserName(user.getUserName());
                    vo.setDeptId(user.getDeptId());
                    return vo;
                })
                .collect(Collectors.toList());
    }
}