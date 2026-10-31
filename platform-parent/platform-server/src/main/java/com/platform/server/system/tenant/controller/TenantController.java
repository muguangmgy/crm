package com.platform.server.system.tenant.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.tenant.dto.TenantQueryDTO;
import com.platform.server.system.tenant.dto.TenantSaveDTO;
import com.platform.server.system.tenant.service.SysTenantService;
import com.platform.server.system.tenant.vo.TenantVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "租户管理", description = "租户CRUD接口（平台管理员）")
@RestController
@RequestMapping("/api/system/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final SysTenantService sysTenantService;

    @Operation(summary = "租户列表", description = "分页查询租户列表，支持租户名称、租户编码、状态筛选")
    @ApiResponse(responseCode = "200", description = "成功返回租户分页列表")
    @PreAuthorize("hasAuthority('system:tenant:list')")
    @GetMapping("/list")
    public Result<PageVO<TenantVO>> list(@Parameter(description = "查询条件") TenantQueryDTO query) {
        return Result.success(sysTenantService.getTenantPage(query));
    }

    @Operation(summary = "租户详情", description = "根据租户ID查询租户详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回租户详情")
    @PreAuthorize("hasAuthority('system:tenant:list')")
    @GetMapping("/{id}")
    public Result<TenantVO> getById(
            @Parameter(description = "租户ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(sysTenantService.getTenantById(id));
    }

    @Operation(summary = "新增租户", description = "创建新租户")
    @ApiResponse(responseCode = "200", description = "成功创建租户，返回租户ID")
    @PreAuthorize("hasAuthority('system:tenant:add')")
    @OperLog(module = "租户管理", operation = "新增", targetType = "租户", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "租户信息", required = true)
            @Valid @RequestBody TenantSaveDTO dto) {
        return Result.success(sysTenantService.saveTenant(dto));
    }

    @Operation(summary = "修改租户", description = "更新租户基本信息")
    @ApiResponse(responseCode = "200", description = "成功修改租户信息")
    @PreAuthorize("hasAuthority('system:tenant:edit')")
    @OperLog(module = "租户管理", operation = "修改", targetType = "租户", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "租户信息", required = true)
            @Valid @RequestBody TenantSaveDTO dto) {
        sysTenantService.updateTenant(dto);
        return Result.success();
    }

    @Operation(summary = "删除租户", description = "根据租户ID删除租户")
    @ApiResponse(responseCode = "200", description = "成功删除租户")
    @PreAuthorize("hasAuthority('system:tenant:delete')")
    @OperLog(module = "租户管理", operation = "删除", targetType = "租户", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "租户ID", required = true, example = "1")
            @PathVariable Long id) {
        sysTenantService.deleteTenant(id);
        return Result.success();
    }

    @Operation(summary = "修改状态", description = "启用或禁用租户")
    @ApiResponse(responseCode = "200", description = "成功修改租户状态")
    @PreAuthorize("hasAuthority('system:tenant:edit')")
    @OperLog(module = "租户管理", operation = "修改状态", targetType = "租户", targetIdExpr = "#dto.id")
    @PutMapping("/status")
    public Result<Void> updateStatus(
            @Parameter(description = "修改租户状态", required = true)
            @Valid @RequestBody UpdateStatusDto dto) {
        sysTenantService.updateStatus(dto);
        return Result.success();
    }

    @Operation(summary = "租户下拉选项", description = "获取启用状态的租户列表，用于下拉选择")
    @ApiResponse(responseCode = "200", description = "成功返回租户选项列表")
    @GetMapping("/options")
    public Result<List<TenantVO>> getOptions() {
        return Result.success(sysTenantService.getTenantOptions());
    }

    @Operation(summary = "延期租户", description = "延长租户有效期")
    @ApiResponse(responseCode = "200", description = "成功延期租户")
    @PreAuthorize("hasAuthority('system:tenant:extend')")
    @OperLog(module = "租户管理", operation = "延期", targetType = "租户", targetIdExpr = "#id")
    @PutMapping("/{id}/extend")
    public Result<Void> extend(
            @Parameter(description = "租户ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "延期天数", required = true, example = "30")
            @RequestParam Integer days) {
        sysTenantService.extendTenant(id, days);
        return Result.success();
    }
}