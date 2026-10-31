import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'

// 静态路由（不需要登录，无布局）
const staticRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  }
]

// CRM隐藏路由（详情页等，不显示在菜单中）
const crmHiddenRoutes: RouteRecordRaw[] = [
  {
    path: '/crm/customer/detail/:id',
    name: 'CustomerDetail',
    component: () => import('@/views/crm/customer/detail.vue'),
    meta: { title: '客户详情' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes
})

// 白名单
const whiteList = ['/login']

// 标记路由是否已添加（独立于 menuStore.isLoaded）
let routesAdded = false

// 添加动态路由的函数
function addDynamicRoutes(): void {
  const menuStore = useMenuStore()

  // 先移除旧的动态路由（如果存在）
  if (router.hasRoute('Layout')) {
    router.removeRoute('Layout')
  }
  if (router.hasRoute('CatchAll')) {
    router.removeRoute('CatchAll')
  }

  // 添加 Layout 路由
  router.addRoute({
    path: '/',
    name: 'Layout',
    component: () => import('@/components/layout/AppLayout.vue'),
    redirect: () => menuStore.getDefaultHomePath(),
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: '404',
        name: 'NotFound',
        component: () => import('@/views/error/404.vue'),
        meta: { title: '404' }
      },
      ...crmHiddenRoutes,
      ...menuStore.routes
    ]
  })

  // 404兜底（未匹配的路由重定向到Layout内的404）
  router.addRoute({
    path: '/:pathMatch(.*)*',
    name: 'CatchAll',
    redirect: '/404'
  })

  routesAdded = true
}

router.beforeEach(async (to) => {
  const userStore = useUserStore()
  const menuStore = useMenuStore()

  // 白名单直接放行
  if (whiteList.includes(to.path)) {
    if (to.path === '/login' && userStore.isLoggedIn) {
      if (!menuStore.isLoaded) {
        await menuStore.fetchUserMenus()
      }
      if (!routesAdded) {
        addDynamicRoutes()
      }
      return menuStore.getDefaultHomePath()
    }
    return
  }

  // 未登录跳转登录页
  if (!userStore.token) {
    return '/login'
  }

  // 获取用户信息
  if (!userStore.userInfo) {
    await userStore.fetchUserInfo()
  }

  // 动态路由未添加时
  if (!routesAdded) {
    // 获取菜单（如果未加载）
    if (!menuStore.isLoaded) {
      await menuStore.fetchUserMenus()
    }

    // 添加动态路由
    addDynamicRoutes()

    // 重新导航到目标路径（确保路由已生效）
    return { path: to.path, replace: true }
  }
})

// 重置路由状态（用于用户切换）
export function resetRouterState(): void {
  routesAdded = false
}

// 手动触发路由更新（用于角色切换后）
export function refreshRoutes(): void {
  const menuStore = useMenuStore()
  if (menuStore.isLoaded) {
    resetRouterState()
    addDynamicRoutes()
  }
}

export default router