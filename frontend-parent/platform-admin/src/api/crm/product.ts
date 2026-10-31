import { http } from '@/api/request'
import type { ProductVO, ProductQueryDTO, ProductSaveDTO, CategoryVO, CategorySaveDTO } from '@/types/crm/product'
import type { PageVO } from '@/types/api'

export const productApi = {
  /** 产品列表 */
  getList(params?: ProductQueryDTO): Promise<PageVO<ProductVO>> {
    return http.get<PageVO<ProductVO>>('/crm/product/list', params)
  },

  /** 产品详情 */
  getById(id: number): Promise<ProductVO> {
    return http.get<ProductVO>(`/crm/product/${id}`)
  },

  /** 新增产品 */
  create(data: ProductSaveDTO): Promise<number> {
    return http.post<number>('/crm/product', data)
  },

  /** 修改产品 */
  update(data: ProductSaveDTO): Promise<void> {
    return http.put<void>('/crm/product', data)
  },

  /** 删除产品 */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/crm/product/${id}`)
  },

  // ==================== 分类 ====================

  /** 分类树 */
  getCategoryTree(): Promise<CategoryVO[]> {
    return http.get<CategoryVO[]>('/crm/product/category/tree')
  },

  /** 分类列表 */
  getCategoryList(): Promise<CategoryVO[]> {
    return http.get<CategoryVO[]>('/crm/product/category/list')
  },

  /** 新增分类 */
  createCategory(data: CategorySaveDTO): Promise<number> {
    return http.post<number>('/crm/product/category', data)
  },

  /** 修改分类 */
  updateCategory(data: CategorySaveDTO): Promise<void> {
    return http.put<void>('/crm/product/category', data)
  },

  /** 删除分类 */
  deleteCategory(id: number): Promise<void> {
    return http.delete<void>(`/crm/product/category/${id}`)
  }
}