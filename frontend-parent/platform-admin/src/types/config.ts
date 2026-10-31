import type { Status } from './api'

/** 值类型：1-string 2-number 3-json */
export type ValueType = 1 | 2 | 3

/** 业务参数信息 */
export interface ConfigVO {
  id: number
  tenantId?: number
  groupCode: string
  configKey: string
  configName: string
  configValue: string
  valueType: ValueType
  remark?: string
  status: Status
  createTime: string
}

/** 业务参数查询参数 */
export interface ConfigQueryDTO {
  pageNum?: number
  pageSize?: number
  tenantId?: number
  groupCode?: string
  configKey?: string
  configName?: string
  status?: Status
}

/** 业务参数保存参数 */
export interface ConfigSaveDTO {
  id?: number
  tenantId?: number
  groupCode: string
  configKey: string
  configName: string
  configValue: string
  valueType: ValueType
  status?: Status
  remark?: string
}

/** 参数分组选项 */
export interface ConfigGroupVO {
  itemValue: string
  itemLabel: string
}