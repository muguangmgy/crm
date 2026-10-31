package com.platform.server.crm.lead.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.lead.dto.*;
import com.platform.server.crm.lead.service.CrmLeadService;
import com.platform.server.crm.lead.vo.LeadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "线索管理", description = "线索CRUD及转商机接口")
@RestController
@RequestMapping("/api/crm/lead")
@RequiredArgsConstructor
public class CrmLeadController {

    private final CrmLeadService leadService;

    @Operation(summary = "线索列表", description = "分页查询线索列表")
    @ApiResponse(responseCode = "200", description = "成功返回线索分页列表")
    @PreAuthorize("hasAuthority('crm:lead:list')")
    @GetMapping("/list")
    public Result<PageVO<LeadVO>> list(@Parameter(description = "查询条件") LeadQueryDTO query) {
        return Result.success(leadService.getLeadPage(query));
    }

    @Operation(summary = "线索详情", description = "查询线索详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回线索详情")
    @PreAuthorize("hasAuthority('crm:lead:query')")
    @GetMapping("/{id}")
    public Result<LeadVO> getById(
            @Parameter(description = "线索ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(leadService.getLeadDetail(id));
    }

    @Operation(summary = "新增线索", description = "创建新线索")
    @ApiResponse(responseCode = "200", description = "成功创建线索")
    @PreAuthorize("hasAuthority('crm:lead:add')")
    @OperLog(module = "线索管理", operation = "新增", targetType = "线索", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "线索信息", required = true)
            @Valid @RequestBody LeadSaveDTO dto) {
        return Result.success(leadService.saveLead(dto));
    }

    @Operation(summary = "修改线索", description = "更新线索基本信息")
    @ApiResponse(responseCode = "200", description = "成功修改线索")
    @PreAuthorize("hasAuthority('crm:lead:edit')")
    @OperLog(module = "线索管理", operation = "修改", targetType = "线索", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "线索信息", required = true)
            @Valid @RequestBody LeadSaveDTO dto) {
        leadService.saveLead(dto);
        return Result.success();
    }

    @Operation(summary = "删除线索", description = "删除线索")
    @ApiResponse(responseCode = "200", description = "成功删除线索")
    @PreAuthorize("hasAuthority('crm:lead:delete')")
    @OperLog(module = "线索管理", operation = "删除", targetType = "线索", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "线索ID", required = true, example = "1")
            @PathVariable Long id) {
        leadService.deleteLead(id);
        return Result.success();
    }

    @Operation(summary = "线索转商机", description = "将线索转换为商机，线索状态变为已转商机")
    @ApiResponse(responseCode = "200", description = "成功转换，返回商机ID")
    @PreAuthorize("hasAuthority('crm:lead:convert')")
    @OperLog(module = "线索管理", operation = "转商机", targetType = "线索", targetIdExpr = "#dto.leadId")
    @PostMapping("/convert")
    public Result<Long> convert(
            @Parameter(description = "转换信息", required = true)
            @Valid @RequestBody LeadConvertDTO dto) {
        return Result.success(leadService.convertToOpportunity(dto));
    }
}