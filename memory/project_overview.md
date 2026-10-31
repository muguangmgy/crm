---
name: CRM系统项目概述
description: CRM后台管理系统的整体架构、技术栈和模块结构
type: project
---

# CRM后台管理系统

**Why:** 这是一个基于Spring Boot + Vue 3的多租户CRM后台管理系统，平台底座与CRM业务模块均已完成。

**How to apply:** 开发新功能时需遵循现有的分层架构、命名规范和代码规范（见CLAUDE.md）。

## 项目定位

- 基于地下管网智能运维平台基础框架开发的CRM系统
- B2B销售场景的客户生命周期管理：线索 → 商机 → 报价 → 合同 → 收款
- 配套销售仪表盘、分析报表与预警

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
| 文件存储 | FastDFS | 1.29 |
| 前端框架 | Vue 3 + TypeScript | 3.5.32 |
| 前端构建 | Vite | 6.3.5 |
| 前端组件 | Element Plus | 2.13.7 |
| 前端状态 | Pinia | 3.0.4 |
| 图表 | ECharts | 5.6 |

## 项目结构

```
crm/
├── platform-parent/              # 后端 Maven 多模块项目
│   ├── platform-common/          # 公共模块（全局依赖、工具类、配置）
│   ├── platform-config/          # 配置模块（application.yml、SQL脚本）
│   │   └── resources/db/crm.sql  # 数据库初始化脚本
│   └── platform-server/          # 业务服务模块
│       └── com.platform.server/
│           ├── auth/             # 认证模块
│           ├── system/           # 系统管理（已完成）
│           └── crm/              # CRM业务模块（已完成）
│               ├── customer/     # 客户/公海/联系人/跟进/标签
│               ├── lead/         # 线索管理
│               ├── opportunity/  # 商机管理
│               ├── product/      # 产品管理
│               ├── quote/        # 报价管理
│               ├── contract/     # 合同管理
│               ├── invoice/      # 发票管理
│               ├── payment/      # 收款管理
│               ├── analysis/     # 数据分析与仪表盘
│               └── task/         # 定时任务
│
├── frontend-parent/
│   └── platform-admin/           # Vue 3 管理后台
│       └── src/views/crm/        # CRM业务页面（含 dashboard、analysis）
│
└── docs/superpowers/specs/       # 业务设计文档
```

## 开发阶段

| 阶段 | 模块 | 状态 |
|------|------|------|
| Phase 1–5 | 平台底座（认证、系统管理、权限、租户、日志） | ✅ 已完成 |
| CRM-1 | 客户管理（含公海池、联系人、跟进、标签） | ✅ 已完成 |
| CRM-2 | 产品管理 | ✅ 已完成 |
| CRM-3 | 线索 + 商机管理 | ✅ 已完成 |
| CRM-4 | 报价 + 合同 + 发票 + 收款 | ✅ 已完成 |
| CRM-5 | 数据分析（仪表盘、销售/客户/商机/合同分析、预警） | ✅ 已完成 |

## 运行配置

- 后端端口: 8081
- 前端端口: 3000
- API代理: `/api` → `http://localhost:8081`
- 数据库: `crm`（脚本：`platform-config/.../db/crm.sql`）
- 默认管理员: `admin` / `123456`
- Git 远程: `github` → `https://github.com/muguangmgy/crm.git`（分支 `master`）
