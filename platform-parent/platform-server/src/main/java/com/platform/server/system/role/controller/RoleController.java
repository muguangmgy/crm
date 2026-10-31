package com.platform.server.system.role.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.role.dto.RoleQueryDTO;
import com.platform.server.system.role.dto.RoleSaveDTO;
import com.platform.server.system.role.service.SysRoleService;
import com.platform.server.system.role.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理", description = "角色CRUD接口")
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final SysRoleService sysRoleService;

    @Operation(summary = "角色列表", description = "分页查询角色列表，支持角色名称、角色编码、状态筛选")
    @ApiResponse(responseCode = "200", description = "成功返回角色分页列表")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/list")
    public Result<PageVO<RoleVO>> list(@Parameter(description = "查询条件") RoleQueryDTO query) {
        return Result.success(sysRoleService.getRolePage(query));
    }

    @Operation(summary = "角色详情", description = "根据角色ID查询角色详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回角色详情")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/{id}")
    public Result<RoleVO> getById(
            @Parameter(description = "角色ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(sysRoleService.getRoleById(id));
    }

    @Operation(summary = "新增角色", description = "创建新角色")
    @ApiResponse(responseCode = "200", description = "成功创建角色，返回角色ID")
    @PreAuthorize("hasAuthority('system:role:add')")
    @OperLog(module = "角色管理", operation = "新增", targetType = "角色", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "角色信息", required = true)
            @Valid @RequestBody RoleSaveDTO dto) {
        return Result.success(sysRoleService.saveRole(dto));
    }

    @Operation(summary = "修改角色", description = "更新角色基本信息")
    @ApiResponse(responseCode = "200", description = "成功修改角色信息")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @OperLog(module = "角色管理", operation = "修改", targetType = "角色", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "角色信息", required = true)
            @Valid @RequestBody RoleSaveDTO dto) {
        sysRoleService.updateRole(dto);
        return Result.success();
    }

    @Operation(summary = "删除角色", description = "根据角色ID删除角色")
    @ApiResponse(responseCode = "200", description = "成功删除角色")
    @PreAuthorize("hasAuthority('system:role:delete')")
    @OperLog(module = "角色管理", operation = "删除", targetType = "角色", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "角色ID", required = true, example = "1")
            @PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }

    @Operation(summary = "修改状态", description = "启用或禁用角色")
    @ApiResponse(responseCode = "200", description = "成功修改角色状态")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @OperLog(module = "角色管理", operation = "修改状态", targetType = "角色", targetIdExpr = "#dto.id")
    @PutMapping("/status")
    public Result<Void> updateStatus(
            @Parameter(description = "修改角色状态", required = true)
            @Valid @RequestBody UpdateStatusDto dto) {
        sysRoleService.updateStatus(dto);
        return Result.success();
    }

    @Operation(summary = "角色下拉选项", description = "获取启用状态的角色列表，用于下拉选择")
    @ApiResponse(responseCode = "200", description = "成功返回角色选项列表")
    @GetMapping("/options")
    public Result<List<RoleVO>> getOptions() {
        return Result.success(sysRoleService.getRoleOptions());
    }

    @Operation(summary = "角色菜单ID列表", description = "获取角色已分配的菜单ID列表")
    @ApiResponse(responseCode = "200", description = "成功返回菜单ID列表")
    @GetMapping("/{roleId}/menus")
    public Result<List<Long>> getMenuIds(
            @Parameter(description = "角色ID", required = true, example = "1")
            @PathVariable Long roleId) {
        return Result.success(sysRoleService.getMenuIds(roleId));
    }

    @Operation(summary = "分配角色菜单", description = "为角色分配菜单权限")
    @ApiResponse(responseCode = "200", description = "成功分配角色菜单权限")
    @PreAuthorize("hasAuthority('system:role:permission')")
    @OperLog(module = "角色管理", operation = "分配权限", targetType = "角色", targetIdExpr = "#roleId")
    @PostMapping("/{roleId}/menus")
    public Result<Void> assignMenus(
            @Parameter(description = "角色ID", required = true, example = "1")
            @PathVariable Long roleId,
            @Parameter(description = "菜单ID列表", required = true)
            @RequestBody List<Long> menuIds) {
        sysRoleService.assignMenus(roleId, menuIds);
        return Result.success();
    }
}