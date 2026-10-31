package com.platform.server.crm.customer.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.crm.customer.dto.FollowSaveDTO;
import com.platform.server.crm.customer.service.FollowService;
import com.platform.server.crm.customer.vo.FollowVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "客户跟进", description = "客户跟进记录管理接口")
@RestController
@RequestMapping("/api/crm/customer/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "跟进记录列表", description = "获取客户的所有跟进记录")
    @ApiResponse(responseCode = "200", description = "成功返回跟进记录列表")
    @GetMapping("/list/{customerId}")
    public Result<List<FollowVO>> list(
            @Parameter(description = "客户ID", required = true)
            @PathVariable Long customerId) {
        return Result.success(followService.getFollowsByCustomerId(customerId));
    }

    @Operation(summary = "保存跟进记录", description = "新增跟进记录")
    @ApiResponse(responseCode = "200", description = "成功保存跟进记录")
    @PreAuthorize("hasAuthority('crm:customer:edit')")
    @OperLog(module = "客户管理", operation = "跟进", targetType = "跟进记录", targetIdExpr = "#result.data")
    @PostMapping("/save")
    public Result<Long> save(
            @Parameter(description = "跟进记录信息", required = true)
            @Valid @RequestBody FollowSaveDTO dto) {
        return Result.success(followService.saveFollow(dto));
    }

    @Operation(summary = "删除跟进记录", description = "删除指定跟进记录")
    @ApiResponse(responseCode = "200", description = "成功删除跟进记录")
    @PreAuthorize("hasAuthority('crm:customer:edit')")
    @OperLog(module = "客户管理", operation = "删除跟进", targetType = "跟进记录", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "跟进记录ID", required = true)
            @PathVariable Long id) {
        followService.deleteFollow(id);
        return Result.success();
    }
}