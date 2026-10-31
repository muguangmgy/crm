package com.platform.server.system.config.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.config.dto.ConfigQueryDTO;
import com.platform.server.system.config.dto.ConfigSaveDTO;
import com.platform.server.system.config.service.SysConfigService;
import com.platform.server.system.config.vo.ConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "业务参数管理", description = "业务参数CRUD接口")
@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class ConfigController {

    private final SysConfigService sysConfigService;

    @Operation(summary = "参数列表", description = "分页查询业务参数列表")
    @ApiResponse(responseCode = "200", description = "成功返回参数分页列表")
    @PreAuthorize("hasAuthority('system:config:list')")
    @GetMapping("/list")
    public Result<PageVO<ConfigVO>> list(@Parameter(description = "查询条件") ConfigQueryDTO query) {
        return Result.success(sysConfigService.getConfigPage(query));
    }

    @Operation(summary = "参数详情", description = "根据ID查询参数详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回参数详情")
    @PreAuthorize("hasAuthority('system:config:list')")
    @GetMapping("/{id}")
    public Result<ConfigVO> getById(
            @Parameter(description = "参数ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(sysConfigService.getConfigById(id));
    }

    @Operation(summary = "新增参数", description = "创建新业务参数")
    @ApiResponse(responseCode = "200", description = "成功创建参数")
    @PreAuthorize("hasAuthority('system:config:add')")
    @OperLog(module = "参数管理", operation = "新增", targetType = "参数", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "参数信息", required = true)
            @Valid @RequestBody ConfigSaveDTO dto) {
        return Result.success(sysConfigService.saveConfig(dto));
    }

    @Operation(summary = "修改参数", description = "更新业务参数信息")
    @ApiResponse(responseCode = "200", description = "成功修改参数")
    @PreAuthorize("hasAuthority('system:config:edit')")
    @OperLog(module = "参数管理", operation = "修改", targetType = "参数", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "参数信息", required = true)
            @Valid @RequestBody ConfigSaveDTO dto) {
        sysConfigService.updateConfig(dto);
        return Result.success();
    }

    @Operation(summary = "删除参数", description = "根据ID删除业务参数")
    @ApiResponse(responseCode = "200", description = "成功删除参数")
    @PreAuthorize("hasAuthority('system:config:delete')")
    @OperLog(module = "参数管理", operation = "删除", targetType = "参数", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "参数ID", required = true, example = "1")
            @PathVariable Long id) {
        sysConfigService.deleteConfig(id);
        return Result.success();
    }

    @Operation(summary = "修改状态", description = "启用或禁用参数")
    @ApiResponse(responseCode = "200", description = "成功修改状态")
    @PreAuthorize("hasAuthority('system:config:edit')")
    @OperLog(module = "参数管理", operation = "修改状态", targetType = "参数", targetIdExpr = "#dto.id")
    @PutMapping("/status")
    public Result<Void> updateStatus(
            @Parameter(description = "状态信息", required = true)
            @Valid @RequestBody UpdateStatusDto dto) {
        sysConfigService.updateStatus(dto);
        return Result.success();
    }

    @Operation(summary = "根据参数键获取参数", description = "获取参数值（租户优先，无需权限）")
    @ApiResponse(responseCode = "200", description = "成功返回参数值")
    @GetMapping("/by-key/{configKey}")
    public Result<ConfigVO> getByKey(
            @Parameter(description = "参数键", required = true, example = "site_name")
            @PathVariable String configKey) {
        return Result.success(sysConfigService.getConfigByKey(configKey));
    }

    @Operation(summary = "根据分组获取参数列表", description = "获取分组参数列表（租户优先，无需权限）")
    @ApiResponse(responseCode = "200", description = "成功返回参数列表")
    @GetMapping("/by-group/{groupCode}")
    public Result<List<ConfigVO>> getByGroup(
            @Parameter(description = "分组编码", required = true, example = "basic")
            @PathVariable String groupCode) {
        return Result.success(sysConfigService.getConfigByGroup(groupCode));
    }
}