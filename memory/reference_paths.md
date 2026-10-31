---
name: 项目关键路径引用
description: 项目中重要文件、配置和资源的路径索引
type: reference
---

# 关键路径引用

**Why:** 快速定位项目核心文件，避免重复查找。

**How to apply:** 需要访问配置、数据库、设计文档时直接引用这些路径。

## 后端核心路径

| 资源 | 路径 |
|------|------|
| 父POM | `platform-parent/pom.xml` |
| 公共模块POM | `platform-parent/platform-common/pom.xml` |
| 业务服务POM | `platform-parent/platform-server/pom.xml` |
| 开发配置 | `platform-parent/platform-config/src/main/resources/application-dev.yml` |
| 数据库脚本 | `platform-parent/platform-config/src/main/resources/db/V1__init_database.sql` |
| Mapper XML | `platform-parent/platform-server/src/main/resources/mapper/system/` |

## 前端核心路径

| 资源 | 路径 |
|------|------|
| package.json | `frontend-parent/platform-admin/package.json` |
| Vite配置 | `frontend-parent/platform-admin/vite.config.ts` |
| API模块 | `frontend-parent/platform-admin/src/api/` |
| 路由配置 | `frontend-parent/platform-admin/src/router/` |
| 类型定义 | `frontend-parent/platform-admin/src/types/` |
| 系统页面 | `frontend-parent/platform-admin/src/views/system/` |

## 设计文档路径

| 资源 | 路径 |
|------|------|
| CRM业务设计 | `docs/superpowers/specs/2026-04-28-crm-business-design.md` |
| 计划目录 | `docs/superpowers/plans/` |

## 业务模块包路径

| 模块 | Java包路径 |
|------|------------|
| 认证 | `com.platform.server.auth` |
| 用户 | `com.platform.server.system.user` |
| 角色 | `com.platform.server.system.role` |
| 菜单 | `com.platform.server.system.menu` |
| 部门 | `com.platform.server.system.dept` |
| 租户 | `com.platform.server.system.tenant` |
| 权限点 | `com.platform.server.system.permission` |
| 登录日志 | `com.platform.server.system.log.login` |
| 操作日志 | `com.platform.server.system.log.oper` |

## 公共模块包路径

| 组件 | Java包路径 |
|------|------------|
| 配置类 | `com.platform.common.config` |
| 常量 | `com.platform.common.constants` |
| 枚举 | `com.platform.common.enums` |
| 异常处理 | `com.platform.common.exception` |
| 工具类 | `com.platform.common.utils` |
| 用户上下文 | `com.platform.common.context.UserContextHolder` |
| 公共响应 | `com.platform.common.vo.Result` |