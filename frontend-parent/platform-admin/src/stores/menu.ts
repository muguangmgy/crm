import { defineStore } from 'pinia'
import { ref } from 'vue'
import { menuApi } from '@/api/menu'
import type { MenuVO } from '@/types/menu'
import type { RouteRecordRaw } from 'vue-router'

// 预加载所有views下的Vue组件
const modules = import.meta.glob('../views/**/*.vue')

// 组件路径映射表（排除敏感目录）
const componentMap: Record<string, () => Promise<unknown>> = {}
for (const path in modules) {
  const componentPath = path.replace('../views/', '').replace('.vue', '')
  // 排除规则：只排除特定目录，不影响业务组件
  // - views/login/ 目录下的登录页面
  // - views/components/layout/ 布局组件
  // - views/error/ 错误页面
  const isExcluded =
    componentPath.startsWith('login/') ||
    componentPath.startsWith('components/layout') ||
    componentPath.startsWith('error')

  if (isExcluded) continue

  componentMap[componentPath] = modules[path] as () => Promise<unknown>
  console.log(`[组件映射] ${componentPath} -> ${path}`)
}

export const useMenuStore = defineStore('menu', () => {
  const menuTree = ref<MenuVO[]>([])
  const routes = ref<RouteRecordRaw[]>([])
  const isLoaded = ref(false)
  // 数据刷新触发器，切换角色后递增，页面监听此值重新加载数据
  const dataRefreshTrigger = ref(0)

  async function fetchUserMenus(roleId?: number): Promise<void> {
    if (isLoaded.value) return
    try {
      const menus = await menuApi.getUserMenus(roleId)
      menuTree.value = menus
      routes.value = buildFlatRoutes(menus)
      isLoaded.value = true
    } catch {
      menuTree.value = []
      routes.value = []
    }
  }

  /**
   * 获取组件加载函数（带安全检查和路径规范化）
   */
  function getComponent(componentPath: string): (() => Promise<unknown>) | undefined {
    if (!componentPath) return undefined

    // 规范化组件路径
    let normalizedPath = componentPath
      .replace(/^views\//, '')        // 移除views前缀
      .replace(/^\/views\//, '')      // 移除/views前缀
      .replace(/^src\/views\//, '')   // 移除src/views前缀
      .replace(/\.vue$/, '')          // 移除.vue后缀
      .replace(/^\//, '')             // 移除开头斜杠

    // 安全检查：排除敏感目录
    const isExcluded =
      normalizedPath.startsWith('login/') ||
      normalizedPath.startsWith('components/layout') ||
      normalizedPath.startsWith('error')

    if (isExcluded) {
      console.warn(`禁止加载敏感组件: ${normalizedPath}`)
      return undefined
    }

    const loader = componentMap[normalizedPath]
    if (!loader) {
      console.warn(`组件路径映射失败: 原始[${componentPath}] -> 规范化[${normalizedPath}]`)
      console.log('可用的组件路径:', Object.keys(componentMap))
    }
    return loader
  }

  /**
   * 构建扁平化路由
   */
  function buildFlatRoutes(menus: MenuVO[]): RouteRecordRaw[] {
    const result: RouteRecordRaw[] = []
    const addedPaths = new Set<string>()

    function processMenu(menu: MenuVO, parentFullPath: string) {
      // 按钮跳过
      if (menu.menuType === 3) return

      // 计算完整路径
      let fullPath = ''
      if (menu.path?.startsWith('/')) {
        fullPath = menu.path
      } else if (menu.path) {
        fullPath = parentFullPath + '/' + menu.path
      } else {
        fullPath = parentFullPath + '/menu-' + menu.id
      }

      // 目录类型：只递归子菜单
      if (menu.menuType === 1) {
        if (menu.children?.length) {
          for (const child of menu.children) {
            processMenu(child, fullPath)
          }
        }
        return
      }

      // 菜单类型：生成路由
      if (menu.menuType === 2) {
        const routePath = fullPath.startsWith('/') ? fullPath.slice(1) : fullPath

        // 防重复 & 跳过dashboard
        if (addedPaths.has(routePath) || routePath === 'dashboard') return
        addedPaths.add(routePath)

        // 获取组件
        const componentLoader = getComponent(menu.component || '')

        if (!componentLoader) {
          console.warn(`菜单[${menu.menuName}] component[${menu.component}] 无效`)
          return
        }

        result.push({
          path: routePath,
          name: `menu-${menu.id}`,
          component: componentLoader,
          meta: {
            title: menu.menuName,
            icon: menu.icon,
            id: menu.id,
            perms: menu.perms
          }
        })

        // 递归子菜单
        if (menu.children?.length) {
          for (const child of menu.children) {
            processMenu(child, fullPath)
          }
        }
      }
    }

    for (const menu of menus) {
      processMenu(menu, '')
    }

    return result
  }

  function clearMenus(): void {
    menuTree.value = []
    routes.value = []
    isLoaded.value = false
  }

  // 触发数据刷新，切换角色后调用
  function triggerDataRefresh(): void {
    dataRefreshTrigger.value++
  }

  /** 登录后默认首页：优先仪表盘，否则第一个有权限菜单 */
  function getDefaultHomePath(): string {
    const routePaths = routes.value.map((route) => {
      const path = String(route.path || '')
      return path.startsWith('/') ? path : `/${path}`
    })

    if (routePaths.includes('/crm/dashboard')) return '/crm/dashboard'
    if (routePaths.includes('/dashboard')) return '/dashboard'
    if (routePaths.length > 0) return routePaths[0]
    return '/profile'
  }

  return {
    menuTree,
    routes,
    isLoaded,
    dataRefreshTrigger,
    fetchUserMenus,
    clearMenus,
    triggerDataRefresh,
    getDefaultHomePath
  }
})