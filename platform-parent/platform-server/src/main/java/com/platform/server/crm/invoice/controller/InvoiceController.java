package com.platform.server.crm.invoice.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.invoice.dto.InvoiceQueryDTO;
import com.platform.server.crm.invoice.dto.InvoiceSaveDTO;
import com.platform.server.crm.invoice.service.InvoiceService;
import com.platform.server.crm.invoice.vo.InvoiceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "发票管理", description = "发票CRUD接口")
@RestController
@RequestMapping("/api/crm/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Operation(summary = "发票列表", description = "分页查询发票列表")
    @ApiResponse(responseCode = "200", description = "成功返回发票分页列表")
    @PreAuthorize("hasAuthority('crm:invoice:list')")
    @GetMapping("/list")
    public Result<PageVO<InvoiceVO>> list(@Parameter(description = "查询条件") InvoiceQueryDTO query) {
        return Result.success(invoiceService.getInvoicePage(query));
    }

    @Operation(summary = "发票详情", description = "查询发票详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回发票详情")
    @PreAuthorize("hasAuthority('crm:invoice:query')")
    @GetMapping("/{id}")
    public Result<InvoiceVO> getById(
            @Parameter(description = "发票ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(invoiceService.getInvoiceById(id));
    }

    @Operation(summary = "新增发票", description = "创建新发票")
    @ApiResponse(responseCode = "200", description = "成功创建发票")
    @PreAuthorize("hasAuthority('crm:invoice:add')")
    @OperLog(module = "发票管理", operation = "新增", targetType = "发票", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "发票信息", required = true)
            @Valid @RequestBody InvoiceSaveDTO dto) {
        return Result.success(invoiceService.saveInvoice(dto));
    }

    @Operation(summary = "修改发票", description = "更新发票信息")
    @ApiResponse(responseCode = "200", description = "成功修改发票")
    @PreAuthorize("hasAuthority('crm:invoice:edit')")
    @OperLog(module = "发票管理", operation = "修改", targetType = "发票", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "发票信息", required = true)
            @Valid @RequestBody InvoiceSaveDTO dto) {
        invoiceService.saveInvoice(dto);
        return Result.success();
    }

    @Operation(summary = "删除发票", description = "删除发票")
    @ApiResponse(responseCode = "200", description = "成功删除发票")
    @PreAuthorize("hasAuthority('crm:invoice:delete')")
    @OperLog(module = "发票管理", operation = "删除", targetType = "发票", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "发票ID", required = true, example = "1")
            @PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return Result.success();
    }

    @Operation(summary = "作废发票", description = "将发票标记为作废状态")
    @ApiResponse(responseCode = "200", description = "成功作废发票")
    @PreAuthorize("hasAuthority('crm:invoice:cancel')")
    @OperLog(module = "发票管理", operation = "作废", targetType = "发票", targetIdExpr = "#id")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(
            @Parameter(description = "发票ID", required = true, example = "1")
            @PathVariable Long id) {
        invoiceService.cancelInvoice(id);
        return Result.success();
    }
}