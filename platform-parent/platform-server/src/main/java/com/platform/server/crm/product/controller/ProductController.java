package com.platform.server.crm.product.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.product.dto.CategorySaveDTO;
import com.platform.server.crm.product.dto.ProductQueryDTO;
import com.platform.server.crm.product.dto.ProductSaveDTO;
import com.platform.server.crm.product.service.ProductService;
import com.platform.server.crm.product.vo.CategoryVO;
import com.platform.server.crm.product.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "产品管理", description = "产品及分类CRUD接口")
@RestController
@RequestMapping("/api/crm/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ===== 产品接口 =====

    @Operation(summary = "产品列表", description = "分页查询产品列表")
    @ApiResponse(responseCode = "200", description = "成功返回产品分页列表")
    @PreAuthorize("hasAuthority('crm:product:list')")
    @GetMapping("/list")
    public Result<PageVO<ProductVO>> list(@Parameter(description = "查询条件") ProductQueryDTO query) {
        return Result.success(productService.getProductPage(query));
    }

    @Operation(summary = "产品详情", description = "查询产品详细信息")
    @ApiResponse(responseCode = "200", description = "成功返回产品详情")
    @PreAuthorize("hasAuthority('crm:product:query')")
    @GetMapping("/{id}")
    public Result<ProductVO> getById(
            @Parameter(description = "产品ID", required = true, example = "1")
            @PathVariable Long id) {
        return Result.success(productService.getProductById(id));
    }

    @Operation(summary = "新增产品", description = "创建新产品")
    @ApiResponse(responseCode = "200", description = "成功创建产品")
    @PreAuthorize("hasAuthority('crm:product:add')")
    @OperLog(module = "产品管理", operation = "新增", targetType = "产品", targetIdExpr = "#result.data")
    @PostMapping
    public Result<Long> save(
            @Parameter(description = "产品信息", required = true)
            @Valid @RequestBody ProductSaveDTO dto) {
        return Result.success(productService.saveProduct(dto));
    }

    @Operation(summary = "修改产品", description = "更新产品信息")
    @ApiResponse(responseCode = "200", description = "成功修改产品")
    @PreAuthorize("hasAuthority('crm:product:edit')")
    @OperLog(module = "产品管理", operation = "修改", targetType = "产品", targetIdExpr = "#dto.id")
    @PutMapping
    public Result<Void> update(
            @Parameter(description = "产品信息", required = true)
            @Valid @RequestBody ProductSaveDTO dto) {
        productService.saveProduct(dto);
        return Result.success();
    }

    @Operation(summary = "删除产品", description = "删除产品")
    @ApiResponse(responseCode = "200", description = "成功删除产品")
    @PreAuthorize("hasAuthority('crm:product:delete')")
    @OperLog(module = "产品管理", operation = "删除", targetType = "产品", targetIdExpr = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "产品ID", required = true, example = "1")
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    // ===== 分类接口 =====

    @Operation(summary = "分类树", description = "获取分类树形结构")
    @ApiResponse(responseCode = "200", description = "成功返回分类树")
    @GetMapping("/category/tree")
    public Result<List<CategoryVO>> categoryTree() {
        return Result.success(productService.getCategoryTree());
    }

    @Operation(summary = "分类列表", description = "获取分类列表")
    @ApiResponse(responseCode = "200", description = "成功返回分类列表")
    @GetMapping("/category/list")
    public Result<List<CategoryVO>> categoryList() {
        return Result.success(productService.getCategoryList());
    }

    @Operation(summary = "新增分类", description = "创建产品分类")
    @ApiResponse(responseCode = "200", description = "成功创建分类")
    @PreAuthorize("hasAuthority('crm:product:category:add')")
    @OperLog(module = "产品管理", operation = "新增分类", targetType = "分类", targetIdExpr = "#result.data")
    @PostMapping("/category")
    public Result<Long> saveCategory(
            @Parameter(description = "分类信息", required = true)
            @Valid @RequestBody CategorySaveDTO dto) {
        return Result.success(productService.saveCategory(dto));
    }

    @Operation(summary = "修改分类", description = "更新分类信息")
    @ApiResponse(responseCode = "200", description = "成功修改分类")
    @PreAuthorize("hasAuthority('crm:product:category:edit')")
    @OperLog(module = "产品管理", operation = "修改分类", targetType = "分类", targetIdExpr = "#dto.id")
    @PutMapping("/category")
    public Result<Void> updateCategory(
            @Parameter(description = "分类信息", required = true)
            @Valid @RequestBody CategorySaveDTO dto) {
        productService.saveCategory(dto);
        return Result.success();
    }

    @Operation(summary = "删除分类", description = "删除产品分类")
    @ApiResponse(responseCode = "200", description = "成功删除分类")
    @PreAuthorize("hasAuthority('crm:product:category:delete')")
    @OperLog(module = "产品管理", operation = "删除分类", targetType = "分类", targetIdExpr = "#id")
    @DeleteMapping("/category/{id}")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID", required = true, example = "1")
            @PathVariable Long id) {
        productService.deleteCategory(id);
        return Result.success();
    }
}