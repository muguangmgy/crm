package com.platform.server.system.dicttype.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.dicttype.dto.DictTypeQueryDTO;
import com.platform.server.system.dicttype.dto.DictTypeSaveDTO;
import com.platform.server.system.dicttype.service.SysDictTypeService;
import com.platform.server.system.dicttype.vo.DictTypeTreeVO;
import com.platform.server.system.dicttype.vo.DictTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典类型管理", description = "字典类型CRUD接口")
@RestController
@RequestMapping("/api/system/dict-type")
@RequiredArgsConstructor
public class DictTypeController {

    private final SysDictTypeService sysDictTypeService;

    @Operation(summary = "字典类型列表", description = "分页查询字典类型列表")
    @ApiResponse(responseCode = "200", description = "成功返回字典类型分页列表")
    @PreAuthorize("hasAuthority('system:dict:list')")
    @GetMapping("/list")
    public Result<PageVO<DictTypeVO>> list(@Parameter(description = "查询条件") DictTypeQueryDTO query) {
        return Result.success(sysDictTypeService.getDictTypePage(query));
    }

    @Operation(summary = "字典类型详情", description = "根据ID查询字典类型详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回字典类型详情")
    @PreAuthorize("hasAuthority('system:dict:list')")
    @GetMapping("/{id}")
    public Result<DictTypeVO> getById(
            @Parameter(description = "字典类型ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(sysDictTypeService.getDictTypeById(id));
    }

    @Operation(summary = "新增字典类型", description = "创建新字典类型")
    @ApiResponse(responseCode = "200", description = "成功创建字典类型")
    @PreAuthorize("hasAuthority('system:dict:add')")
    @OperLog(module = "字典类型管理", operation = "新增", targetType = "字典类型", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "字典类型信息", required = true)
            @Valid @RequestBody DictTypeSaveDTO dto) {
        return Result.success(sysDictTypeService.saveDictType(dto));
    }

    @Operation(summary = "修改字典类型", description = "更新字典类型信息")
    @ApiResponse(responseCode = "200", description = "成功修改字典类型")
    @PreAuthorize("hasAuthority('system:dict:edit')")
    @OperLog(module = "字典类型管理", operation = "修改", targetType = "字典类型", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "字典类型信息", required = true)
            @Valid @RequestBody DictTypeSaveDTO dto) {
        sysDictTypeService.updateDictType(dto);
        return Result.success();
    }

    @Operation(summary = "删除字典类型", description = "根据ID删除字典类型")
    @ApiResponse(responseCode = "200", description = "成功删除字典类型")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    @OperLog(module = "字典类型管理", operation = "删除", targetType = "字典类型", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "字典类型ID", required = true, example = "1")
            @PathVariable Long id) {
        sysDictTypeService.deleteDictTypeWithCheck(id);
        return Result.success();
    }

    @Operation(summary = "修改状态", description = "启用或禁用字典类型")
    @ApiResponse(responseCode = "200", description = "成功修改状态")
    @PreAuthorize("hasAuthority('system:dict:edit')")
    @OperLog(module = "字典类型管理", operation = "修改状态", targetType = "字典类型", targetIdExpr = "#dto.id")
    @PutMapping("/status")
    public Result<Void> updateStatus(
            @Parameter(description = "状态信息", required = true)
            @Valid @RequestBody UpdateStatusDto dto) {
        sysDictTypeService.updateStatus(dto);
        return Result.success();
    }

    @Operation(summary = "字典类型树结构", description = "获取字典类型树形结构")
    @ApiResponse(responseCode = "200", description = "成功返回字典类型树")
    @PreAuthorize("hasAuthority('system:dict:list')")
    @GetMapping("/tree")
    public Result<List<DictTypeTreeVO>> getTree() {
        return Result.success(sysDictTypeService.getDictTypeTree());
    }

    @Operation(summary = "字典类型下拉选项", description = "获取启用状态的字典类型列表")
    @ApiResponse(responseCode = "200", description = "成功返回字典类型选项列表")
    @GetMapping("/options")
    public Result<List<DictTypeVO>> getOptions() {
        return Result.success(sysDictTypeService.getDictTypeOptions());
    }
}