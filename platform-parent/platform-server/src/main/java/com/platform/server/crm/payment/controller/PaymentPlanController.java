package com.platform.server.crm.payment.controller;

import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.payment.dto.PaymentPlanQueryDTO;
import com.platform.server.crm.payment.dto.PaymentPlanSaveDTO;
import com.platform.server.crm.payment.service.PaymentPlanService;
import com.platform.server.crm.payment.vo.PaymentPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "收款计划管理", description = "收款计划CRUD接口")
@RestController
@RequestMapping("/api/crm/payment/plan")
@RequiredArgsConstructor
public class PaymentPlanController {

    private final PaymentPlanService paymentPlanService;

    @Operation(summary = "收款计划列表", description = "分页查询收款计划列表")
    @GetMapping("/list")
    public Result<PageVO<PaymentPlanVO>> list(PaymentPlanQueryDTO query) {
        return Result.success(paymentPlanService.getPlanPage(query));
    }

    @Operation(summary = "收款计划详情", description = "根据ID查询收款计划详情")
    @Parameter(name = "id", description = "计划ID", required = true, example = "1")
    @GetMapping("/{id}")
    public Result<PaymentPlanVO> getById(@PathVariable Long id) {
        return Result.success(paymentPlanService.getPlanById(id));
    }

    @Operation(summary = "合同收款计划", description = "根据合同ID查询收款计划列表")
    @Parameter(name = "contractId", description = "合同ID", required = true, example = "1")
    @GetMapping("/contract/{contractId}")
    public Result<List<PaymentPlanVO>> getByContractId(@PathVariable Long contractId) {
        return Result.success(paymentPlanService.getPlansByContractId(contractId));
    }

    @Operation(summary = "新增收款计划", description = "创建收款计划，默认状态为未收款")
    @PostMapping
    public Result<Long> add(@Valid @RequestBody PaymentPlanSaveDTO dto) {
        return Result.success(paymentPlanService.savePlan(dto));
    }

    @Operation(summary = "修改收款计划", description = "修改收款计划信息")
    @PutMapping
    public Result<Long> edit(@Valid @RequestBody PaymentPlanSaveDTO dto) {
        return Result.success(paymentPlanService.savePlan(dto));
    }

    @Operation(summary = "删除收款计划", description = "删除收款计划，已收款的计划不能删除")
    @Parameter(name = "id", description = "计划ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        paymentPlanService.deletePlan(id);
        return Result.success();
    }
}