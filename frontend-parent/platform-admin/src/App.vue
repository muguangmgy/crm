<template>
  <el-config-provider :dialog="dialogConfig">
    <router-view />
  </el-config-provider>
</template>

<script setup lang="ts">
// 布局由路由控制；通过 ConfigProvider 统一 Element Plus 弹窗垂直居中
const dialogConfig = {
  alignCenter: true
}
</script>

<style lang="scss">
#app {
  height: 100vh;
}
</style>
