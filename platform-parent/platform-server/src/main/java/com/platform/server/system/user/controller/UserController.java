package com.platform.server.system.user.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.user.dto.ResetPasswordDTO;
import com.platform.server.system.user.dto.UserQueryDTO;
import com.platform.server.system.user.dto.UserSaveDTO;
import com.platform.server.system.user.service.SysUserService;
import com.platform.server.system.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理", description = "用户CRUD接口")
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService sysUserService;

    @Operation(summary = "用户列表", description = "分页查询用户列表，支持用户名、姓名、手机号、状态、部门筛选")
    @ApiResponse(responseCode = "200", description = "成功返回用户分页列表")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/list")
    public Result<PageVO<UserVO>> list(@Parameter(description = "查询条件") UserQueryDTO query) {
        return Result.success(sysUserService.getUserPage(query));
    }

    @Operation(summary = "用户详情", description = "根据用户ID查询用户详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回用户详情")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/{id}")
    public Result<UserVO> getById(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long id) {
        UserVO user = sysUserService.getUserById(id);
        // 查询用户角色
        user.setRoleIds(sysUserService.getRoleIds(id));
        return Result.success(user);
    }

    @Operation(summary = "新增用户", description = "创建新用户，密码使用BCrypt加密存储")
    @ApiResponse(responseCode = "200", description = "成功创建用户，返回用户ID")
    @PreAuthorize("hasAuthority('system:user:add')")
    @OperLog(module = "用户管理", operation = "新增", targetType = "用户", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "用户信息", required = true)
            @Valid @RequestBody UserSaveDTO dto) {
        return Result.success(sysUserService.saveUser(dto));
    }

    @Operation(summary = "修改用户", description = "更新用户基本信息，不修改密码")
    @ApiResponse(responseCode = "200", description = "成功修改用户信息")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @OperLog(module = "用户管理", operation = "修改", targetType = "用户", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "用户信息", required = true)
            @Valid @RequestBody UserSaveDTO dto) {
        sysUserService.updateUser(dto);
        return Result.success();
    }

    @Operation(summary = "删除用户", description = "根据用户ID删除用户（物理删除）")
    @ApiResponse(responseCode = "200", description = "成功删除用户")
    @PreAuthorize("hasAuthority('system:user:delete')")
    @OperLog(module = "用户管理", operation = "删除", targetType = "用户", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "重置密码", description = "重置用户密码，新密码使用BCrypt加密")
    @ApiResponse(responseCode = "200", description = "成功重置密码")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @OperLog(module = "用户管理", operation = "重置密码", targetType = "用户", targetIdExpr = "#dto.id")
    @PutMapping("/password")
    public Result<Void> resetPassword(
            @Parameter(description = "重置密码", required = true)
            @Valid @RequestBody ResetPasswordDTO dto) {
        sysUserService.resetPassword(dto);
        return Result.success();
    }

    @Operation(summary = "修改状态", description = "启用或禁用用户")
    @ApiResponse(responseCode = "200", description = "成功修改用户状态")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @OperLog(module = "用户管理", operation = "修改状态", targetType = "用户", targetIdExpr = "#dto.id")
    @PutMapping("/status")
    public Result<Void> updateStatus(
            @Parameter(description = "修改用户状态", required = true)
            @Valid @RequestBody UpdateStatusDto dto) {
        sysUserService.updateStatus(dto);
        return Result.success();
    }

    @Operation(summary = "用户角色ID列表", description = "获取用户已分配的角色ID列表")
    @ApiResponse(responseCode = "200", description = "成功返回角色ID列表")
    @GetMapping("/{userId}/roles")
    public Result<List<Long>> getRoleIds(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        return Result.success(sysUserService.getRoleIds(userId));
    }

    @Operation(summary = "分配用户角色", description = "为用户分配角色")
    @ApiResponse(responseCode = "200", description = "成功分配用户角色")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @OperLog(module = "用户管理", operation = "分配角色", targetType = "用户", targetIdExpr = "#userId")
    @PostMapping("/{userId}/roles")
    public Result<Void> assignRoles(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "角色ID列表", required = true)
            @RequestBody List<Long> roleIds) {
        sysUserService.assignRoles(userId, roleIds);
        return Result.success();
    }

    @Operation(summary = "用户选项", description = "获取启用状态的用户列表，用于下拉选择")
    @ApiResponse(responseCode = "200", description = "成功返回用户选项列表")
    @GetMapping("/options")
    public Result<List<UserVO>> getUserOptions() {
        return Result.success(sysUserService.getUserOptions());
    }
}