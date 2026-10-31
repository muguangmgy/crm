package com.platform.server.crm.quote.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.quote.dto.QuoteQueryDTO;
import com.platform.server.crm.quote.dto.QuoteSaveDTO;
import com.platform.server.crm.quote.service.QuoteService;
import com.platform.server.crm.quote.vo.QuoteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "报价管理", description = "报价单CRUD接口")
@RestController
@RequestMapping("/api/crm/quote")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @Operation(summary = "报价单列表", description = "分页查询报价单列表")
    @ApiResponse(responseCode = "200", description = "成功返回报价单分页列表")
    @PreAuthorize("hasAuthority('crm:quote:list')")
    @GetMapping("/list")
    public Result<PageVO<QuoteVO>> list(@Parameter(description = "查询条件") QuoteQueryDTO query) {
        return Result.success(quoteService.getQuotePage(query));
    }

    @Operation(summary = "报价单详情", description = "查询报价单详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回报价单详情")
    @PreAuthorize("hasAuthority('crm:quote:query')")
    @GetMapping("/{id}")
    public Result<QuoteVO> getById(
            @Parameter(description = "报价单ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(quoteService.getQuoteById(id));
    }

    @Operation(summary = "新增报价单", description = "创建新报价单")
    @ApiResponse(responseCode = "200", description = "成功创建报价单")
    @PreAuthorize("hasAuthority('crm:quote:add')")
    @OperLog(module = "报价管理", operation = "新增", targetType = "报价单", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "报价单信息", required = true)
            @Valid @RequestBody QuoteSaveDTO dto) {
        return Result.success(quoteService.saveQuote(dto));
    }

    @Operation(summary = "修改报价单", description = "更新报价单信息")
    @ApiResponse(responseCode = "200", description = "成功修改报价单")
    @PreAuthorize("hasAuthority('crm:quote:edit')")
    @OperLog(module = "报价管理", operation = "修改", targetType = "报价单", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "报价单信息", required = true)
            @Valid @RequestBody QuoteSaveDTO dto) {
        quoteService.saveQuote(dto);
        return Result.success();
    }

    @Operation(summary = "删除报价单", description = "删除报价单")
    @ApiResponse(responseCode = "200", description = "成功删除报价单")
    @PreAuthorize("hasAuthority('crm:quote:delete')")
    @OperLog(module = "报价管理", operation = "删除", targetType = "报价单", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "报价单ID", required = true, example = "1")
            @PathVariable Long id) {
        quoteService.deleteQuote(id);
        return Result.success();
    }

    @Operation(summary = "发送报价单", description = "将报价单发送给客户")
    @ApiResponse(responseCode = "200", description = "成功发送报价单")
    @PreAuthorize("hasAuthority('crm:quote:send')")
    @OperLog(module = "报价管理", operation = "发送", targetType = "报价单", targetIdExpr = "#id")
    @PostMapping("/send/{id}")
    public Result<Void> send(
            @Parameter(description = "报价单ID", required = true, example = "1")
            @PathVariable Long id) {
        quoteService.sendQuote(id);
        return Result.success();
    }

    @Operation(summary = "报价单中标", description = "标记报价单为中标状态")
    @ApiResponse(responseCode = "200", description = "成功标记中标")
    @PreAuthorize("hasAuthority('crm:quote:accept')")
    @OperLog(module = "报价管理", operation = "中标", targetType = "报价单", targetIdExpr = "#id")
    @PostMapping("/accept/{id}")
    public Result<Void> accept(
            @Parameter(description = "报价单ID", required = true, example = "1")
            @PathVariable Long id) {
        quoteService.acceptQuote(id);
        return Result.success();
    }

    @Operation(summary = "报价单拒绝", description = "标记报价单为拒绝状态")
    @ApiResponse(responseCode = "200", description = "成功标记拒绝")
    @PreAuthorize("hasAuthority('crm:quote:reject')")
    @OperLog(module = "报价管理", operation = "拒绝", targetType = "报价单", targetIdExpr = "#id")
    @PostMapping("/reject/{id}")
    public Result<Void> reject(
            @Parameter(description = "报价单ID", required = true, example = "1")
            @PathVariable Long id) {
        quoteService.rejectQuote(id);
        return Result.success();
    }
}