<template>
  <div class="sidebar" :class="{ 'sidebar-collapsed': collapsed }">
    <div class="logo">
      <div class="logo-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="logo-svg">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke-linecap="round" stroke-linejoin="round"/>
          <circle cx="9" cy="7" r="4"/>
          <path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M16 3.13a4 4 0 0 1 0 7.75" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span v-show="!collapsed" class="logo-text">CRM 管理系统</span>
    </div>
    <el-scrollbar class="menu-scrollbar">
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :collapse-transition="false"
        router
        class="sidebar-menu"
        background-color="#1a1a2e"
        text-color="rgba(255, 255, 255, 0.85)"
        active-text-color="#409eff"
      >
        <template v-for="menu in menuStore.menuTree" :key="menu.id">
          <SidebarMenuItem :menu="menu" />
        </template>
      </el-menu>
    </el-scrollbar>
    <!-- 角色切换区域 -->
    <div class="role-switcher" v-if="userStore.userInfo?.roles?.length">
      <el-dropdown
        v-if="userStore.userInfo.roles.length > 1"
        @command="handleSwitchRole"
        trigger="click"
        placement="top"
        popper-class="sidebar-popper"
      >
        <div class="role-display">
          <div class="role-info">
            <el-icon class="role-icon">
              <Avatar />
            </el-icon>
            <span class="role-name" v-show="!collapsed">{{
              userStore.currentRole?.roleName
            }}</span>
          </div>
          <el-icon class="arrow-icon" v-show="!collapsed">
            <ArrowUp />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="role in userStore.userInfo.roles"
              :key="role.roleId"
              :command="role.roleId"
            >
              <div class="role-option">
                <el-icon>
                  <User />
                </el-icon>
                <span
                  :class="{
                    'active-role':
                      role.roleId === userStore.currentRole?.roleId,
                  }"
                >
                  {{ role.roleName }}
                </span>
                <el-icon
                  v-if="role.roleId === userStore.currentRole?.roleId"
                  class="check-icon"
                >
                  <Check />
                </el-icon>
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <div v-else class="role-display single">
        <div class="role-info">
          <el-icon class="role-icon">
            <User />
          </el-icon>
          <span class="role-name" v-show="!collapsed">{{
            userStore.userInfo.roles[0].roleName
          }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";
import { ArrowUp, Check, User } from "@element-plus/icons-vue";
import { useAppStore } from "@/stores/app";
import { useMenuStore } from "@/stores/menu";
import { useUserStore } from "@/stores/user";
import SidebarMenuItem from "./SidebarMenuItem.vue";

const route = useRoute();
const appStore = useAppStore();
const menuStore = useMenuStore();
const userStore = useUserStore();

const collapsed = computed(() => appStore.sidebarCollapsed);
const activeMenu = computed(() => route.path);

async function handleSwitchRole(roleId: number) {
  await userStore.switchRole(roleId);
}
</script>

<style lang="scss" scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
  background: linear-gradient(135deg, #0f3460 0%, #1a1a2e 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 8px;
  color: #fff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);

  .logo-svg {
    width: 22px;
    height: 22px;
  }
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
  letter-spacing: 1px;
}

.menu-scrollbar {
  flex: 1;
}

.sidebar-menu {
  border-right: none;
  background: transparent;

  // 关键：设置 Element Plus 菜单 CSS 变量，控制弹出层颜色
  --el-menu-bg-color: transparent;
  --el-menu-text-color: rgba(255, 255, 255, 0.85);
  --el-menu-active-color: #409eff;
  --el-menu-item-hover-bg-color: rgba(64, 158, 255, 0.15);
  --el-bg-color-overlay: #1a1a2e;

  &:not(.el-menu--collapse) {
    width: 200px;
  }

  // 深色主题全局样式
  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    color: rgba(255, 255, 255, 0.85);
    height: 48px;
    line-height: 48px;
    margin: 4px 8px;
    border-radius: 8px;
    background: transparent;
    font-weight: 500;

    &:hover {
      background: rgba(64, 158, 255, 0.15);
      color: #fff;
    }

    .el-icon {
      color: rgba(255, 255, 255, 0.85);
      font-size: 18px;
      margin-right: 8px;
    }
  }

  :deep(.el-menu-item.is-active) {
    color: #409eff;
    background: rgba(64, 158, 255, 0.25);

    .el-icon {
      color: #409eff;
    }
  }

  :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
    color: #409eff;

    .el-icon {
      color: #409eff;
    }
  }

  :deep(.el-sub-menu__title .el-sub-menu__icon-arrow) {
    color: rgba(255, 255, 255, 0.6);
  }

  // 折叠状态样式 - 图标完全居中
  &:deep(.el-menu--collapse) {
    width: 64px;

    // 覆盖 Element Plus CSS 变量
    --el-menu-base-level-padding: 0 !important;
    --el-menu-level-padding: 0 !important;
    --el-menu-icon-width: 0 !important;

    // 一级菜单项
    .el-menu-item {
      padding: 0 !important;
      margin: 4px 8px;
      width: calc(100% - 16px);
      height: 48px;
      display: flex !important;
      justify-content: center !important;
      align-items: center !important;

      .menu-title-content {
        display: flex !important;
        justify-content: center !important;
        align-items: center !important;
        width: 100% !important;
        padding: 0 !important;

        .menu-icon {
          margin: 0 !important;
          font-size: 18px;
        }

        .menu-text {
          display: none !important;
        }
      }
    }

    // 一级子菜单（el-sub-menu）特殊处理
    .el-sub-menu {
      .el-sub-menu__title {
        padding: 0 !important;
        margin: 4px 8px;
        width: calc(100% - 16px);
        height: 48px;
        display: flex !important;
        justify-content: center !important;
        align-items: center !important;
        padding-left: 0 !important;
        padding-right: 0 !important;

        // 关键：覆盖 Element Plus 的内部容器
        .el-sub-menu__title-inner {
          display: flex !important;
          justify-content: center !important;
          align-items: center !important;
          width: 100% !important;
          padding: 0 !important;
          padding-left: 0 !important;
          padding-right: 0 !important;
          margin: 0 !important;
        }

        // 覆盖可能的 padding wrapper
        > div,
        > span {
          padding: 0 !important;
          margin: 0 !important;
        }

        .menu-title-content {
          display: flex !important;
          justify-content: center !important;
          align-items: center !important;
          width: 100% !important;
          padding: 0 !important;
          margin: 0 !important;

          .menu-icon {
            margin: 0 !important;
            font-size: 18px;
          }

          .menu-text {
            display: none !important;
          }
        }
      }

      // 子菜单箭头隐藏
      .el-sub-menu__icon-arrow {
        display: none !important;
      }
    }
  }
}

.role-switcher {
  padding: 12px 16px;
  background: linear-gradient(135deg, #0f3460 0%, #1a1a2e 100%);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

// 折叠状态下角色切换图标居中
.sidebar-collapsed .role-switcher {
  padding: 12px 8px;
}

.sidebar-collapsed .role-display {
  justify-content: center !important;
  padding: 10px 8px !important;
  margin: 0 8px;
  width: calc(100% - 16px);
}

.sidebar-collapsed .role-info {
  justify-content: center !important;
  gap: 0 !important;
  width: 100%;
}

.role-display {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  padding: 10px 12px;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    background: rgba(64, 158, 255, 0.15);
  }

  &.single {
    cursor: default;
    justify-content: center;

    &:hover {
      background: transparent;
    }
  }
}

.role-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.role-icon {
  font-size: 18px;
  color: #409eff;
}

.role-name {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
}

.arrow-icon {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  transition: transform 0.2s;
}

.role-option {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;

  .el-icon {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.6);
  }
}

.active-role {
  color: #409eff;
  font-weight: 500;
}

.check-icon {
  color: #409eff;
  font-size: 16px;
  margin-left: auto;
}
</style>