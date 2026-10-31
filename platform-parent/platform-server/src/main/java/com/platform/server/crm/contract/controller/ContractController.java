package com.platform.server.crm.contract.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.contract.dto.ContractQueryDTO;
import com.platform.server.crm.contract.dto.ContractSaveDTO;
import com.platform.server.crm.contract.service.ContractService;
import com.platform.server.crm.contract.vo.ContractVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "合同管理", description = "合同CRUD接口")
@RestController
@RequestMapping("/api/crm/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @Operation(summary = "合同列表", description = "分页查询合同列表")
    @ApiResponse(responseCode = "200", description = "成功返回合同分页列表")
    @PreAuthorize("hasAuthority('crm:contract:list')")
    @GetMapping("/list")
    public Result<PageVO<ContractVO>> list(@Parameter(description = "查询条件") ContractQueryDTO query) {
        return Result.success(contractService.getContractPage(query));
    }

    @Operation(summary = "合同下拉选项", description = "发票/收款等关联选择用，仅租户隔离")
    @ApiResponse(responseCode = "200", description = "成功返回合同选项列表")
    @PreAuthorize("hasAnyAuthority('crm:contract:list','crm:invoice:list','crm:invoice:add','crm:invoice:edit','crm:payment:plan:list','crm:payment:plan:add','crm:payment:record:list','crm:payment:record:add')")
    @GetMapping("/options")
    public Result<List<ContractVO>> options(
            @Parameter(description = "合同状态，默认生效(3)", example = "3")
            @RequestParam(required = false) Integer status) {
        return Result.success(contractService.getContractOptions(status));
    }

    @Operation(summary = "合同详情", description = "查询合同详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回合同详情")
    @PreAuthorize("hasAuthority('crm:contract:query')")
    @GetMapping("/{id}")
    public Result<ContractVO> getById(
            @Parameter(description = "合同ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(contractService.getContractById(id));
    }

    @Operation(summary = "新增合同", description = "创建新合同")
    @ApiResponse(responseCode = "200", description = "成功创建合同")
    @PreAuthorize("hasAuthority('crm:contract:add')")
    @OperLog(module = "合同管理", operation = "新增", targetType = "合同", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "合同信息", required = true)
            @Valid @RequestBody ContractSaveDTO dto) {
        return Result.success(contractService.saveContract(dto));
    }

    @Operation(summary = "修改合同", description = "更新合同信息")
    @ApiResponse(responseCode = "200", description = "成功修改合同")
    @PreAuthorize("hasAuthority('crm:contract:edit')")
    @OperLog(module = "合同管理", operation = "修改", targetType = "合同", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "合同信息", required = true)
            @Valid @RequestBody ContractSaveDTO dto) {
        contractService.saveContract(dto);
        return Result.success();
    }

    @Operation(summary = "删除合同", description = "删除合同")
    @ApiResponse(responseCode = "200", description = "成功删除合同")
    @PreAuthorize("hasAuthority('crm:contract:delete')")
    @OperLog(module = "合同管理", operation = "删除", targetType = "合同", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "合同ID", required = true, example = "1")
            @PathVariable Long id) {
        contractService.deleteContract(id);
        return Result.success();
    }

    @Operation(summary = "提交审批", description = "将合同提交审批")
    @ApiResponse(responseCode = "200", description = "成功提交审批")
    @PreAuthorize("hasAuthority('crm:contract:submit')")
    @OperLog(module = "合同管理", operation = "提交审批", targetType = "合同", targetIdExpr = "#id")
    @PostMapping("/{id}/submit")
    public Result<Void> submit(
            @Parameter(description = "合同ID", required = true, example = "1")
            @PathVariable Long id) {
        contractService.submitContract(id);
        return Result.success();
    }

    @Operation(summary = "审批通过", description = "审批通过合同")
    @ApiResponse(responseCode = "200", description = "成功审批通过")
    @PreAuthorize("hasAuthority('crm:contract:approve')")
    @OperLog(module = "合同管理", operation = "审批通过", targetType = "合同", targetIdExpr = "#id")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(
            @Parameter(description = "合同ID", required = true, example = "1")
            @PathVariable Long id) {
        contractService.approveContract(id);
        return Result.success();
    }

    @Operation(summary = "审批驳回", description = "驳回合同审批")
    @ApiResponse(responseCode = "200", description = "成功驳回")
    @PreAuthorize("hasAuthority('crm:contract:approve')")
    @OperLog(module = "合同管理", operation = "审批驳回", targetType = "合同", targetIdExpr = "#id")
    @PostMapping("/{id}/reject")
    public Result<Void> reject(
            @Parameter(description = "合同ID", required = true, example = "1")
            @PathVariable Long id) {
        contractService.rejectContract(id);
        return Result.success();
    }

    @Operation(summary = "终止合同", description = "终止生效的合同")
    @ApiResponse(responseCode = "200", description = "成功终止合同")
    @PreAuthorize("hasAuthority('crm:contract:terminate')")
    @OperLog(module = "合同管理", operation = "终止", targetType = "合同", targetIdExpr = "#id")
    @PostMapping("/{id}/terminate")
    public Result<Void> terminate(
            @Parameter(description = "合同ID", required = true, example = "1")
            @PathVariable Long id) {
        contractService.terminateContract(id);
        return Result.success();
    }
}