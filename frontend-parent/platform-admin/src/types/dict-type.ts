import type { Status } from './api'

/** 字典类型信息 */
export interface DictTypeVO {
  id: number
  tenantId?: number
  parentId?: number
  dictCode: string
  dictName: string
  sort?: number
  status: Status
  remark?: string
  createTime: string
}

/** 字典类型树节点 */
export interface DictTypeTreeVO {
  id: number
  tenantId: number
  tenantName: string
  parentId: number
  dictCode: string
  dictName: string
  sort: number
  status: Status
  remark?: string
  createTime: string
  children: DictTypeTreeVO[]
}

/** 字典类型查询参数 */
export interface DictTypeQueryDTO {
  pageNum?: number
  pageSize?: number
  tenantId?: number
  dictCode?: string
  dictName?: string
  status?: Status
}

/** 字典类型保存参数 */
export interface DictTypeSaveDTO {
  id?: number
  tenantId?: number
  parentId?: number
  dictCode: string
  dictName: string
  sort?: number
  status?: Status
  remark?: string
}