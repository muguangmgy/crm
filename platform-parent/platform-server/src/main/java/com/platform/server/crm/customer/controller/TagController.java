package com.platform.server.crm.customer.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.crm.customer.dto.TagSaveDTO;
import com.platform.server.crm.customer.service.TagService;
import com.platform.server.crm.customer.vo.TagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "客户标签", description = "客户标签管理接口")
@RestController
@RequestMapping("/api/crm/customer/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "标签列表", description = "获取租户下所有启用的客户标签")
    @ApiResponse(responseCode = "200", description = "成功返回标签列表")
    @GetMapping("/list")
    public Result<List<TagVO>> list() {
        return Result.success(tagService.getAllTags());
    }

    @Operation(summary = "保存标签", description = "新增或修改客户标签")
    @ApiResponse(responseCode = "200", description = "成功保存标签")
    @PreAuthorize("hasAuthority('crm:customer:tag:edit')")
    @OperLog(module = "客户管理", operation = "保存标签", targetType = "标签", targetIdExpr = "#result.data")
    @PostMapping("/save")
    public Result<Long> save(
            @Parameter(description = "标签信息", required = true)
            @Valid @RequestBody TagSaveDTO dto) {
        return Result.success(tagService.saveTag(dto));
    }

    @Operation(summary = "删除标签", description = "删除客户标签")
    @ApiResponse(responseCode = "200", description = "成功删除标签")
    @PreAuthorize("hasAuthority('crm:customer:tag:delete')")
    @OperLog(module = "客户管理", operation = "删除标签", targetType = "标签", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "标签ID", required = true)
            @PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success();
    }
}