<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="24" class="stat-row">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card stat-card--primary">
          <div class="stat-content">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">用户总数</div>
          </div>
          <div class="stat-icon">
            <el-icon :size="48"><User /></el-icon>
          </div>
          <div class="stat-trend up">
            <el-icon><ArrowUp /></el-icon>
            <span>12%</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card stat-card--success">
          <div class="stat-content">
            <div class="stat-value">{{ stats.roleCount }}</div>
            <div class="stat-label">角色总数</div>
          </div>
          <div class="stat-icon">
            <el-icon :size="48"><Stamp /></el-icon>
          </div>
          <div class="stat-trend up">
            <el-icon><ArrowUp /></el-icon>
            <span>8%</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card stat-card--warning">
          <div class="stat-content">
            <div class="stat-value">{{ stats.menuCount }}</div>
            <div class="stat-label">菜单总数</div>
          </div>
          <div class="stat-icon">
            <el-icon :size="48"><Menu /></el-icon>
          </div>
          <div class="stat-trend down">
            <el-icon><ArrowDown /></el-icon>
            <span>3%</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card stat-card--danger">
          <div class="stat-content">
            <div class="stat-value">{{ stats.tenantCount }}</div>
            <div class="stat-label">租户总数</div>
          </div>
          <div class="stat-icon">
            <el-icon :size="48"><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-trend up">
            <el-icon><ArrowUp /></el-icon>
            <span>15%</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-row :gutter="24" class="quick-row">
      <el-col :span="24">
        <el-card class="quick-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="quick-item" @click="$router.push('/system/user')">
              <el-icon :size="32" class="quick-icon"><User /></el-icon>
              <span>用户管理</span>
            </div>
            <div class="quick-item" @click="$router.push('/system/role')">
              <el-icon :size="32" class="quick-icon"><Stamp /></el-icon>
              <span>角色管理</span>
            </div>
            <div class="quick-item" @click="$router.push('/system/menu')">
              <el-icon :size="32" class="quick-icon"><Menu /></el-icon>
              <span>菜单管理</span>
            </div>
            <div class="quick-item" @click="$router.push('/system/dept')">
              <el-icon :size="32" class="quick-icon"
                ><OfficeBuilding
              /></el-icon>
              <span>部门管理</span>
            </div>
            <div class="quick-item" @click="$router.push('/system/tenant')">
              <el-icon :size="32" class="quick-icon"><House /></el-icon>
              <span>租户管理</span>
            </div>
            <div class="quick-item" @click="$router.push('/system/log/login')">
              <el-icon :size="32" class="quick-icon"><Document /></el-icon>
              <span>登录日志</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统信息 -->
    <el-row :gutter="24">
      <el-col :xs="24" :md="12">
        <el-card class="info-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">系统信息</span>
            </div>
          </template>
          <el-descriptions :column="1" border class="system-desc">
            <el-descriptions-item label="系统名称">
              <el-tag type="primary">后管管理系统</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="系统版本">V1.0.0</el-descriptions-item>
            <el-descriptions-item label="前端框架">
              <el-tag>Vue 3.5 + Element Plus 2.11</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="后端框架">
              <el-tag>Spring Boot 3.2 + MyBatis-Plus</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="数据库">
              <el-tag type="success">MySQL 8.0</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="info-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">今日动态</span>
            </div>
          </template>
          <div class="today-stats">
            <div class="today-item">
              <div class="today-value">128</div>
              <div class="today-label">登录次数</div>
            </div>
            <div class="today-item">
              <div class="today-value">56</div>
              <div class="today-label">操作次数</div>
            </div>
            <div class="today-item">
              <div class="today-value">5</div>
              <div class="today-label">新增用户</div>
            </div>
            <div class="today-item">
              <div class="today-value">3</div>
              <div class="today-label">活跃租户</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

const stats = ref({
  userCount: 100,
  roleCount: 50,
  menuCount: 80,
  tenantCount: 10,
});
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 0;
}

.stat-row {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s ease;
  animation: cardSlideUp 0.5s ease-out backwards;

  &:nth-child(1) {
    animation-delay: 0.1s;
  }
  &:nth-child(2) {
    animation-delay: 0.2s;
  }
  &:nth-child(3) {
    animation-delay: 0.3s;
  }
  &:nth-child(4) {
    animation-delay: 0.4s;
  }

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 16px 32px rgba(0, 0, 0, 0.12);
  }

  &:focus-visible {
    outline: 2px solid rgba(64, 158, 255, 0.5);
    outline-offset: 2px;
  }

  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 4px;
  }

  &--primary::before {
    background: linear-gradient(90deg, #409eff, #667eea);
  }

  &--success::before {
    background: linear-gradient(90deg, #67c23a, #85ce61);
  }

  &--warning::before {
    background: linear-gradient(90deg, #e6a23c, #ebb563);
  }

  &--danger::before {
    background: linear-gradient(90deg, #f56c6c, #f78989);
  }
}

@keyframes cardSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-icon {
  position: absolute;
  right: 24px;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0.2;
  color: #303133;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  margin-top: 12px;

  &.up {
    color: #67c23a;
  }

  &.down {
    color: #f56c6c;
  }
}

.quick-row {
  margin-bottom: 24px;
}

.quick-card {
  border-radius: 12px;
  border: none;
  background: #fff;

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .card-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 24px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px 16px;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.25s ease;
  background: #f5f7fa;
  position: relative;
  overflow: hidden;

  // 添加背景动画效果
  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(64, 158, 255, 0.1),
      transparent
    );
    transition: left 0.5s ease;
  }

  &:hover {
    background: #e8f4ff;
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(64, 158, 255, 0.15);

    // 激活背景动画
    &::before {
      left: 100%;
    }

    .quick-icon {
      color: #409eff;
      transform: scale(1.1);
    }
  }

  &:focus-visible {
    outline: 2px solid rgba(64, 158, 255, 0.5);
    outline-offset: 2px;
  }

  &:active {
    transform: translateY(-2px);
  }
}

.quick-icon {
  color: #606266;
  transition: all 0.25s ease;
}

.quick-item span {
  font-size: 14px;
  color: #606266;
}

.info-card {
  border-radius: 12px;
  border: none;
  background: #fff;

  .card-header {
    display: flex;
    align-items: center;
  }

  .card-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
}

.system-desc {
  :deep(.el-descriptions__label) {
    width: 100px;
    font-weight: 500;
  }
}

.today-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.today-item {
  text-align: center;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.25s ease;
  cursor: default;

  &:hover {
    background: #e8f4ff;
    transform: translateY(-2px);

    .today-value {
      color: #667eea;
    }
  }
}

.today-value {
  font-size: 28px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 8px;
  transition: color 0.25s ease;
}

.today-label {
  font-size: 14px;
  color: #909399;
}

@media (max-width: 768px) {
  .quick-actions {
    grid-template-columns: repeat(3, 1fr);
  }

  .today-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

// 减少动画偏好
@media (prefers-reduced-motion: reduce) {
  .stat-card,
  .quick-item,
  .today-item {
    animation: none;
    transition: none;

    &:hover {
      transform: none;
    }

    &::before {
      transition: none;
    }
  }
}
</style>