package com.platform.server.system.dept.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.dept.dto.DeptQueryDTO;
import com.platform.server.system.dept.dto.DeptSaveDTO;
import com.platform.server.system.dept.service.SysDeptService;
import com.platform.server.system.dept.vo.DeptVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理", description = "部门CRUD接口")
@RestController
@RequestMapping("/api/system/dept")
@RequiredArgsConstructor
public class DeptController {

    private final SysDeptService sysDeptService;

    @Operation(summary = "部门列表", description = "分页查询部门列表，支持部门名称、状态筛选")
    @ApiResponse(responseCode = "200", description = "成功返回部门分页列表")
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/list")
    public Result<PageVO<DeptVO>> list(@Parameter(description = "查询条件") DeptQueryDTO query) {
        return Result.success(sysDeptService.getDeptPage(query));
    }

    @Operation(summary = "部门树", description = "获取完整部门树结构")
    @ApiResponse(responseCode = "200", description = "成功返回部门树")
    @GetMapping("/tree")
    public Result<List<DeptVO>> getTree() {
        return Result.success(sysDeptService.getDeptTree());
    }

    @Operation(summary = "部门详情", description = "根据部门ID查询部门详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回部门详情")
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/{id}")
    public Result<DeptVO> getById(
            @Parameter(description = "部门ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(sysDeptService.getDeptById(id));
    }

    @Operation(summary = "新增部门", description = "创建新部门")
    @ApiResponse(responseCode = "200", description = "成功创建部门，返回部门ID")
    @PreAuthorize("hasAuthority('system:dept:add')")
    @OperLog(module = "部门管理", operation = "新增", targetType = "部门", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "部门信息", required = true)
            @Valid @RequestBody DeptSaveDTO dto) {
        return Result.success(sysDeptService.saveDept(dto));
    }

    @Operation(summary = "修改部门", description = "更新部门基本信息")
    @ApiResponse(responseCode = "200", description = "成功修改部门信息")
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @OperLog(module = "部门管理", operation = "修改", targetType = "部门", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "部门信息", required = true)
            @Valid @RequestBody DeptSaveDTO dto) {
        sysDeptService.updateDept(dto);
        return Result.success();
    }

    @Operation(summary = "删除部门", description = "根据部门ID删除部门")
    @ApiResponse(responseCode = "200", description = "成功删除部门")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    @OperLog(module = "部门管理", operation = "删除", targetType = "部门", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "部门ID", required = true, example = "1")
            @PathVariable Long id) {
        sysDeptService.deleteDept(id);
        return Result.success();
    }

    @Operation(summary = "修改状态", description = "启用或禁用部门")
    @ApiResponse(responseCode = "200", description = "成功修改部门状态")
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @OperLog(module = "部门管理", operation = "修改状态", targetType = "部门", targetIdExpr = "#dto.id")
    @PutMapping("/status")
    public Result<Void> updateStatus(
            @Parameter(description = "修改部门状态", required = true)
            @Valid @RequestBody UpdateStatusDto dto) {
        sysDeptService.updateStatus(dto);
        return Result.success();
    }
}