package com.platform.server.system.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.constants.PlatformConstants;
import com.platform.common.constants.RedisConstants;
import com.platform.common.context.UserContextHolder;
import com.platform.common.utils.JsonUtil;
import com.platform.server.auth.security.UserPermissionService;
import com.platform.server.common.config.InterceptorConfig;
import com.platform.server.common.converter.SystemConverter;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.menu.dto.MenuQueryDTO;
import com.platform.server.system.menu.dto.MenuSaveDTO;
import com.platform.server.system.menu.mapper.SysMenuMapper;
import com.platform.server.system.menu.po.SysMenu;
import com.platform.server.system.menu.service.SysMenuService;
import com.platform.server.system.menu.vo.MenuVO;
import com.platform.server.system.role.mapper.SysRoleMapper;
import com.platform.server.system.role.po.SysRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SystemConverter systemConverter;
    private final UserPermissionService userPermissionService;
    private final SysRoleMapper sysRoleMapper;
    private final StringRedisTemplate redisTemplate;
    private final InterceptorConfig interceptorConfig;

    @Override
    public PageVO<MenuVO> getMenuPage(MenuQueryDTO query) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (query.getMenuName() != null) {
            wrapper.like(SysMenu::getMenuName, query.getMenuName());
        }
        if (query.getMenuType() != null) {
            wrapper.eq(SysMenu::getMenuType, query.getMenuType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysMenu::getStatus, query.getStatus());
        }
        // 非平台用户只能看到visible=1的菜单
        if (!UserContextHolder.isPlatformRole() || PlatformConstants.TENANT_ADMIN_ROLE_CODE.equals(UserContextHolder.getRoleCode())) {
            wrapper.eq(SysMenu::getVisible, 1);
        }
        wrapper.orderByAsc(SysMenu::getSort);

        Page<SysMenu> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SysMenu> result = sysMenuMapper.selectPage(page, wrapper);

        List<MenuVO> list = systemConverter.toMenuVOList(result.getRecords());
        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public List<MenuVO> getMenuTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        // 非平台用户只能看到visible=1的菜单
        if (!UserContextHolder.isPlatformRole() || PlatformConstants.TENANT_ADMIN_ROLE_CODE.equals(UserContextHolder.getRoleCode())) {
            wrapper.eq(SysMenu::getVisible, 1);
        }
        wrapper.orderByAsc(SysMenu::getSort, SysMenu::getId);
        List<SysMenu> allMenus = sysMenuMapper.selectList(wrapper);
        List<MenuVO> menuVOList = systemConverter.toMenuVOList(allMenus);
        return buildTree(menuVOList, 0L);
    }

    @Override
    public List<MenuVO> getUserMenus(Long roleId) {
        Long userId = UserContextHolder.getUserId();
        Long currentRoleId = UserContextHolder.getRoleId();
        if (userId == null || currentRoleId == null) {
            return new ArrayList<>();
        }
        if (roleId == null) {
            roleId = currentRoleId;
        }

        //缓存最新角色到redis
        SysRole sysRole = sysRoleMapper.getRoleById(roleId);
        redisTemplate.opsForValue().set(RedisConstants.REDIS_USER_ROLE_KEY + userId, JsonUtil.toJson(sysRole),
                interceptorConfig.getAccessTokenExpireTime(), TimeUnit.MINUTES);

        List<MenuVO> userMenus = sysMenuMapper.selectMenusByRoleId(roleId);
        return buildTree(userMenus, 0L);
    }

    @Override
    public MenuVO getMenuById(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        return systemConverter.toMenuVO(menu);
    }

    @Override
    public Long saveMenu(MenuSaveDTO dto) {
        SysMenu menu = new SysMenu();
        menu.setParentId(dto.getParentId());
        menu.setMenuName(dto.getMenuName());
        menu.setMenuType(dto.getMenuType());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setPerms(dto.getPerms());
        menu.setIcon(dto.getIcon());
        menu.setSort(dto.getSort() != null ? dto.getSort() : 0);
        menu.setVisible(dto.getVisible() != null ? dto.getVisible() : 1);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        sysMenuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    public void updateMenu(MenuSaveDTO dto) {
        SysMenu menu = new SysMenu();
        menu.setId(dto.getId());
        menu.setParentId(dto.getParentId());
        menu.setMenuName(dto.getMenuName());
        menu.setMenuType(dto.getMenuType());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setPerms(dto.getPerms());
        menu.setIcon(dto.getIcon());
        menu.setSort(dto.getSort());
        menu.setVisible(dto.getVisible());
        menu.setStatus(dto.getStatus());
        sysMenuMapper.updateById(menu);
        // 清除该菜单关联角色的用户权限缓存
        userPermissionService.clearMenuUsersPermissionCache(dto.getId());
    }

    @Override
    public void deleteMenu(Long id) {
        // 清除该菜单关联角色的用户权限缓存
        userPermissionService.clearMenuUsersPermissionCache(id);
        sysMenuMapper.deleteById(id);
    }

    private List<MenuVO> buildTree(List<MenuVO> menus, Long parentId) {
        List<MenuVO> tree = new ArrayList<>();
        for (MenuVO menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                // 递归构建子菜单
                menu.setChildren(buildTree(menus, menu.getId()));
                tree.add(menu);
            }
        }
        // 按sort排序
        tree.sort((a, b) -> (a.getSort() != null ? a.getSort() : 0) - (b.getSort() != null ? b.getSort() : 0));
        return tree;
    }
}