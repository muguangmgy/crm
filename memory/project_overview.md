---
name: CRM系统项目概述
description: CRM后台管理系统的整体架构、技术栈和模块结构
type: project
---

# CRM后台管理系统

**Why:** 这是一个基于Spring Boot + Vue 3的多租户CRM后台管理系统，已完成基础功能开发，正进入业务模块开发阶段。

**How to apply:** 开发新功能时需遵循现有的分层架构、命名规范和代码规范（见CLAUDE.md）。

## 项目定位

- 基于地下管网智能运维平台基础框架开发的CRM系统
- B2B/B2B销售场景的客户生命周期管理
- 采用渐进式模块开发策略

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 后端语言 | Java (OpenJDK) | 17 |
| 后端框架 | Spring Boot | 3.2.5 |
| ORM | MyBatis-Plus | 3.5.15 |
| 数据库 | MySQL | 8.0.33 |
| 认证 | Spring Security + JWT | 6.x |
| 缓存 | Redis | - |
| API文档 | SpringDoc OpenAPI | 2.5.0 |
| 工具库 | Hutool | 5.8.27 |
| 对象转换 | MapStruct | 1.5.5 |
| 前端框架 | Vue 3 + TypeScript | 3.5.32 |
| 前端构建 | Vite | 6.3.5 |
| 前端组件 | Element Plus | 2.13.7 |
| 前端状态 | Pinia | 3.0.4 |

## 项目结构

```
crm/
├── platform-parent/              # 后端 Maven 多模块项目
│   ├── platform-common/          # 公共模块（全局依赖、工具类、配置）
│   ├── platform-config/          # 配置模块（application.yml、SQL脚本）
│   └── platform-server/          # 业务服务模块
│       └── com.platform.server/
│           ├── auth/             # 认证模块（登录、JWT、刷新Token）
│           ├── system/           # 系统管理（已完成）
│           │   ├── user/         # 用户管理
│           │   ├── role/         # 角色管理
│           │   ├── menu/         # 菜单管理
│           │   ├── dept/         # 部门管理
│           │   ├── tenant/       # 租户管理
│           │   ├── permission/   # 权限点管理
│           │   └── log/          # 日志管理
│           └── crm/              # CRM业务模块（待开发）
│               ├── customer/     # 客户管理
│               ├── product/      # 产品管理
│               ├── opportunity/  # 商机管理
│               ├── contract/     # 合同管理
│               ├── payment/      # 收款管理
│               └── analysis/     # 数据分析
│
├── frontend-parent/              # 前端项目
│   └── platform-admin/           # Vue 3 管理后台
│       └── src/
│           ├── api/              # API请求模块
│           ├── components/       # 公共组件
│           ├── composables/      # 组合式函数
│           ├── router/           # 路由配置
│           ├── stores/           # Pinia状态管理
│           ├── types/            # TypeScript类型定义
│           ├── utils/            # 工具函数
│           └── views/            # 页面组件
│               ├── system/       # 系统管理页面
│               └── crm/          # CRM业务页面（待开发）
│
└── docs/superpowers/             # 设计文档
    └── specs/                    # 业务规格文档
        └── 2026-04-28-crm-business-design.md
```

## 开发阶段

| 阶段 | 模块 | 状态 |
|------|------|------|
| Phase 1 | 用户登录、JWT Token、Token刷新、注销 | ✅ 已完成 |
| Phase 2 | 用户/角色/菜单/部门管理CRUD | ✅ 已完成 |
| Phase 3 | 权限点管理、角色权限分配、租户管理、数据权限切面 | ✅ 已完成 |
| Phase 4 | 登录日志、操作日志 | ✅ 已完成 |
| Phase 5 | 多租户业务规则完善 | ✅ 已完成 |
| CRM-1 | 客户管理模块 | 📋 规划中 |
| CRM-2 | 产品管理模块 | 📋 规划中 |
| CRM-3 | 商机管理模块 | 📋 规划中 |
| CRM-4 | 合同+收款管理 | 📋 规划中 |
| CRM-5 | 数据分析模块 | 📋 规划中 |

## 运行配置

- 后端端口: 8081
- 前端端口: 3000
- API代理: `/api` → `http://localhost:8081`
- 数据库: `crm`
- 默认管理员: `admin` / `123456`