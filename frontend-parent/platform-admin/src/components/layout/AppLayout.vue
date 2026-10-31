<template>
  <el-container class="app-layout">
    <el-aside :width="appStore.sidebarCollapsed ? '64px' : '200px'" class="app-aside">
      <Sidebar />
    </el-aside>
    <el-container class="app-body">
      <el-header class="app-header">
        <Header />
      </el-header>
      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/app'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'

const appStore = useAppStore()
</script>

<style lang="scss" scoped>
.app-layout {
  height: 100vh;
  background: #f0f2f5;
}

.app-aside {
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  transition: width 0.3s ease;
  overflow: hidden;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.25);
}

.app-body {
  display: flex;
  flex-direction: column;
}

.app-header {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.app-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>