# 后台管理基础功能

## 项目概述

后台管理基础功能主要实现常见项目管理后台的基础菜单、角色、用户、权限、租户等功能，为实现其他业务功能提供基础支撑的复用模块。

## 构建与运行命令

### 后端（Java/Spring Boot）

```bash
# 在 platform-parent 目录下执行
cd platform-parent

# 编译项目（跳过测试）
mvn clean compile -DskipTests

# 运行测试
mvn test

# 运行单个测试类
mvn test -Dtest=UserServiceTest

# 运行单个测试方法
mvn test -Dtest=UserServiceTest#testCreateUser

# 打包项目
mvn clean package -DskipTests -Pdev

# 启动服务（开发环境）
mvn spring-boot:run -pl platform-server -Pdev
```

**后端端口：** 8081
**API文档：** http://localhost:8081/swagger-ui.html

### 前端（Vue 3 + Vite）

```bash
# 在 frontend-parent/platform-admin 目录下执行
cd frontend-parent/platform-admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

**前端端口：** 3000
**代理配置：** `/api` 请求自动代理到 `http://localhost:8081`

### 前后端联调

1. 先启动后端服务（端口 8081）
2. 再启动前端开发服务器（端口 3000）
3. 前端通过 `/api` 路径访问后端接口，Vite 自动代理

## 技术栈

| 类别    | 技术                    | 版本     |
|-------|-----------------------|--------|
| 语言    | Java (OpenJDK)        | 17     |
| 框架    | Spring Boot           | 3.2.5  |
| ORM   | MyBatis-Plus          | 3.5.15 |
| 数据库   | MySQL                 | 8.0.33 |
| 认证    | Spring Security + JWT | 6.x    |
| 缓存    | Redis                 | -      |
| API文档 | SpringDoc OpenAPI     | 2.5.0  |
| 工具库   | Hutool                | 5.8.27 |
| 文件存储  | FastDFS               | 1.29   |

## 项目结构

```
platform-parent/
├── platform-common/          # 公共模块（全局依赖）
│   └── com.platform.common/
│       ├── config/           # 配置类（Redis、MyBatis、FastDFS、线程池等）
│       ├── constants/        # 常量
│       ├── enums/            # 枚举
│       ├── exception/        # 异常处理
│       ├── utils/            # 工具类
│       ├── vo/               # 公共响应对象（Result）
│       └── client/           # 外部服务客户端
│
├── platform-config/          # 配置模块
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-prod.yml
│       └── db/crm.sql        # 数据库初始化脚本
│
├── platform-server/          # 业务服务模块
│   └── com.platform.server/
│       ├── auth/             # 认证模块
│       ├── system/           # 系统管理
│       │   ├── user/         # 用户管理
│       │   ├── role/         # 角色管理
│       │   ├── menu/         # 菜单管理
│       │   ├── dept/         # 部门管理
│       │   ├── tenant/       # 租户管理
│       │   ├── dicttype/     # 字典类型
│       │   ├── dictdata/     # 字典数据
│       │   └── log/          # 日志管理
│       ├── crm/              # CRM业务
│       │   ├── customer/     # 客户/公海/联系人/跟进/标签
│       │   ├── lead/         # 线索
│       │   ├── opportunity/  # 商机
│       │   ├── product/      # 产品
│       │   ├── quote/        # 报价
│       │   ├── contract/     # 合同
│       │   ├── invoice/      # 发票
│       │   ├── payment/      # 收款
│       │   ├── analysis/     # 数据分析与仪表盘
│       │   └── task/         # 定时任务
│       └── common/           # 业务公共
│           ├── context/      # 用户上下文
│           ├── constants/    # 业务常量
│           ├── query/        # 分页查询基类
│           └── vo/           # 分页响应基类
│
└── docs/superpowers/         # 设计文档与计划
    ├── specs/                # 规范文档
    └── plans/                # 实现计划
```

## 业务模块分层规范

每个业务子模块遵循统一分层：

```
user/
├── controller/       # UserController - 接口层，REST API
├── service/          # UserService / UserServiceImpl - 业务层
│   └── impl/
├── mapper/           # UserMapper - 数据访问层
├── po/               # SysUser - 持久化对象（数据库实体）
├── dto/              # UserSaveDTO、UserQueryDTO - 数据传输对象（请求入参）
├── vo/               # UserVO - 视图对象（响应出参）
└── converter/        # UserConverter - 对象转换（可选）
```

**命名约定：**

- PO: `Sys{Entity}` 如 `SysUser`、`SysRole`
- DTO: `{Entity}SaveDTO`、`EntityQueryDTO`
- VO: `{Entity}VO`
- Mapper: `Sys{Entity}Mapper`
- Service: `Sys{Entity}Service`

## 代码规范

### 1. Swagger注解规范

**所有API接口的请求DTO和响应VO必须添加Swagger注解：**

```java
// DTO（请求入参）
@Data
@Schema(description = "用户保存请求")
public class UserSaveDTO {
    @Schema(description = "用户ID（修改时必填）")
    private Long id;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhangsan")
    @NotBlank(message = "用户名不能为空")
    private String userCode;
}

// VO（响应出参）
@Data
@Schema(description = "用户信息响应")
public class UserVO {
    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```

**Controller必须添加完整注解：**

```java

@Tag(name = "用户管理", description = "用户CRUD接口")
@RestController
@RequestMapping("/api/system/user")
public class UserController {

    @Operation(summary = "用户列表", description = "分页查询用户列表...")
    @ApiResponse(responseCode = "200", description = "成功返回用户分页列表")
    @GetMapping("/list")
    public Result<PageVO<UserVO>> list(
            @Parameter(description = "查询条件") UserQueryDTO query) {
        return Result.success(sysUserService.getUserPage(query));
    }
}
```

### 2. DTO与VO分离规范

- **DTO**: 数据传输对象，用于接收请求参数
    - 存放位置：`模块/dto/`
    - 命名：`{Entity}SaveDTO`（新增/修改）、`{Entity}QueryDTO`（查询条件）

- **VO**: 视图对象，用于返回响应数据
    - 存放位置：`模块/vo/`
    - 命名：`{Entity}VO`（详情）、`{Entity}ListVO`（列表项，如有差异）

- **严禁混用**：不要用VO接收请求，不要用DTO返回响应

### 3. PO实体类注释规范

所有PO实体类字段必须添加注释：

```java

@Data
@TableName("sys_user")
public class SysUser {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 用户名 */
    private String userCode;

    /** 密码（BCrypt加密） */
    private String password;
}
```

### 4. MyBatis-Plus使用规范

**简单CRUD使用内置方法，不写XML：**

```java
// ✅ 正确：使用内置方法
sysUserMapper.insert(user);
sysUserMapper.

updateById(user);
sysUserMapper.

deleteById(id);
sysUserMapper.

selectById(id);
sysUserMapper.

selectList(wrapper);

// ❌ 错误：简单SQL写XML
<insert id="insertUser">...</insert>
<update id="updateUserById">...</update>
```

**需要写XML的场景：**

- 多表关联查询
- 复杂条件查询（如分页查询需要关联表）
- 批量操作（如批量插入、批量更新）
- 需要返回关联数据的查询（如查询用户同时返回部门名称）

### 5. 事务使用规范

**单条数据库操作不需要`@Transactional`：**

```java
// ✅ 正确：单条操作不加事务
public void updateUser(UserSaveDTO dto) {
    sysUserMapper.updateById(user);
}

// ✅ 正确：多表操作需要事务
@Transactional
public void saveUserWithRole(UserSaveDTO dto) {
    sysUserMapper.insert(user);
    userRoleMapper.insertBatch(user.getId(), roleIds);
}
```

**需要事务的场景：**

- 多表操作需要一致性
- 先查后改（依赖查询结果）
- 需要特定隔离级别
- 批量操作

### 6. 配置文件规范

**不要随意修改配置文件**：

- 公共依赖统一在 `platform-common/pom.xml` 管理
- 业务模块 `pom.xml` 只添加模块特有的依赖
- 配置文件修改需确认后再执行

### 7. API路径规范

| 模块   | 路径前缀                   |
|------|------------------------|
| 认证   | `/api/auth`            |
| 用户管理 | `/api/system/user`     |
| 角色管理 | `/api/system/role`     |
| 菜单管理 | `/api/system/menu`     |
| 部门管理 | `/api/system/dept`     |
| 租户管理 | `/api/system/tenant`   |
| 字典管理 | `/api/system/dict`     |
| 日志管理 | `/api/system/log`      |
| 客户管理 | `/api/crm/customer`    |
| 线索管理 | `/api/crm/lead`        |
| 商机管理 | `/api/crm/opportunity` |
| 产品管理 | `/api/crm/product`     |
| 报价管理 | `/api/crm/quote`       |
| 合同管理 | `/api/crm/contract`    |
| 发票管理 | `/api/crm/invoice`     |
| 收款管理 | `/api/crm/payment`     |
| 数据分析 | `/api/crm/analysis`    |

### 8. 分页查询规范

**查询DTO继承PageQuery：**

```java

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询条件")
public class UserQueryDTO extends PageQuery {
    @Schema(description = "用户名")
    private String userCode;
}
```

**响应使用PageVO：**

```java

@GetMapping("/list")
public Result<PageVO<UserVO>> list(UserQueryDTO query) {
    return Result.success(sysUserService.getUserPage(query));
}
```

## 认证机制

**双Token机制：**

- Access Token: 30分钟有效期，用于接口访问
- Refresh Token: 7天有效期，存储在Redis，用于刷新Access Token

**JWT Payload：**

```json
{
  "userId": 1001,
  "userCode": "admin",
  "tenantId": 1,
  "deptId": 10,
  "roles": [
    "admin"
  ]
}
```

## 开发阶段

| 阶段      | 内容                                | 状态    |
|---------|-----------------------------------|-------|
| Phase 1 | 用户登录、JWT Token、Token刷新、注销         | ✅ 已完成 |
| Phase 2 | 用户/角色/菜单/部门管理CRUD                 | ✅ 已完成 |
| Phase 3 | 权限点管理、角色权限分配、租户管理、数据权限切面          | ✅ 已完成 |
| Phase 4 | 登录日志、操作日志                         | ✅ 已完成 |
| Phase 5 | 多租户业务规则完善（角色/部门/用户创建限制、租户继承、自动填充） | ✅ 已完成 |
| CRM-1   | 客户管理（含公海池、联系人、跟进、标签）              | ✅ 已完成 |
| CRM-2   | 产品管理                              | ✅ 已完成 |
| CRM-3   | 线索管理、商机管理                         | ✅ 已完成 |
| CRM-4   | 报价、合同、发票、收款管理                     | ✅ 已完成 |
| CRM-5   | 数据分析（仪表盘并行查询、销售/客户/商机/合同分析、预警）   | ✅ 已完成 |

## 数据权限切面

**使用@DataScope注解自动注入数据权限SQL条件：**

```java
// Service层使用
@DataScope(deptAlias = "d", userAlias = "u")
public PageVO<UserVO> getUserPage(UserQueryDTO query) {
    // query.dataScope 会自动被切面注入SQL条件
    return userMapper.selectUserPage(query);
}

// Mapper XML中使用注入的条件
<select id="selectUserPage"resultType="UserVO">
SELECT u.*
FROM sys_user
u
LEFT JOIN
sys_dept d
ON u.dept_id =d.id
WHERE u.status =1

$ {
    query.dataScope
}

ORDER BY
u.create_time DESC
</select>
```

**数据权限范围类型：**

| 值 | 类型    | SQL条件                                        |
|---|-------|----------------------------------------------|
| 1 | 全部数据  | 无条件（仅平台用户可选）                                 |
| 2 | 本租户数据 | `WHERE tenant_id = ?`                        |
| 3 | 本部门数据 | `WHERE dept_id = ?`                          |
| 4 | 部门及下级 | `WHERE dept_id = ? OR dept_path LIKE '/?/%'` |
| 5 | 仅本人数据 | `WHERE create_by = ?`                        |

> 注：自定义(6)暂不支持，已移除

## 多租户业务规则

### 用户上下文

**UserContextHolder 提供的方法：**

- `getUserId()` - 获取当前用户ID
- `getUserCode()` - 获取当前用户名
- `getTenantId()` - 获取当前用户租户ID
- `getDeptId()` - 获取当前用户部门ID
- `isPlatformRole()` - 判断是否平台角色（Boolean类型）

> UserContextHolder 位于 `platform-common` 模块，供所有模块使用

### 角色创建规则

| 规则     | 说明                                      |
|--------|-----------------------------------------|
| 平台角色编码 | 平台用户创建平台角色时，roleCode 必须以 `PLATFORM_` 开头 |
| 数据范围必填 | dataScope 不能为空                          |
| 权限范围限制 | 非平台用户不能选择"全部数据"                         |
| 租户角色创建 | 平台用户可选择为特定租户创建角色                        |

### 部门创建规则

| 规则   | 说明                                            |
|------|-----------------------------------------------|
| 自动填充 | createTime、updateTime、createBy、updateBy 自动填充  |
| 顶级限制 | 非平台用户不能创建顶级部门（parentId=0）                     |
| 租户继承 | 子部门继承上级部门的 tenantId                           |
| 部门路径 | deptPath 使用自身ID拼接：`parentPath + "/" + selfId` |

### 用户创建规则

| 规则   | 说明                            |
|------|-------------------------------|
| 部门必选 | deptId 不能为空                   |
| 租户跟随 | 新用户 tenantId 取自所选部门的 tenantId |

## MapStruct转换器

**统一使用SystemConverter进行PO→VO转换：**

```java
// Service中注入
private final SystemConverter systemConverter;

// 单个对象转换
UserVO vo = systemConverter.toUserVO(user);

// 列表转换
List<UserVO> list = systemConverter.toUserVOList(userList);
```

**SystemConverter包含的方法：**

- `toUserVO/toUserVOList` - 用户转换
- `toRoleVO/toRoleVOList` - 角色转换
- `toMenuVO/toMenuVOList` - 菜单转换
- `toDeptVO/toDeptVOList` - 部门转换
- `toTenantVO/toTenantVOList` - 租户转换
- `toPermissionVO/toPermissionVOList` - 权限点转换

## 前端项目架构

**项目路径：** `frontend-parent/platform-admin/`

**技术栈：** Vue 3 + TypeScript + Vite + Element Plus + Pinia

**目录结构：**

```
src/
├── api/           # API请求模块（axios封装，含 api/crm/）
├── components/    # 公共组件
├── composables/   # 组合式函数（hooks，含 useAnalysisFilters）
├── directives/    # 自定义指令
├── router/        # 路由配置
├── stores/        # Pinia状态管理
├── types/         # TypeScript类型定义（含 types/crm/）
├── utils/         # 工具函数
└── views/
    ├── system/    # 系统管理页面
    └── crm/       # CRM业务页面（dashboard、analysis、客户/线索/商机等）
```

**开发注意事项：**

- API请求通过 `/api` 路径，Vite自动代理到后端
- 使用 Element Plus 组件库
- 状态管理使用 Pinia
- 类型定义放在 `types/` 目录