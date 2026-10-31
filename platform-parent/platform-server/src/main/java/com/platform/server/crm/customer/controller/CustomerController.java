package com.platform.server.crm.customer.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.customer.dto.*;
import com.platform.server.crm.customer.service.CustomerService;
import com.platform.server.crm.customer.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "客户管理", description = "客户CRUD及公海池接口")
@RestController
@RequestMapping("/api/crm/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "客户列表", description = "分页查询私有池客户列表")
    @ApiResponse(responseCode = "200", description = "成功返回客户分页列表")
    @PreAuthorize("hasAuthority('crm:customer:list')")
    @GetMapping("/list")
    public Result<PageVO<CustomerVO>> list(@Parameter(description = "查询条件") CustomerQueryDTO query) {
        return Result.success(customerService.getPrivatePoolPage(query));
    }

    @Operation(summary = "公海池列表", description = "分页查询公海池客户列表")
    @ApiResponse(responseCode = "200", description = "成功返回公海池客户列表")
    @PreAuthorize("hasAuthority('crm:customer:pool')")
    @GetMapping("/pool")
    public Result<PageVO<CustomerVO>> poolList(@Parameter(description = "查询条件") CustomerQueryDTO query) {
        return Result.success(customerService.getPublicPoolPage(query));
    }

    @Operation(summary = "客户详情", description = "查询客户详细信息，包含联系人、跟进记录、标签")
    @ApiResponse(responseCode = "200", description = "成功返回客户详情")
    @PreAuthorize("hasAuthority('crm:customer:query')")
    @GetMapping("/{id}")
    public Result<CustomerDetailVO> getById(
            @Parameter(description = "客户ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(customerService.getCustomerDetail(id));
    }

    @Operation(summary = "新增客户", description = "创建新客户，自动进入私有池")
    @ApiResponse(responseCode = "200", description = "成功创建客户")
    @PreAuthorize("hasAuthority('crm:customer:add')")
    @OperLog(module = "客户管理", operation = "新增", targetType = "客户", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "客户信息", required = true)
            @Valid @RequestBody CustomerSaveDTO dto) {
        return Result.success(customerService.saveCustomer(dto));
    }

    @Operation(summary = "修改客户", description = "更新客户基本信息")
    @ApiResponse(responseCode = "200", description = "成功修改客户")
    @PreAuthorize("hasAuthority('crm:customer:edit')")
    @OperLog(module = "客户管理", operation = "修改", targetType = "客户", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "客户信息", required = true)
            @Valid @RequestBody CustomerSaveDTO dto) {
        customerService.saveCustomer(dto);
        return Result.success();
    }

    @Operation(summary = "删除客户", description = "删除客户及其关联数据")
    @ApiResponse(responseCode = "200", description = "成功删除客户")
    @PreAuthorize("hasAuthority('crm:customer:delete')")
    @OperLog(module = "客户管理", operation = "删除", targetType = "客户", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "客户ID", required = true, example = "1")
            @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Result.success();
    }

    @Operation(summary = "领取客户", description = "从公海池领取客户到私有池")
    @ApiResponse(responseCode = "200", description = "成功领取客户")
    @PreAuthorize("hasAuthority('crm:customer:pool:take')")
    @OperLog(module = "客户管理", operation = "领取", targetType = "客户", targetIdExpr = "#id")
    @PostMapping("/pool/take/{id}")
    public Result<Void> takeFromPool(
            @Parameter(description = "客户ID", required = true, example = "1")
            @PathVariable Long id) {
        customerService.takeFromPool(id);
        return Result.success();
    }

    @Operation(summary = "释放客户", description = "将客户从私有池释放到公海池")
    @ApiResponse(responseCode = "200", description = "成功释放客户")
    @PreAuthorize("hasAuthority('crm:customer:pool:release')")
    @OperLog(module = "客户管理", operation = "释放", targetType = "客户", targetIdExpr = "#id")
    @PostMapping("/pool/release/{id}")
    public Result<Void> releaseToPool(
            @Parameter(description = "客户ID", required = true, example = "1")
            @PathVariable Long id) {
        customerService.releaseToPool(id);
        return Result.success();
    }

    @Operation(summary = "转移客户", description = "将客户转移给其他销售人员")
    @ApiResponse(responseCode = "200", description = "成功转移客户")
    @PreAuthorize("hasAuthority('crm:customer:pool:transfer')")
    @OperLog(module = "客户管理", operation = "转移", targetType = "客户", targetIdExpr = "#dto.customerId")
    @PostMapping("/pool/transfer")
    public Result<Void> transfer(
            @Parameter(description = "转移信息", required = true)
            @Valid @RequestBody PoolTransferDTO dto) {
        customerService.transferCustomer(dto);
        return Result.success();
    }

    @Operation(summary = "获取客户标签", description = "获取客户已绑定的标签列表")
    @ApiResponse(responseCode = "200", description = "成功返回标签列表")
    @GetMapping("/{customerId}/tags")
    public Result<List<TagVO>> getCustomerTags(
            @Parameter(description = "客户ID", required = true)
            @PathVariable Long customerId) {
        return Result.success(customerService.getCustomerTags(customerId));
    }

    @Operation(summary = "绑定客户标签", description = "为客户绑定标签")
    @ApiResponse(responseCode = "200", description = "成功绑定标签")
    @PreAuthorize("hasAuthority('crm:customer:edit')")
    @OperLog(module = "客户管理", operation = "绑定标签", targetType = "客户", targetIdExpr = "#customerId")
    @PostMapping("/{customerId}/tags")
    public Result<Void> bindTags(
            @Parameter(description = "客户ID", required = true)
            @PathVariable Long customerId,
            @Parameter(description = "标签ID列表", required = true)
            @RequestBody List<Long> tagIds) {
        customerService.bindTags(customerId, tagIds);
        return Result.success();
    }

    @Operation(summary = "获取所有标签", description = "获取租户下所有启用的客户标签")
    @ApiResponse(responseCode = "200", description = "成功返回标签列表")
    @GetMapping("/tags")
    public Result<List<TagVO>> getAllTags() {
        return Result.success(customerService.getAllTags());
    }
}