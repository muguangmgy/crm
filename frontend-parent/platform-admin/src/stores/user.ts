import {defineStore} from 'pinia'
import {computed, ref} from 'vue'
import {authApi} from '@/api/auth'
import type {LoginDTO, RoleInfoVO, UserInfoVO} from '@/types/user'
import {useMenuStore} from '@/stores/menu'
import router, {refreshRoutes, resetRouterState} from '@/router'

const TOKEN_KEY = 'platform_admin_token'

export const useUserStore = defineStore('user', () => {
    const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
    const userInfo = ref<UserInfoVO | null>(null)
    const permissions = ref<string[]>([])
    const currentRole = ref<RoleInfoVO | null>(null)

    const isLoggedIn = computed(() => !!token.value && !!userInfo.value)

    // 是否平台角色
    const isPlatformRole = computed(() => userInfo.value?.isPlatformRole ?? false)

    // 当前租户ID
    const currentTenantId = computed(() => userInfo.value?.tenantId)

    function setToken(newToken: string) {
        token.value = newToken
        localStorage.setItem(TOKEN_KEY, newToken)
    }

    function clearToken() {
        token.value = ''
        userInfo.value = null
        permissions.value = []
        currentRole.value = null
        localStorage.removeItem(TOKEN_KEY)
        // 清除菜单缓存和路由状态
        const menuStore = useMenuStore()
        menuStore.clearMenus()
        resetRouterState()
    }

    async function login(loginData: LoginDTO): Promise<boolean> {
        try {
            const res = await authApi.login(loginData)
            // 先清除旧的菜单缓存和路由状态，再设置新token
            const menuStore = useMenuStore()
            menuStore.clearMenus()
            resetRouterState()
            setToken(res.accessToken)
            await fetchUserInfo()
            return true
        } catch {
            return false
        }
    }

    async function fetchUserInfo(): Promise<void> {
        if (!token.value) return
        try {
            const info = await authApi.getUserInfo()
            userInfo.value = info
            permissions.value = info.permissions || []
            await initDefaultRole()
        } catch {
            clearToken()
        }
    }

    async function initDefaultRole(): Promise<void> {
        if (userInfo.value?.roles?.length) {
            // 优先使用后端返回的当前角色，否则选择第一个
            const currentRoleId = userInfo.value.currentRoleId
            if (currentRoleId) {
                currentRole.value = userInfo.value.roles.find(r => r.roleId === currentRoleId) || userInfo.value.roles[0]
            } else {
                currentRole.value = userInfo.value.roles[0]
            }
            const menuStore = useMenuStore()
            await menuStore.fetchUserMenus(currentRole.value.roleId)
        }
    }

    async function switchRole(roleId: number): Promise<void> {
        // 先清除菜单缓存和路由状态
        const menuStore = useMenuStore()
        menuStore.clearMenus()
        resetRouterState()
        // 调用菜单接口（后端会更新当前角色到 Redis）
        await menuStore.fetchUserMenus(roleId)
        // 立即添加动态路由（确保路由已生效）
        refreshRoutes()
        // 重新获取用户信息，获取新角色的权限数据
        const info = await authApi.getUserInfo()
        userInfo.value = info
        permissions.value = info.permissions || []
        // 更新当前角色
        if (info.currentRoleId) {
            currentRole.value = info.roles.find(r => r.roleId === info.currentRoleId) || info.roles[0]
        } else {
            currentRole.value = info.roles[0]
        }
        // 触发页面数据刷新
        menuStore.triggerDataRefresh()
    }

    async function logout(): Promise<void> {
        try {
            await authApi.logout()
        } finally {
            clearToken()
            router.push('/login')
        }
    }

    function hasPermission(perm: string): boolean {
        return permissions.value.includes(perm) || permissions.value.includes('*:*:*')
    }

    return {
        token,
        userInfo,
        permissions,
        currentRole,
        isLoggedIn,
        isPlatformRole,
        currentTenantId,
        setToken,
        clearToken,
        login,
        fetchUserInfo,
        initDefaultRole,
        switchRole,
        logout,
        hasPermission
    }
})