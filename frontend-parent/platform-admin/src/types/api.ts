/** 统一API响应结构 */
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}

/** 分页查询参数 */
export interface PageQuery {
  pageNum?: number
  pageSize?: number
}

/** 分页响应结构 */
export interface PageVO<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

/** 状态类型：0禁用 1启用 */
export type Status = 0 | 1

/** 通用选项类型 */
export interface OptionVO {
  id: number
  name: string
  code?: string
}