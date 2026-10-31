package com.platform.server.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.annotation.DataPermissionIgnore;
import com.platform.common.annotation.DataScope;
import com.platform.server.system.user.dto.UserQueryDTO;
import com.platform.server.system.user.po.SysUser;
import com.platform.server.system.user.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @DataScope(deptAlias = "u", userAlias = "u")
    IPage<UserVO> selectUserPage(Page<SysUser> page, @Param("query") UserQueryDTO query);

    @DataPermissionIgnore
    UserVO selectUserById(@Param("id") Long id);

    @DataPermissionIgnore
    SysUser selectById(@Param("id") Long id);
}