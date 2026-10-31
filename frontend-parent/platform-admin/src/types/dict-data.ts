import type { Status } from './api'

/** 字典项信息 */
export interface DictDataVO {
  id: number
  tenantId?: number
  dictCode: string
  itemValue: string
  itemLabel: string
  sort: number
  status: Status
  remark?: string
  createTime: string
}

/** 字典项查询参数 */
export interface DictDataQueryDTO {
  pageNum?: number
  pageSize?: number
  tenantId?: number
  dictCode?: string
  itemLabel?: string
  status?: Status
}

/** 字典项保存参数 */
export interface DictDataSaveDTO {
  id?: number
  tenantId?: number
  dictCode: string
  itemValue: string
  itemLabel: string
  sort?: number
  status?: Status
  remark?: string
}

/** 字典项批量保存参数 */
export interface DictDataBatchSaveDTO {
  dictCode: string
  tenantId?: number
  items: DictDataItemDTO[]
  deleteIds?: number[]
}

/** 字典项数据 */
export interface DictDataItemDTO {
  id?: number
  itemLabel: string
  itemValue: string
  sort?: number
  status?: Status
  remark?: string
}