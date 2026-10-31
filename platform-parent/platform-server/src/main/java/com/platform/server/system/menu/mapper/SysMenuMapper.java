package com.platform.server.system.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.server.system.menu.po.SysMenu;
import com.platform.server.system.menu.vo.MenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 多表关联查询：根据角色ID查询菜单
     */
    List<MenuVO> selectMenusByRoleId(@Param("roleId") Long roleId);

    List<String> selectPermissionByRoleId(@Param("roleId") Long roleId);

    List<String> selectPermissionByUserId(@Param("userId") Long userId);
}