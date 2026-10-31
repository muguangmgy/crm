package com.platform.server.crm.customer.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.crm.customer.dto.ContactSaveDTO;
import com.platform.server.crm.customer.service.ContactService;
import com.platform.server.crm.customer.vo.ContactVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "客户联系人", description = "客户联系人管理接口")
@RestController
@RequestMapping("/api/crm/customer/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @Operation(summary = "联系人列表", description = "获取客户的所有联系人")
    @ApiResponse(responseCode = "200", description = "成功返回联系人列表")
    @GetMapping("/list/{customerId}")
    public Result<List<ContactVO>> list(
            @Parameter(description = "客户ID", required = true)
            @PathVariable Long customerId) {
        return Result.success(contactService.getContactsByCustomerId(customerId));
    }

    @Operation(summary = "保存联系人", description = "新增或修改联系人")
    @ApiResponse(responseCode = "200", description = "成功保存联系人")
    @PreAuthorize("hasAuthority('crm:customer:edit')")
    @OperLog(module = "客户管理", operation = "保存联系人", targetType = "联系人", targetIdExpr = "#result.data")
    @PostMapping("/save")
    public Result<Long> save(
            @Parameter(description = "联系人信息", required = true)
            @Valid @RequestBody ContactSaveDTO dto) {
        return Result.success(contactService.saveContact(dto));
    }

    @Operation(summary = "删除联系人", description = "删除指定联系人")
    @ApiResponse(responseCode = "200", description = "成功删除联系人")
    @PreAuthorize("hasAuthority('crm:customer:edit')")
    @OperLog(module = "客户管理", operation = "删除联系人", targetType = "联系人", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "联系人ID", required = true)
            @PathVariable Long id) {
        contactService.deleteContact(id);
        return Result.success();
    }

    @Operation(summary = "设置主要联系人", description = "将指定联系人设为主要联系人")
    @ApiResponse(responseCode = "200", description = "成功设置主要联系人")
    @PreAuthorize("hasAuthority('crm:customer:edit')")
    @OperLog(module = "客户管理", operation = "设置主要联系人", targetType = "联系人", targetIdExpr = "#contactId")
    @PostMapping("/primary/{customerId}/{contactId}")
    public Result<Void> setPrimary(
            @Parameter(description = "客户ID", required = true)
            @PathVariable Long customerId,
            @Parameter(description = "联系人ID", required = true)
            @PathVariable Long contactId) {
        contactService.setPrimaryContact(customerId, contactId);
        return Result.success();
    }
}