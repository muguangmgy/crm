package com.platform.server.system.menu.service;

import com.platform.server.common.vo.PageVO;
import com.platform.server.system.menu.dto.MenuQueryDTO;
import com.platform.server.system.menu.dto.MenuSaveDTO;
import com.platform.server.system.menu.vo.MenuVO;

import java.util.List;

public interface SysMenuService {

    PageVO<MenuVO> getMenuPage(MenuQueryDTO query);

    List<MenuVO> getMenuTree();

    List<MenuVO> getUserMenus(Long roleId);

    MenuVO getMenuById(Long id);

    Long saveMenu(MenuSaveDTO dto);

    void updateMenu(MenuSaveDTO dto);

    void deleteMenu(Long id);
}