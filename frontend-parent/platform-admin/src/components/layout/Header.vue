<template>
  <div class="header">
    <div class="header-left">
      <div class="collapse-btn" @click="toggleSidebar">
        <el-icon :size="20">
          <component :is="appStore.sidebarCollapsed ? 'Expand' : 'Fold'"/>
        </el-icon>
      </div>
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>
    <div class="header-right">
      <el-dropdown trigger="click" class="user-dropdown">
        <div class="user-info">
          <el-avatar :size="36" class="user-avatar">
            {{ userStore.userInfo?.userName?.charAt(0) || 'U' }}
          </el-avatar>
          <span class="user-name">{{ userStore.userInfo?.userName || '用户' }}</span>
          <el-icon class="dropdown-icon">
            <ArrowDown/>
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goToProfile">
              <el-icon>
                <User/>
              </el-icon>
              个人中心
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon>
                <SwitchButton/>
              </el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useAppStore} from '@/stores/app'
import {useUserStore} from '@/stores/user'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const currentTitle = computed(() => {
  return route.meta?.title as string || ''
})

function toggleSidebar() {
  appStore.toggleSidebar()
}

function goToProfile() {
  router.push('/profile')
}

function handleLogout() {
  userStore.logout()
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: #f5f7fa;
  }
}

.breadcrumb {
  :deep(.el-breadcrumb__item) {
    .el-breadcrumb__inner {
      color: #606266;
      font-weight: 400;
    }

    &:last-child .el-breadcrumb__inner {
      color: #303133;
      font-weight: 500;
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: #f5f7fa;
  }
}

.user-avatar {
  background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
  color: #fff;
  font-weight: 500;
}

.user-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.dropdown-icon {
  color: #909399;
  font-size: 12px;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;

  .el-icon {
    font-size: 16px;
  }
}
</style>