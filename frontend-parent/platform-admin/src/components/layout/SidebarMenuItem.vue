<template>
  <template v-if="menu.menuType === 1 && menu.children?.length">
    <el-sub-menu
      :index="fullPath"
      popper-class="sidebar-popper"
    >
      <template #title>
        <div class="menu-title-content">
          <el-icon v-if="menu.icon" class="menu-icon"><component :is="menu.icon" /></el-icon>
          <span class="menu-text">{{ menu.menuName }}</span>
        </div>
      </template>
      <SidebarMenuItem
        v-for="child in menu.children"
        :key="child.id"
        :menu="child"
        :parent-path="fullPath"
      />
    </el-sub-menu>
  </template>
  <template v-else-if="menu.menuType === 2">
    <el-menu-item :index="fullPath">
      <div class="menu-title-content">
        <el-icon v-if="menu.icon" class="menu-icon"><component :is="menu.icon" /></el-icon>
        <span class="menu-text">{{ menu.menuName }}</span>
      </div>
    </el-menu-item>
  </template>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { MenuVO } from '@/types/menu'

const props = defineProps<{
  menu: MenuVO
  parentPath?: string
}>()

const fullPath = computed(() => {
  const menuPath = props.menu.path || `menu-${props.menu.id}`

  if (menuPath.startsWith('/')) {
    return menuPath
  }

  const parent = props.parentPath || ''
  return parent + '/' + menuPath
})
</script>

<style lang="scss" scoped>
.menu-title-content {
  display: flex;
  align-items: center;
  width: 100%;
}

.menu-icon {
  font-size: 18px;
}

.menu-text {
  margin-left: 8px;
  white-space: nowrap;
}

// 折叠状态下隐藏文字
:deep(.el-menu--collapse) {
  .menu-title-content {
    justify-content: center;
  }

  .menu-text {
    display: none;
  }

  .menu-icon {
    margin: 0;
  }
}
</style>