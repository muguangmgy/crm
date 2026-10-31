<template>
  <div class="not-found-page">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <div class="error-message">页面不存在</div>
      <div class="error-desc">您访问的页面不存在或已被删除</div>
      <el-button type="primary" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回上一页
      </el-button>
      <el-button @click="goHome">
        <el-icon><HomeFilled /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useMenuStore } from '@/stores/menu'

const router = useRouter()
const menuStore = useMenuStore()

function goBack() {
  router.back()
}

function goHome() {
  router.push(menuStore.getDefaultHomePath())
}
</script>

<style lang="scss" scoped>
.not-found-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 400px;
}

.not-found-content {
  text-align: center;
}

.error-code {
  font-size: 120px;
  font-weight: 700;
  color: #409eff;
  line-height: 1;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.error-message {
  font-size: 24px;
  color: #303133;
  margin-bottom: 12px;
}

.error-desc {
  font-size: 14px;
  color: #909399;
  margin-bottom: 32px;
}

.el-button {
  margin: 0 8px;
}
</style>