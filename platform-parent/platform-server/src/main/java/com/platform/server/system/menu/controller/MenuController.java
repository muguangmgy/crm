package com.platform.server.system.menu.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.menu.dto.MenuQueryDTO;
import com.platform.server.system.menu.dto.MenuSaveDTO;
import com.platform.server.system.menu.service.SysMenuService;
import com.platform.server.system.menu.vo.MenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理", description = "菜单CRUD接口")
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final SysMenuService sysMenuService;

    @Operation(summary = "菜单列表", description = "分页查询菜单列表，支持菜单名称、菜单类型、状态筛选")
    @ApiResponse(responseCode = "200", description = "成功返回菜单分页列表")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/list")
    public Result<PageVO<MenuVO>> list(@Parameter(description = "查询条件") MenuQueryDTO query) {
        return Result.success(sysMenuService.getMenuPage(query));
    }

    @Operation(summary = "菜单树", description = "获取完整菜单树结构")
    @ApiResponse(responseCode = "200", description = "成功返回菜单树")
    @GetMapping("/tree")
    public Result<List<MenuVO>> getTree() {
        return Result.success(sysMenuService.getMenuTree());
    }

    @Operation(summary = "用户菜单", description = "根据当前角色获取用户菜单树")
    @ApiResponse(responseCode = "200", description = "成功返回菜单树")
    @GetMapping("/userMenus")
    public Result<List<MenuVO>> getUserMenus(
            @Parameter(description = "角色ID（可选）") @RequestParam(required = false) Long roleId) {
        return Result.success(sysMenuService.getUserMenus(roleId));
    }

    @Operation(summary = "菜单详情", description = "根据菜单ID查询菜单详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回菜单详情")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/{id}")
    public Result<MenuVO> getById(
            @Parameter(description = "菜单ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(sysMenuService.getMenuById(id));
    }

    @Operation(summary = "新增菜单", description = "创建新菜单")
    @ApiResponse(responseCode = "200", description = "成功创建菜单，返回菜单ID")
    @PreAuthorize("hasAuthority('system:menu:add')")
    @OperLog(module = "菜单管理", operation = "新增", targetType = "菜单", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "菜单信息", required = true)
            @Valid @RequestBody MenuSaveDTO dto) {
        return Result.success(sysMenuService.saveMenu(dto));
    }

    @Operation(summary = "修改菜单", description = "更新菜单基本信息")
    @ApiResponse(responseCode = "200", description = "成功修改菜单信息")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @OperLog(module = "菜单管理", operation = "修改", targetType = "菜单", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "菜单信息", required = true)
            @Valid @RequestBody MenuSaveDTO dto) {
        sysMenuService.updateMenu(dto);
        return Result.success();
    }

    @Operation(summary = "删除菜单", description = "根据菜单ID删除菜单")
    @ApiResponse(responseCode = "200", description = "成功删除菜单")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @OperLog(module = "菜单管理", operation = "删除", targetType = "菜单", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "菜单ID", required = true, example = "1")
            @PathVariable Long id) {
        sysMenuService.deleteMenu(id);
        return Result.success();
    }
}