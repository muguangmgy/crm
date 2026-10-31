import type {Status} from './api'

/** 用户信息 */
export interface UserVO {
    id: number
    userCode: string
    userName: string
    phone?: string
    email?: string
    avatar?: string
    deptId?: number
    deptName?: string
    tenantId?: number
    roleIds?: number[]
    status: Status
    createTime: string
}

/** 用户查询参数 */
export interface UserQueryDTO {
    pageNum?: number
    pageSize?: number
    tenantId?: number
    userCode?: string
    userName?: string
    phone?: string
    status?: Status
    deptId?: number
}

/** 用户保存参数 */
export interface UserSaveDTO {
    id?: number
    userCode: string
    userName: string
    password?: string
    phone?: string
    email?: string
    deptId?: number
    roleIds?: number[]
    status: Status
}

/** 登录请求参数 */
export interface LoginDTO {
    userCode: string
    password: string
    captcha: string
    rememberMe?: boolean
}

/** 登录响应 */
export interface LoginVO {
    accessToken: string
    expireTime: number
}

/** 角色信息 */
export interface RoleInfoVO {
    roleId: number
    roleCode: string
    roleName: string
    dataScope: number
}

/** 当前用户信息 */
export interface UserInfoVO {
    userId: number
    userCode: string
    userName: string
    avatar?: string
    tenantId: number
    tenantCode: string
    deptId: number
    isPlatformRole: boolean
    roles: RoleInfoVO[]
    currentRoleId?: number
    permissions: string[]
}

/** 验证码响应 */
export interface CaptchaVO {
    captchaImage: string
}

/** 个人信息修改参数 */
export interface ProfileUpdateDTO {
    userName?: string
    avatar?: string
}

/** 密码修改参数 */
export interface PasswordChangeDTO {
    oldPassword: string
    newPassword: string
}