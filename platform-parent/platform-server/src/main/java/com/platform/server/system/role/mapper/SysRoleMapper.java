package com.platform.server.system.role.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.system.role.po.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @InterceptorIgnore(tenantLine = "true")
    List<SysRole> getUserRoles(Long userId);

    @InterceptorIgnore(tenantLine = "true")
    SysRole getRoleById(Long id);
}