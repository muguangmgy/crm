import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'

/**
 * 权限指令
 * 使用方式: v-permission="'system:user:add'" 或 v-permission="['system:user:add', 'system:user:edit']"
 * 多个权限时，满足任一权限即可显示
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
    const userStore = useUserStore()
    const value = binding.value

    if (!value) return

    const permissions = Array.isArray(value) ? value : [value]
    const hasPermission = permissions.some(perm => userStore.hasPermission(perm))

    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}

export default permission