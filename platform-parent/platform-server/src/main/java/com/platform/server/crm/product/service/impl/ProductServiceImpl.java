package com.platform.server.crm.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.exception.BusinessException;
import com.platform.common.context.UserContextHolder;
import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.product.dto.CategorySaveDTO;
import com.platform.server.crm.product.dto.ProductQueryDTO;
import com.platform.server.crm.product.dto.ProductSaveDTO;
import com.platform.server.crm.product.mapper.CategoryMapper;
import com.platform.server.crm.product.mapper.ProductMapper;
import com.platform.server.crm.product.po.CrmProduct;
import com.platform.server.crm.product.po.CrmProductCategory;
import com.platform.server.crm.product.service.ProductService;
import com.platform.server.crm.product.vo.CategoryVO;
import com.platform.server.crm.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public PageVO<ProductVO> getProductPage(ProductQueryDTO query) {
        Page<CrmProduct> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<CrmProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmProduct::getTenantId, UserContextHolder.getTenantId());

        if (query.getProductName() != null) {
            wrapper.like(CrmProduct::getProductName, query.getProductName());
        }
        if (query.getProductCode() != null) {
            wrapper.like(CrmProduct::getProductCode, query.getProductCode());
        }
        if (query.getCategoryId() != null) {
            wrapper.eq(CrmProduct::getCategoryId, query.getCategoryId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(CrmProduct::getStatus, query.getStatus());
        }
        if (query.getUnit() != null) {
            wrapper.eq(CrmProduct::getUnit, query.getUnit());
        }

        wrapper.orderByDesc(CrmProduct::getCreateTime);

        Page<CrmProduct> result = productMapper.selectPage(page, wrapper);

        // 获取分类名称映射
        Map<Long, String> categoryMap = getCategoryMap();

        List<ProductVO> list = result.getRecords().stream()
                .map(p -> toProductVO(p, categoryMap))
                .collect(Collectors.toList());

        return PageVO.of(list, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public ProductVO getProductById(Long id) {
        CrmProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }

        ProductVO vo = toProductVO(product, getCategoryMap());
        return vo;
    }

    @Override
    @Transactional
    public Long saveProduct(ProductSaveDTO dto) {
        CrmProduct product;

        if (dto.getId() != null) {
            // 修改
            product = productMapper.selectById(dto.getId());
            if (product == null) {
                throw new BusinessException("产品不存在");
            }
        } else {
            // 新增
            product = new CrmProduct();
            product.setTenantId(UserContextHolder.getTenantId());
            product.setProductNo(generateProductNo());
        }

        product.setCategoryId(dto.getCategoryId());
        product.setProductName(dto.getProductName());
        product.setProductCode(dto.getProductCode());
        product.setUnit(dto.getUnit());
        product.setSpec(dto.getSpec());
        product.setModel(dto.getModel());
        product.setPrice(dto.getPrice());
        product.setMinPrice(dto.getMinPrice());
        product.setCostPrice(dto.getCostPrice());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        product.setSort(dto.getSort() != null ? dto.getSort() : 0);

        if (dto.getId() != null) {
            productMapper.updateById(product);
        } else {
            productMapper.insert(product);
        }

        return product.getId();
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        CrmProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        productMapper.deleteById(id);
    }

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<CrmProductCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<CrmProductCategory>()
                        .eq(CrmProductCategory::getTenantId, UserContextHolder.getTenantId())
                        .eq(CrmProductCategory::getStatus, 1)
                        .orderByAsc(CrmProductCategory::getSort)
        );

        List<CategoryVO> voList = categories.stream()
                .map(this::toCategoryVO)
                .collect(Collectors.toList());

        // 构建树结构
        return buildCategoryTree(voList, 0L);
    }

    @Override
    public List<CategoryVO> getCategoryList() {
        List<CrmProductCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<CrmProductCategory>()
                        .eq(CrmProductCategory::getTenantId, UserContextHolder.getTenantId())
                        .orderByAsc(CrmProductCategory::getSort)
        );

        return categories.stream()
                .map(this::toCategoryVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long saveCategory(CategorySaveDTO dto) {
        CrmProductCategory category;

        if (dto.getId() != null) {
            // 修改
            category = categoryMapper.selectById(dto.getId());
            if (category == null) {
                throw new BusinessException("分类不存在");
            }
        } else {
            // 新增
            category = new CrmProductCategory();
            category.setTenantId(UserContextHolder.getTenantId());
        }

        category.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        category.setCategoryName(dto.getCategoryName());
        category.setSort(dto.getSort() != null ? dto.getSort() : 0);
        category.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        if (dto.getId() != null) {
            categoryMapper.updateById(category);
        } else {
            categoryMapper.insert(category);
        }

        return category.getId();
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        CrmProductCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查是否有子分类
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<CrmProductCategory>()
                        .eq(CrmProductCategory::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException("存在子分类，无法删除");
        }

        // 检查是否有产品
        Long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<CrmProduct>()
                        .eq(CrmProduct::getCategoryId, id)
        );
        if (productCount > 0) {
            throw new BusinessException("分类下存在产品，无法删除");
        }

        categoryMapper.deleteById(id);
    }

    @Override
    public String getCategoryName(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        CrmProductCategory category = categoryMapper.selectById(categoryId);
        return category != null ? category.getCategoryName() : null;
    }

    // ===== 私有方法 =====

    private ProductVO toProductVO(CrmProduct product, Map<Long, String> categoryMap) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setProductNo(product.getProductNo());
        vo.setProductName(product.getProductName());
        vo.setProductCode(product.getProductCode());
        vo.setCategoryId(product.getCategoryId());
        vo.setCategoryName(categoryMap.getOrDefault(product.getCategoryId(), ""));
        vo.setUnit(product.getUnit());
        vo.setSpec(product.getSpec());
        vo.setModel(product.getModel());
        vo.setPrice(product.getPrice());
        vo.setMinPrice(product.getMinPrice());
        vo.setCostPrice(product.getCostPrice());
        vo.setDescription(product.getDescription());
        vo.setImageUrl(product.getImageUrl());
        vo.setStatus(product.getStatus());
        vo.setSort(product.getSort());
        vo.setCreateTime(product.getCreateTime());
        vo.setUpdateTime(product.getUpdateTime());
        return vo;
    }

    private CategoryVO toCategoryVO(CrmProductCategory category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setParentId(category.getParentId());
        vo.setCategoryName(category.getCategoryName());
        vo.setSort(category.getSort());
        vo.setStatus(category.getStatus());
        vo.setCreateTime(category.getCreateTime());
        vo.setUpdateTime(category.getUpdateTime());
        return vo;
    }

    private Map<Long, String> getCategoryMap() {
        List<CrmProductCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<CrmProductCategory>()
                        .eq(CrmProductCategory::getTenantId, UserContextHolder.getTenantId())
        );
        return categories.stream()
                .collect(Collectors.toMap(CrmProductCategory::getId, CrmProductCategory::getCategoryName));
    }

    private List<CategoryVO> buildCategoryTree(List<CategoryVO> categories, Long parentId) {
        List<CategoryVO> tree = new ArrayList<>();
        for (CategoryVO category : categories) {
            if (category.getParentId().equals(parentId)) {
                category.setChildren(buildCategoryTree(categories, category.getId()));
                tree.add(category);
            }
        }
        return tree;
    }

    private String generateProductNo() {
        return "PROD" + System.currentTimeMillis();
    }
}