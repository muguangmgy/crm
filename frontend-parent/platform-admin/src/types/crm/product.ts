import type { Status } from '@/types/api'

/** 产品VO */
export interface ProductVO {
  id: number
  productNo?: string
  productName: string
  productCode?: string
  categoryId?: number
  categoryName?: string
  unit?: string
  spec?: string
  model?: string
  price: number
  minPrice?: number
  costPrice?: number
  description?: string
  imageUrl?: string
  status: Status
  sort: number
  createTime: string
  updateTime: string
}

/** 产品查询DTO */
export interface ProductQueryDTO {
  pageNum?: number
  pageSize?: number
  productName?: string
  productCode?: string
  categoryId?: number
  status?: Status
  unit?: string
}

/** 产品保存DTO */
export interface ProductSaveDTO {
  id?: number
  categoryId?: number
  productName: string
  productCode?: string
  unit?: string
  spec?: string
  model?: string
  price: number
  minPrice?: number
  costPrice?: number
  description?: string
  imageUrl?: string
  status?: Status
  sort?: number
}

/** 分类VO */
export interface CategoryVO {
  id: number
  parentId: number
  categoryName: string
  sort: number
  status: Status
  createTime: string
  updateTime: string
  children?: CategoryVO[]
}

/** 分类保存DTO */
export interface CategorySaveDTO {
  id?: number
  parentId?: number
  categoryName: string
  sort?: number
  status?: Status
}