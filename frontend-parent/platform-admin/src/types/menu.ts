import type { Status } from './api'

/** 菜单类型：1目录 2菜单 3按钮 */
export type MenuType = 1 | 2 | 3

/** 菜单信息 */
export interface MenuVO {
  id: number
  menuName: string
  menuType: MenuType
  parentId: number | null
  path?: string
  component?: string
  perms?: string
  icon?: string
  sort: number
  visible: number
  status: Status
  createTime: string
  children?: MenuVO[]
}

/** 菜单查询参数 */
export interface MenuQueryDTO {
  pageNum?: number
  pageSize?: number
  menuName?: string
  menuType?: MenuType
  status?: Status
}

/** 菜单保存参数 */
export interface MenuSaveDTO {
  id?: number
  menuName: string
  menuType: number
  parentId: number | null
  path?: string
  component?: string
  perms?: string
  icon?: string
  sort: number
  visible: number
  status: Status
}