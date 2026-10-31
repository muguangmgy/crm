package com.platform.server.crm.opportunity.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.opportunity.dto.OpportunityCloseDTO;
import com.platform.server.crm.opportunity.dto.OpportunityQueryDTO;
import com.platform.server.crm.opportunity.dto.OpportunitySaveDTO;
import com.platform.server.crm.opportunity.dto.StageSaveDTO;
import com.platform.server.crm.opportunity.service.OpportunityService;
import com.platform.server.crm.opportunity.vo.OpportunityVO;
import com.platform.server.crm.opportunity.vo.StageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商机管理", description = "商机及阶段CRUD接口")
@RestController
@RequestMapping("/api/crm/opportunity")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;

    // ===== 商机接口 =====

    @Operation(summary = "商机列表", description = "分页查询商机列表")
    @ApiResponse(responseCode = "200", description = "成功返回商机分页列表")
    @PreAuthorize("hasAuthority('crm:opportunity:list')")
    @GetMapping("/list")
    public Result<PageVO<OpportunityVO>> list(@Parameter(description = "查询条件") OpportunityQueryDTO query) {
        return Result.success(opportunityService.getOpportunityPage(query));
    }

    @Operation(summary = "商机详情", description = "查询商机详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回商机详情")
    @PreAuthorize("hasAuthority('crm:opportunity:query')")
    @GetMapping("/{id}")
    public Result<OpportunityVO> getById(
            @Parameter(description = "商机ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(opportunityService.getOpportunityById(id));
    }

    @Operation(summary = "新增商机", description = "创建新商机")
    @ApiResponse(responseCode = "200", description = "成功创建商机")
    @PreAuthorize("hasAuthority('crm:opportunity:add')")
    @OperLog(module = "商机管理", operation = "新增", targetType = "商机", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "商机信息", required = true)
            @Valid @RequestBody OpportunitySaveDTO dto) {
        return Result.success(opportunityService.saveOpportunity(dto));
    }

    @Operation(summary = "修改商机", description = "更新商机信息")
    @ApiResponse(responseCode = "200", description = "成功修改商机")
    @PreAuthorize("hasAuthority('crm:opportunity:edit')")
    @OperLog(module = "商机管理", operation = "修改", targetType = "商机", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "商机信息", required = true)
            @Valid @RequestBody OpportunitySaveDTO dto) {
        opportunityService.saveOpportunity(dto);
        return Result.success();
    }

    @Operation(summary = "删除商机", description = "删除商机")
    @ApiResponse(responseCode = "200", description = "成功删除商机")
    @PreAuthorize("hasAuthority('crm:opportunity:delete')")
    @OperLog(module = "商机管理", operation = "删除", targetType = "商机", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "商机ID", required = true, example = "1")
            @PathVariable Long id) {
        opportunityService.deleteOpportunity(id);
        return Result.success();
    }

    @Operation(summary = "阶段推进", description = "推进商机到下一阶段")
    @ApiResponse(responseCode = "200", description = "成功推进阶段")
    @PreAuthorize("hasAuthority('crm:opportunity:advance')")
    @OperLog(module = "商机管理", operation = "阶段推进", targetType = "商机", targetIdExpr = "#id")
    @PostMapping("/advance/{id}")
    public Result<Void> advance(
            @Parameter(description = "商机ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "目标阶段ID", required = true, example = "2")
            @RequestParam Long nextStageId) {
        opportunityService.advanceOpportunity(id, nextStageId);
        return Result.success();
    }

    @Operation(summary = "成交/失败", description = "标记商机成交或失败")
    @ApiResponse(responseCode = "200", description = "成功标记商机状态")
    @PreAuthorize("hasAuthority('crm:opportunity:close')")
    @OperLog(module = "商机管理", operation = "成交/失败", targetType = "商机", targetIdExpr = "#id")
    @PostMapping("/close/{id}")
    public Result<Void> close(
            @Parameter(description = "商机ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "成交/失败信息", required = true)
            @Valid @RequestBody OpportunityCloseDTO dto) {
        opportunityService.closeOpportunity(id, dto);
        return Result.success();
    }

    // ===== 阶段接口 =====

    @Operation(summary = "阶段列表", description = "获取所有阶段配置")
    @ApiResponse(responseCode = "200", description = "成功返回阶段列表")
    @GetMapping("/stage/list")
    public Result<List<StageVO>> stageList() {
        return Result.success(opportunityService.getStageList());
    }

    @Operation(summary = "启用阶段列表", description = "获取启用的阶段配置（用于下拉选择）")
    @ApiResponse(responseCode = "200", description = "成功返回阶段列表")
    @GetMapping("/stage/active")
    public Result<List<StageVO>> activeStageList() {
        return Result.success(opportunityService.getActiveStageList());
    }

    @Operation(summary = "新增阶段", description = "创建商机阶段")
    @ApiResponse(responseCode = "200", description = "成功创建阶段")
    @PreAuthorize("hasAuthority('crm:opportunity:stage:add')")
    @OperLog(module = "商机管理", operation = "新增阶段", targetType = "阶段", targetIdExpr = "#result.data")
    @PostMapping("/stage")
    public Result<Long> saveStage(
            @Parameter(description = "阶段信息", required = true)
            @Valid @RequestBody StageSaveDTO dto) {
        return Result.success(opportunityService.saveStage(dto));
    }

    @Operation(summary = "修改阶段", description = "更新阶段配置")
    @ApiResponse(responseCode = "200", description = "成功修改阶段")
    @PreAuthorize("hasAuthority('crm:opportunity:stage:edit')")
    @OperLog(module = "商机管理", operation = "修改阶段", targetType = "阶段", targetIdExpr = "#dto.id")
    @PutMapping("/stage")
    public Result<Void> updateStage(
            @Parameter(description = "阶段信息", required = true)
            @Valid @RequestBody StageSaveDTO dto) {
        opportunityService.saveStage(dto);
        return Result.success();
    }

    @Operation(summary = "删除阶段", description = "删除商机阶段")
    @ApiResponse(responseCode = "200", description = "成功删除阶段")
    @PreAuthorize("hasAuthority('crm:opportunity:stage:delete')")
    @OperLog(module = "商机管理", operation = "删除阶段", targetType = "阶段", targetIdExpr = "#id")
    @DeleteMapping("/stage/{id}")
    public Result<Void> deleteStage(
            @Parameter(description = "阶段ID", required = true, example = "1")
            @PathVariable Long id) {
        opportunityService.deleteStage(id);
        return Result.success();
    }
}