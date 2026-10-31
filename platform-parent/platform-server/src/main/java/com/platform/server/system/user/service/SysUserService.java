package com.platform.server.system.user.service;

import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.user.dto.ResetPasswordDTO;
import com.platform.server.system.user.dto.UserQueryDTO;
import com.platform.server.system.user.dto.UserSaveDTO;
import com.platform.server.system.user.po.SysUser;
import com.platform.server.system.user.vo.UserVO;

import java.util.List;

public interface SysUserService {

    /**
     * 根据用户名查询
     */
    SysUser getByUserCode(String userCode);

    void updateLastLoginTime(Long userId);

    PageVO<UserVO> getUserPage(UserQueryDTO query);

    UserVO getUserById(Long id);

    Long saveUser(UserSaveDTO dto);

    void updateUser(UserSaveDTO dto);

    void deleteUser(Long id);

    void resetPassword(ResetPasswordDTO dto);

    void updateStatus(UpdateStatusDto dto);

    /**
     * 获取用户已分配的角色ID列表
     */
    List<Long> getRoleIds(Long userId);

    /**
     * 分配用户角色
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取启用用户列表（用于下拉选择）
     */
    List<UserVO> getUserOptions();
}