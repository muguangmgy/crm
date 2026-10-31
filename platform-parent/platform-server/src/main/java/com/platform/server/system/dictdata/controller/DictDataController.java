package com.platform.server.system.dictdata.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.dto.UpdateStatusDto;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.dictdata.dto.DictDataBatchSaveDTO;
import com.platform.server.system.dictdata.dto.DictDataQueryDTO;
import com.platform.server.system.dictdata.dto.DictDataSaveDTO;
import com.platform.server.system.dictdata.service.SysDictDataService;
import com.platform.server.system.dictdata.vo.DictDataVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典项管理", description = "字典项CRUD接口")
@RestController
@RequestMapping("/api/system/dict-data")
@RequiredArgsConstructor
public class DictDataController {

    private final SysDictDataService sysDictDataService;

    @Operation(summary = "字典项列表", description = "分页查询字典项列表")
    @ApiResponse(responseCode = "200", description = "成功返回字典项分页列表")
    @PreAuthorize("hasAuthority('system:dict:list')")
    @GetMapping("/list")
    public Result<PageVO<DictDataVO>> list(@Parameter(description = "查询条件") DictDataQueryDTO query) {
        return Result.success(sysDictDataService.getDictDataPage(query));
    }

    @Operation(summary = "字典项详情", description = "根据ID查询字典项详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回字典项详情")
    @PreAuthorize("hasAuthority('system:dict:list')")
    @GetMapping("/{id}")
    public Result<DictDataVO> getById(
            @Parameter(description = "字典项ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(sysDictDataService.getDictDataById(id));
    }

    @Operation(summary = "新增字典项", description = "创建新字典项")
    @ApiResponse(responseCode = "200", description = "成功创建字典项")
    @PreAuthorize("hasAuthority('system:dict:add')")
    @OperLog(module = "字典项管理", operation = "新增", targetType = "字典项", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "字典项信息", required = true)
            @Valid @RequestBody DictDataSaveDTO dto) {
        return Result.success(sysDictDataService.saveDictData(dto));
    }

    @Operation(summary = "修改字典项", description = "更新字典项信息")
    @ApiResponse(responseCode = "200", description = "成功修改字典项")
    @PreAuthorize("hasAuthority('system:dict:edit')")
    @OperLog(module = "字典项管理", operation = "修改", targetType = "字典项", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "字典项信息", required = true)
            @Valid @RequestBody DictDataSaveDTO dto) {
        sysDictDataService.updateDictData(dto);
        return Result.success();
    }

    @Operation(summary = "删除字典项", description = "根据ID删除字典项")
    @ApiResponse(responseCode = "200", description = "成功删除字典项")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    @OperLog(module = "字典项管理", operation = "删除", targetType = "字典项", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "字典项ID", required = true, example = "1")
            @PathVariable Long id) {
        sysDictDataService.deleteDictData(id);
        return Result.success();
    }

    @Operation(summary = "修改状态", description = "启用或禁用字典项")
    @ApiResponse(responseCode = "200", description = "成功修改状态")
    @PreAuthorize("hasAuthority('system:dict:edit')")
    @OperLog(module = "字典项管理", operation = "修改状态", targetType = "字典项", targetIdExpr = "#dto.id")
    @PutMapping("/status")
    public Result<Void> updateStatus(
            @Parameter(description = "状态信息", required = true)
            @Valid @RequestBody UpdateStatusDto dto) {
        sysDictDataService.updateStatus(dto);
        return Result.success();
    }

    @Operation(summary = "按字典编码获取字典项", description = "根据字典编码和租户ID获取字典项列表")
    @ApiResponse(responseCode = "200", description = "成功返回字典项列表")
    @PreAuthorize("hasAuthority('system:dict:list')")
    @GetMapping("/by-code/{dictCode}")
    public Result<List<DictDataVO>> getByDictCode(
            @Parameter(description = "字典编码", required = true, example = "STATUS")
            @PathVariable String dictCode,
            @Parameter(description = "租户ID", example = "0")
            @RequestParam(required = false) Long tenantId) {
        return Result.success(sysDictDataService.getDictDataByDictCode(dictCode, tenantId));
    }

    @Operation(summary = "批量保存字典项", description = "批量新增、修改、删除字典项")
    @ApiResponse(responseCode = "200", description = "成功批量保存")
    @PreAuthorize("hasAuthority('system:dict:edit')")
    @OperLog(module = "字典管理", operation = "批量保存字典项", targetType = "字典项")
    @PostMapping("/batch")
    public Result<Void> batchSave(
            @Parameter(description = "批量保存数据", required = true)
            @Valid @RequestBody DictDataBatchSaveDTO dto) {
        sysDictDataService.batchSaveDictData(dto);
        return Result.success();
    }

    @Operation(summary = "按字典编码获取字典项", description = "前端其他页面功能取值使用")
    @ApiResponse(responseCode = "200", description = "成功返回字典项列表")
    @GetMapping("/getDictDataByCode/{dictCode}")
    public Result<List<DictDataVO>> getDictDataByCode(
            @Parameter(description = "字典编码", required = true, example = "STATUS")
            @PathVariable String dictCode) {
        return Result.success(sysDictDataService.getDictDataByCode(dictCode));
    }
}