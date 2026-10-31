package com.platform.server.crm.product.service;

import com.platform.server.common.vo.PageVO;
import com.platform.server.crm.product.dto.CategorySaveDTO;
import com.platform.server.crm.product.dto.ProductQueryDTO;
import com.platform.server.crm.product.dto.ProductSaveDTO;
import com.platform.server.crm.product.vo.CategoryVO;
import com.platform.server.crm.product.vo.ProductVO;

import java.util.List;

public interface ProductService {

    /** 产品分页查询 */
    PageVO<ProductVO> getProductPage(ProductQueryDTO query);

    /** 产品详情 */
    ProductVO getProductById(Long id);

    /** 保存产品（新增/修改） */
    Long saveProduct(ProductSaveDTO dto);

    /** 删除产品 */
    void deleteProduct(Long id);

    /** 获取所有分类树 */
    List<CategoryVO> getCategoryTree();

    /** 获取分类列表 */
    List<CategoryVO> getCategoryList();

    /** 保存分类（新增/修改） */
    Long saveCategory(CategorySaveDTO dto);

    /** 删除分类 */
    void deleteCategory(Long id);

    /** 根据分类ID获取分类名称 */
    String getCategoryName(Long categoryId);
}