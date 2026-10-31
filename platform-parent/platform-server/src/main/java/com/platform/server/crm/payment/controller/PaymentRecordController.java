package com.platform.server.crm.payment.controller;

import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.payment.dto.PaymentRecordQueryDTO;
import com.platform.server.crm.payment.dto.PaymentRecordSaveDTO;
import com.platform.server.crm.payment.service.PaymentRecordService;
import com.platform.server.crm.payment.vo.PaymentRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "收款记录管理", description = "收款记录CRUD接口")
@RestController
@RequestMapping("/api/crm/payment/record")
@RequiredArgsConstructor
public class PaymentRecordController {

    private final PaymentRecordService paymentRecordService;

    @Operation(summary = "收款记录列表", description = "分页查询收款记录列表")
    @GetMapping("/list")
    public Result<PageVO<PaymentRecordVO>> list(PaymentRecordQueryDTO query) {
        return Result.success(paymentRecordService.getRecordPage(query));
    }

    @Operation(summary = "收款记录详情", description = "根据ID查询收款记录详情")
    @Parameter(name = "id", description = "记录ID", required = true, example = "1")
    @GetMapping("/{id}")
    public Result<PaymentRecordVO> getById(@PathVariable Long id) {
        return Result.success(paymentRecordService.getRecordById(id));
    }

    @Operation(summary = "合同收款记录", description = "根据合同ID查询收款记录列表")
    @Parameter(name = "contractId", description = "合同ID", required = true, example = "1")
    @GetMapping("/contract/{contractId}")
    public Result<List<PaymentRecordVO>> getByContractId(@PathVariable Long contractId) {
        return Result.success(paymentRecordService.getRecordsByContractId(contractId));
    }

    @Operation(summary = "新增收款记录", description = "创建收款记录，默认状态为待确认")
    @PostMapping
    public Result<Long> add(@Valid @RequestBody PaymentRecordSaveDTO dto) {
        return Result.success(paymentRecordService.saveRecord(dto));
    }

    @Operation(summary = "修改收款记录", description = "修改收款记录信息，已确认的记录不能修改")
    @PutMapping
    public Result<Long> edit(@Valid @RequestBody PaymentRecordSaveDTO dto) {
        return Result.success(paymentRecordService.saveRecord(dto));
    }

    @Operation(summary = "删除收款记录", description = "删除收款记录，已确认的记录不能删除")
    @Parameter(name = "id", description = "记录ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        paymentRecordService.deleteRecord(id);
        return Result.success();
    }

    @Operation(summary = "收款确认", description = "确认收款，更新合同已收金额")
    @Parameter(name = "id", description = "记录ID", required = true, example = "1")
    @PostMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        paymentRecordService.confirmPayment(id);
        return Result.success();
    }

    @Operation(summary = "取消确认", description = "取消收款确认，更新合同已收金额")
    @Parameter(name = "id", description = "记录ID", required = true, example = "1")
    @PostMapping("/{id}/unconfirm")
    public Result<Void> unconfirm(@PathVariable Long id) {
        paymentRecordService.unconfirmPayment(id);
        return Result.success();
    }
}