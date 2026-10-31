---
name: CRM系统业务功能设计
description: CRM系统完整的业务功能规划，包含客户管理、产品管理、商机管理、合同收款、数据分析五大模块
type: project
---

# CRM系统业务功能设计规格

## 1. 项目概述

### 1.1 项目背景

基于已完成的后台管理基础功能（多租户、用户、角色、部门、菜单、权限、日志），开发CRM系统核心业务模块，实现完整的客户生命周期管理。

### 1.2 目标用户

- **企业客户**：B2B销售场景，管理企业信息、多联系人、组织架构
- **个人客户**：B2C服务场景，管理个人信息、家庭成员等

### 1.3 开发策略

采用**渐进式模块开发**策略，按依赖关系分5个阶段：

| Phase | 模块 | 依赖 | 预估周期 |
|-------|------|------|----------|
| 1 | 客户管理 | 无 | 基础模块 |
| 2 | 产品管理 | 无 | 基础模块 |
| 3 | 商机管理 | Phase 1, 2 | 核心业务 |
| 4 | 合同+收款管理 | Phase 3 | 商业闭环 |
| 5 | 数据分析 | Phase 1-4 | BI能力 |

---

## 2. 整体架构设计

### 2.1 模块分层架构

```
platform-server/
├── system/              # 系统管理模块（已完成）
│   ├── user/
│   ├── role/
│   ├── menu/
│   ├── dept/
│   ├── tenant/
│   └── log/
│
├── crm/                 # CRM业务模块（新增）
│   ├── customer/        # 客户管理（Phase 1）
│   ├── product/         # 产品管理（Phase 2）
│   ├── opportunity/     # 商机管理（Phase 3）
│   ├── contract/        # 合同管理（Phase 4）
│   ├── payment/         # 收款管理（Phase 4）
│   └── analysis/        # 数据分析（Phase 5）
│
└── common/              # 业务公共
```

### 2.2 CRM公共基础设施

| 组件 | 位置 | 说明 |
|------|------|------|
| CRMConverter | crm/common/converter/ | CRM模块MapStruct转换器 |
| CustomerOwnerAspect | crm/common/aspect/ | 客户归属权限切面（私有池访问控制） |
| SalesStageConfig | sys_config表 | 可配置销售阶段配置项 |
| CRMConstants | crm/common/constants/ | CRM业务常量（客户类型、池类型等） |
| CRMEnums | crm/common/enums/ | CRM枚举定义 |

### 2.3 数据权限继承

CRM模块复用现有数据权限切面：

| 模块 | 权限策略 |
|------|----------|
| 客户 | 按部门数据范围过滤 + 私有池归属过滤 |
| 商机 | 继承客户权限 + 负责人过滤 |
| 合同 | 继承商机权限 + 审批人过滤 |
| 产品 | 全租户可见（仅管理权限控制） |
| 分析 | 按数据权限范围聚合统计 |

### 2.4 API路径规范

| 模块 | 路径前缀 |
|------|----------|
| 客户管理 | `/api/crm/customer` |
| 产品管理 | `/api/crm/product` |
| 线索管理 | `/api/crm/lead` |
| 商机管理 | `/api/crm/opportunity` |
| 报价管理 | `/api/crm/quote` |
| 合同管理 | `/api/crm/contract` |
| 收款管理 | `/api/crm/payment` |
| 发票管理 | `/api/crm/invoice` |
| 数据分析 | `/api/crm/analysis` |

---

## 3. Phase 1：客户管理模块

### 3.1 客户数据模型

**核心实体表：**

| 表名 | 说明 |
|------|------|
| `crm_customer` | 客户主表（企业/个人共用） |
| `crm_customer_pool` | 客户池配置（公海池规则） |
| `crm_customer_contact` | 客户联系人（企业客户多人） |
| `crm_customer_tag` | 客户标签 |
| `crm_customer_tag_rel` | 客户-标签关联 |
| `crm_customer_follow` | 客户跟进记录 |

**crm_customer 表结构：**

```sql
CREATE TABLE crm_customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID（继承）',
    customer_type TINYINT NOT NULL COMMENT '客户类型：1企业 2个人',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户名称（企业名/个人姓名）',
    customer_no VARCHAR(50) COMMENT '客户编号（自动生成）',

    -- 企业客户字段
    industry VARCHAR(50) COMMENT '行业',
    company_size VARCHAR(20) COMMENT '企业规模',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    address VARCHAR(200) COMMENT '详细地址',
    website VARCHAR(100) COMMENT '网站',

    -- 个人客户字段
    gender TINYINT COMMENT '性别：0未知 1男 2女',
    birthday DATE COMMENT '生日',
    occupation VARCHAR(50) COMMENT '职业',

    -- 通用字段
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    source VARCHAR(50) COMMENT '客户来源',
    level TINYINT DEFAULT 1 COMMENT '客户等级：1普通 2重要 3VIP',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',

    -- 池管理
    pool_type TINYINT DEFAULT 1 COMMENT '池类型：1私有池 2公海池',
    owner_id BIGINT COMMENT '负责人ID（私有池时必填）',
    lock_time DATETIME COMMENT '锁定时间（公海领取后）',
    lock_days INT DEFAULT 30 COMMENT '锁定天数（领取后需跟进）',

    -- 基础字段
    next_follow_time DATETIME COMMENT '下次跟进时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT 0 COMMENT '创建人',
    update_by BIGINT DEFAULT 0 COMMENT '更新人',

    INDEX idx_tenant_id (tenant_id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_pool_type (pool_type),
    INDEX idx_customer_no (customer_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';
```

**crm_customer_contact 表结构：**

```sql
CREATE TABLE crm_customer_contact (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',

    contact_name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    gender TINYINT COMMENT '性别',
    position VARCHAR(50) COMMENT '职位',
    department VARCHAR(50) COMMENT '部门',
    is_primary TINYINT DEFAULT 0 COMMENT '是否主要联系人：0否 1是',

    phone VARCHAR(20) COMMENT '电话',
    mobile VARCHAR(20) COMMENT '手机',
    email VARCHAR(100) COMMENT '邮箱',
    wechat VARCHAR(50) COMMENT '微信',

    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户联系人表';
```

**crm_customer_tag 表结构：**

```sql
CREATE TABLE crm_customer_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    tag_color VARCHAR(20) COMMENT '标签颜色',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户标签表';
```

**crm_customer_tag_rel 表结构：**

```sql
CREATE TABLE crm_customer_tag_rel (
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (customer_id, tag_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户标签关联表';
```

**crm_customer_follow 表结构：**

```sql
CREATE TABLE crm_customer_follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',

    follow_type VARCHAR(50) COMMENT '跟进方式（电话、邮件、拜访等）',
    follow_content VARCHAR(500) NOT NULL COMMENT '跟进内容',
    next_follow_time DATETIME COMMENT '下次跟进时间',

    owner_id BIGINT NOT NULL COMMENT '跟进人',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_customer_id (customer_id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_next_follow_time (next_follow_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户跟进记录表';
```

### 3.2 公海池机制

**公海池规则配置（sys_config表存储）：**

| 配置项 | 配置键 | 默认值 | 说明 |
|------|------|--------|------|
| 锁定天数 | `crm_pool_lock_days` | 30 | 领取后锁定天数 |
| 无跟进释放天数 | `crm_pool_no_follow_days` | 15 | 无跟进自动释放天数 |
| 每人最大私有客户数 | `crm_pool_max_per_user` | 100 | 私有池上限 |

**池流转逻辑：**

| 操作 | 处理逻辑 |
|------|----------|
| 领取 | 从公海池领取 → 进入私有池，设置owner_id，开始锁定计时 |
| 释放 | 私有池客户释放 → 进入公海池，清除owner_id |
| 转移 | 私有池客户转移给其他销售 → 变更owner_id |
| 自动释放 | 超过`no_follow_days`无跟进记录 → 自动进入公海池（定时任务） |

**权限控制规则：**

| 池类型 | 可见性 | 可操作 |
|------|------|------|
| 公海池 | 全租户可见（按数据权限） | 领取、查看 |
| 私有池 | 仅owner可见（或部门共享） | 查看、编辑、跟进、释放、转移 |

### 3.3 API接口设计

| 路径 | 方法 | 功能 | 权限标识 |
|------|------|------|----------|
| `/api/crm/customer/list` | GET | 客户列表（私有池） | `crm:customer:list` |
| `/api/crm/customer/pool` | GET | 公海池列表 | `crm:customer:pool` |
| `/api/crm/customer/:id` | GET | 客户详情 | `crm:customer:query` |
| `/api/crm/customer/save` | POST | 新增/修改客户 | `crm:customer:add` / `crm:customer:edit` |
| `/api/crm/customer/delete/:id` | DELETE | 删除客户 | `crm:customer:delete` |
| `/api/crm/customer/pool/take/:id` | POST | 从公海领取客户 | `crm:customer:pool:take` |
| `/api/crm/customer/pool/release/:id` | POST | 释放到公海 | `crm:customer:pool:release` |
| `/api/crm/customer/pool/transfer/:id` | POST | 转移客户 | `crm:customer:pool:transfer` |
| `/api/crm/customer/contact/list/:customerId` | GET | 联系人列表 | `crm:customer:contact:list` |
| `/api/crm/customer/contact/save` | POST | 新增/修改联系人 | `crm:customer:contact:save` |
| `/api/crm/customer/contact/delete/:id` | DELETE | 删除联系人 | `crm:customer:contact:delete` |
| `/api/crm/customer/follow/list/:customerId` | GET | 跟进记录列表 | `crm:customer:follow:list` |
| `/api/crm/customer/follow/save` | POST | 新增跟进记录 | `crm:customer:follow:add` |
| `/api/crm/customer/tag/list` | GET | 标签列表 | `crm:customer:tag:list` |
| `/api/crm/customer/tag/save` | POST | 新增/修改标签 | `crm:customer:tag:save` |
| `/api/crm/customer/tag/delete/:id` | DELETE | 删除标签 | `crm:customer:tag:delete` |
| `/api/crm/customer/tag/bind/:customerId` | POST | 绑定标签 | `crm:customer:tag:bind` |

---

## 4. Phase 2：产品管理模块

### 4.1 产品数据模型

**核心实体表：**

| 表名 | 说明 |
|------|------|
| `crm_product` | 产品主表 |
| `crm_product_category` | 产品分类 |

**crm_product_category 表结构：**

```sql
CREATE TABLE crm_product_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID（0为顶级）',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT 0 COMMENT '创建人',
    update_by BIGINT DEFAULT 0 COMMENT '更新人',

    INDEX idx_tenant_id (tenant_id),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品分类表';
```

**crm_product 表结构：**

```sql
CREATE TABLE crm_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    category_id BIGINT COMMENT '分类ID',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    product_no VARCHAR(50) COMMENT '产品编号（自动生成）',
    product_code VARCHAR(50) COMMENT '产品编码',

    unit VARCHAR(20) COMMENT '单位（件、台、套等）',
    spec VARCHAR(100) COMMENT '规格',
    model VARCHAR(50) COMMENT '型号',

    price DECIMAL(12,2) NOT NULL COMMENT '标准价格',
    min_price DECIMAL(12,2) COMMENT '最低价格（价格区间下限）',
    cost_price DECIMAL(12,2) COMMENT '成本价格（内部参考）',

    description VARCHAR(500) COMMENT '产品描述',
    image_url VARCHAR(200) COMMENT '产品图片',
    status TINYINT DEFAULT 1 COMMENT '状态：0停售 1在售',

    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT 0 COMMENT '创建人',
    update_by BIGINT DEFAULT 0 COMMENT '更新人',

    INDEX idx_tenant_id (tenant_id),
    INDEX idx_category_id (category_id),
    INDEX idx_product_no (product_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';
```

### 4.2 价格区间逻辑

| 场景 | 处理逻辑 |
|------|----------|
| 报价时 | 报价单中产品价格必须在 `[min_price, price]` 区间内 |
| 低于最低价 | 系统提示预警，需审批后方可生效 |
| 未设置min_price | 默认最低价为标准价格的80% |

### 4.3 API接口设计

| 路径 | 方法 | 功能 | 权限标识 |
|------|------|------|----------|
| `/api/crm/product/list` | GET | 产品列表（分页） | `crm:product:list` |
| `/api/crm/product/tree` | GET | 产品树（分类+产品） | `crm:product:list` |
| `/api/crm/product/:id` | GET | 产品详情 | `crm:product:query` |
| `/api/crm/product/save` | POST | 新增/修改产品 | `crm:product:add` / `crm:product:edit` |
| `/api/crm/product/delete/:id` | DELETE | 删除产品 | `crm:product:delete` |
| `/api/crm/product/category/list` | GET | 分类列表 | `crm:product:category:list` |
| `/api/crm/product/category/tree` | GET | 分类树 | `crm:product:category:list` |
| `/api/crm/product/category/save` | POST | 新增/修改分类 | `crm:product:category:save` |
| `/api/crm/product/category/delete/:id` | DELETE | 删除分类 | `crm:product:category:delete` |

---

## 5. Phase 3：商机管理模块

### 5.1 商机数据模型

**核心实体表：**

| 表名 | 说明 |
|------|------|
| `crm_lead` | 销售线索 |
| `crm_opportunity` | 商机主表 |
| `crm_opportunity_stage` | 商机阶段配置（可配置流程） |
| `crm_quote` | 报价单 |
| `crm_quote_product` | 报价单产品明细 |

**crm_lead 表结构：**

```sql
CREATE TABLE crm_lead (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    customer_id BIGINT COMMENT '关联客户（可空，线索转商机时关联）',
    contact_id BIGINT COMMENT '关联联系人',

    lead_name VARCHAR(100) NOT NULL COMMENT '线索名称',
    source VARCHAR(50) COMMENT '线索来源（展会、广告、推荐等）',
    status TINYINT DEFAULT 1 COMMENT '状态：1新建 2跟进中 3已转商机 4已失效',

    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    company VARCHAR(100) COMMENT '公司名称',

    description VARCHAR(500) COMMENT '线索描述',
    owner_id BIGINT NOT NULL COMMENT '负责人',

    converted_time DATETIME COMMENT '转商机时间',
    converted_opp_id BIGINT COMMENT '转换后的商机ID',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT 0 COMMENT '创建人',
    update_by BIGINT DEFAULT 0 COMMENT '更新人',

    INDEX idx_tenant_id (tenant_id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售线索表';
```

**crm_opportunity_stage 表结构：**

```sql
CREATE TABLE crm_opportunity_stage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',

    stage_name VARCHAR(50) NOT NULL COMMENT '阶段名称',
    stage_code VARCHAR(50) COMMENT '阶段编码',
    probability INT DEFAULT 0 COMMENT '默认成交概率（0-100）',

    sort INT DEFAULT 0 COMMENT '排序（决定流程顺序）',
    is_win TINYINT DEFAULT 0 COMMENT '是否成交阶段：0否 1是',
    is_lost TINYINT DEFAULT 0 COMMENT '是否失败阶段：0否 1是',

    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_tenant_id (tenant_id),
    INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商机阶段配置表';

-- 默认阶段配置
INSERT INTO crm_opportunity_stage (tenant_id, stage_name, stage_code, probability, sort, is_win, is_lost) VALUES
(1, '初步接触', 'CONTACT', 10, 1, 0, 0),
(1, '需求确认', 'NEED', 30, 2, 0, 0),
(1, '方案报价', 'QUOTE', 50, 3, 0, 0),
(1, '商务谈判', 'NEGOTIATE', 70, 4, 0, 0),
(1, '成交', 'WIN', 100, 5, 1, 0),
(1, '失败', 'LOST', 0, 6, 0, 1);
```

**crm_opportunity 表结构：**

```sql
CREATE TABLE crm_opportunity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    customer_id BIGINT NOT NULL COMMENT '关联客户',
    contact_id BIGINT COMMENT '主要联系人',

    opp_name VARCHAR(100) NOT NULL COMMENT '商机名称',
    opp_no VARCHAR(50) COMMENT '商机编号（自动生成）',

    stage_id BIGINT NOT NULL COMMENT '当前阶段ID',
    stage_name VARCHAR(50) COMMENT '当前阶段名称（冗余，便于查询）',
    probability INT DEFAULT 0 COMMENT '成交概率（继承阶段配置）',

    expected_amount DECIMAL(12,2) COMMENT '预期金额',
    actual_amount DECIMAL(12,2) COMMENT '实际成交金额',

    expected_date DATE COMMENT '预期成交日期',
    close_date DATE COMMENT '实际成交日期',

    source VARCHAR(50) COMMENT '商机来源',
    status TINYINT DEFAULT 1 COMMENT '状态：1进行中 2已成交 3已失败',

    owner_id BIGINT NOT NULL COMMENT '负责人',
    winner_quote_id BIGINT COMMENT '中标报价单ID',

    description VARCHAR(500) COMMENT '商机描述',
    remark VARCHAR(500) COMMENT '备注',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT 0 COMMENT '创建人',
    update_by BIGINT DEFAULT 0 COMMENT '更新人',

    INDEX idx_tenant_id (tenant_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_stage_id (stage_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商机表';
```

**crm_quote 表结构：**

```sql
CREATE TABLE crm_quote (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    opportunity_id BIGINT NOT NULL COMMENT '关联商机',
    customer_id BIGINT NOT NULL COMMENT '关联客户',

    quote_no VARCHAR(50) COMMENT '报价单编号（自动生成）',
    quote_name VARCHAR(100) COMMENT '报价单名称',

    total_amount DECIMAL(12,2) NOT NULL COMMENT '报价总金额',
    discount_rate DECIMAL(5,2) COMMENT '整体折扣率（%）',
    final_amount DECIMAL(12,2) COMMENT '最终金额',

    status TINYINT DEFAULT 1 COMMENT '状态：1草稿 2已发送 3已接受 4已拒绝',

    expire_date DATE COMMENT '报价有效期',
    send_time DATETIME COMMENT '发送时间',
    accept_time DATETIME COMMENT '接受时间',

    remark VARCHAR(500) COMMENT '备注',

    owner_id BIGINT NOT NULL COMMENT '负责人',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT 0 COMMENT '创建人',
    update_by BIGINT DEFAULT 0 COMMENT '更新人',

    INDEX idx_tenant_id (tenant_id),
    INDEX idx_opportunity_id (opportunity_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报价单表';
```

**crm_quote_product 表结构：**

```sql
CREATE TABLE crm_quote_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    quote_id BIGINT NOT NULL COMMENT '报价单ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',

    product_name VARCHAR(100) COMMENT '产品名称（冗余）',
    unit VARCHAR(20) COMMENT '单位',
    quantity DECIMAL(10,2) NOT NULL COMMENT '数量',

    unit_price DECIMAL(12,2) NOT NULL COMMENT '单价',
    discount_rate DECIMAL(5,2) COMMENT '行折扣（%）',
    total_price DECIMAL(12,2) COMMENT '行总价',

    remark VARCHAR(200) COMMENT '备注',

    INDEX idx_quote_id (quote_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报价单产品明细表';
```

### 5.2 商机流程逻辑

| 场景 | 处理逻辑 |
|------|----------|
| 线索转商机 | 线索状态变更为"已转商机"，自动创建商机（阶段=第一阶段） |
| 阶段推进 | 商机阶段只能向前推进（sort递增），不能回退 |
| 成交标记 | 到达"is_win=1"阶段时，商机状态自动变为"已成交" |
| 失败标记 | 商机可随时标记为失败（无需走完所有阶段） |
| 报价关联 | 报价单必须关联商机，中标报价单标记商机成交金额 |

### 5.3 API接口设计

**线索接口：**

| 路径 | 方法 | 功能 | 权限标识 |
|------|------|------|----------|
| `/api/crm/lead/list` | GET | 线索列表 | `crm:lead:list` |
| `/api/crm/lead/:id` | GET | 线索详情 | `crm:lead:query` |
| `/api/crm/lead/save` | POST | 新增/修改线索 | `crm:lead:add` / `crm:lead:edit` |
| `/api/crm/lead/delete/:id` | DELETE | 删除线索 | `crm:lead:delete` |
| `/api/crm/lead/convert/:id` | POST | 线索转商机 | `crm:lead:convert` |

**商机接口：**

| 路径 | 方法 | 功能 | 权限标识 |
|------|------|------|----------|
| `/api/crm/opportunity/list` | GET | 商机列表 | `crm:opportunity:list` |
| `/api/crm/opportunity/:id` | GET | 商机详情 | `crm:opportunity:query` |
| `/api/crm/opportunity/save` | POST | 新增/修改商机 | `crm:opportunity:add` / `crm:opportunity:edit` |
| `/api/crm/opportunity/delete/:id` | DELETE | 删除商机 | `crm:opportunity:delete` |
| `/api/crm/opportunity/stage/list` | GET | 商机阶段配置列表 | `crm:opportunity:stage:list` |
| `/api/crm/opportunity/stage/save` | POST | 新增/修改阶段配置 | `crm:opportunity:stage:save` |
| `/api/crm/opportunity/stage/delete/:id` | DELETE | 删除阶段配置 | `crm:opportunity:stage:delete` |
| `/api/crm/opportunity/advance/:id` | POST | 推进商机阶段 | `crm:opportunity:advance` |
| `/api/crm/opportunity/close/:id` | POST | 商机成交/失败 | `crm:opportunity:close` |

**报价接口：**

| 路径 | 方法 | 功能 | 权限标识 |
|------|------|------|----------|
| `/api/crm/quote/list` | GET | 报价单列表 | `crm:quote:list` |
| `/api/crm/quote/:id` | GET | 报价单详情 | `crm:quote:query` |
| `/api/crm/quote/save` | POST | 新增/修改报价单 | `crm:quote:add` / `crm:quote:edit` |
| `/api/crm/quote/delete/:id` | DELETE | 删除报价单 | `crm:quote:delete` |
| `/api/crm/quote/send/:id` | POST | 发送报价单 | `crm:quote:send` |
| `/api/crm/quote/accept/:id` | POST | 报价单中标 | `crm:quote:accept` |
| `/api/crm/quote/reject/:id` | POST | 报价单拒绝 | `crm:quote:reject` |

---

## 6. Phase 4：合同+收款管理模块

### 6.1 合同数据模型

**核心实体表：**

| 表名 | 说明 |
|------|------|
| `crm_contract` | 合同主表 |
| `crm_contract_template` | 合同模板（可选） |
| `crm_payment_plan` | 收款计划 |
| `crm_payment_record` | 收款记录 |
| `crm_invoice` | 发票管理 |

**crm_contract 表结构：**

```sql
CREATE TABLE crm_contract (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    customer_id BIGINT NOT NULL COMMENT '关联客户',
    opportunity_id BIGINT COMMENT '关联商机（可选）',
    quote_id BIGINT COMMENT '关联报价单（可选）',

    contract_no VARCHAR(50) COMMENT '合同编号（自动生成）',
    contract_name VARCHAR(100) NOT NULL COMMENT '合同名称',
    contract_type VARCHAR(50) COMMENT '合同类型（销售合同、服务合同等）',

    contract_amount DECIMAL(12,2) NOT NULL COMMENT '合同总金额',
    received_amount DECIMAL(12,2) DEFAULT 0 COMMENT '已收款金额',
    invoiced_amount DECIMAL(12,2) DEFAULT 0 COMMENT '已开票金额',

    sign_date DATE COMMENT '签订日期',
    start_date DATE NOT NULL COMMENT '合同开始日期',
    end_date DATE NOT NULL COMMENT '合同结束日期',

    sign_user_id BIGINT COMMENT '签约人（客户方）',
    our_sign_user_id BIGINT COMMENT '我方签约人',

    status TINYINT DEFAULT 1 COMMENT '状态：1草稿 2审批中 3生效 4到期 5终止',

    owner_id BIGINT NOT NULL COMMENT '合同负责人',

    file_url VARCHAR(200) COMMENT '合同文件URL',
    remark VARCHAR(500) COMMENT '备注',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT 0 COMMENT '创建人',
    update_by BIGINT DEFAULT 0 COMMENT '更新人',

    INDEX idx_tenant_id (tenant_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_opportunity_id (opportunity_id),
    INDEX idx_status (status),
    INDEX idx_end_date (end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';
```

**crm_payment_plan 表结构：**

```sql
CREATE TABLE crm_payment_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    contract_id BIGINT NOT NULL COMMENT '关联合同',

    plan_no INT NOT NULL COMMENT '计划期数（第几期收款）',
    plan_amount DECIMAL(12,2) NOT NULL COMMENT '计划收款金额',
    plan_date DATE NOT NULL COMMENT '计划收款日期',

    received_amount DECIMAL(12,2) DEFAULT 0 COMMENT '已收款金额',
    received_status TINYINT DEFAULT 0 COMMENT '收款状态：0未收 1部分 2已收',

    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_contract_id (contract_id),
    INDEX idx_plan_date (plan_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收款计划表';
```

**crm_payment_record 表结构：**

```sql
CREATE TABLE crm_payment_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    contract_id BIGINT NOT NULL COMMENT '关联合同',
    plan_id BIGINT COMMENT '关联收款计划（可选）',

    payment_no VARCHAR(50) COMMENT '收款编号（自动生成）',
    payment_amount DECIMAL(12,2) NOT NULL COMMENT '本次收款金额',
    payment_date DATE NOT NULL COMMENT '收款日期',

    payment_type VARCHAR(50) COMMENT '收款方式（银行转账、现金、支票等）',
    payer_name VARCHAR(100) COMMENT '付款人',
    payer_account VARCHAR(100) COMMENT '付款账户',

    our_account VARCHAR(100) COMMENT '收款账户',

    status TINYINT DEFAULT 1 COMMENT '状态：1已确认 2待确认',

    remark VARCHAR(200) COMMENT '备注',
    voucher_url VARCHAR(200) COMMENT '收款凭证',

    confirm_time DATETIME COMMENT '确认时间',
    confirm_by BIGINT COMMENT '确认人',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT 0 COMMENT '创建人',
    update_by BIGINT DEFAULT 0 COMMENT '更新人',

    INDEX idx_contract_id (contract_id),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收款记录表';
```

**crm_invoice 表结构：**

```sql
CREATE TABLE crm_invoice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    contract_id BIGINT NOT NULL COMMENT '关联合同',

    invoice_no VARCHAR(50) COMMENT '发票号码',
    invoice_type VARCHAR(50) COMMENT '发票类型（增值税专用、普通等）',
    invoice_amount DECIMAL(12,2) NOT NULL COMMENT '开票金额',

    invoice_date DATE NOT NULL COMMENT '开票日期',
    invoice_status TINYINT DEFAULT 1 COMMENT '状态：1已开票 2已作废',

    remark VARCHAR(200) COMMENT '备注',
    file_url VARCHAR(200) COMMENT '发票文件',

    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT 0 COMMENT '创建人',
    update_by BIGINT DEFAULT 0 COMMENT '更新人',

    INDEX idx_contract_id (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票表';
```

### 6.2 合同审批流程

| 状态值 | 状态名称 | 说明 |
|--------|----------|------|
| 1 | 草稿 | 新建合同，可编辑删除 |
| 2 | 审批中 | 提交审批，进入审批流程 |
| 3 | 生效 | 审批通过，合同生效 |
| 4 | 到期 | 合同到期日期到期，自动标记 |
| 5 | 终止 | 手动终止合同（提前结束） |

**状态流转规则：**

| 流转 | 触发条件 |
|------|----------|
| 草稿 → 审批中 | 提交审批 |
| 审批中 → 生效 | 审批通过，自动生成收款计划 |
| 审批中 → 草稿 | 审批驳回 |
| 生效 → 到期 | 合同end_date到期（定时任务） |
| 生效 → 终止 | 手动终止 |

### 6.3 收款逻辑

| 场景 | 处理逻辑 |
|------|----------|
| 合同生效 | 根据收款计划模板或手动创建收款计划 |
| 收款录入 | 记录收款金额，关联收款计划则更新计划状态 |
| 收款确认 | 收款记录确认后，自动更新合同`received_amount` |
| 收款预警 | 收款计划到期前7天提醒，超期未收预警（定时任务） |

### 6.4 API接口设计

**合同接口：**

| 路径 | 方法 | 功能 | 权限标识 |
|------|------|------|----------|
| `/api/crm/contract/list` | GET | 合同列表 | `crm:contract:list` |
| `/api/crm/contract/:id` | GET | 合同详情 | `crm:contract:query` |
| `/api/crm/contract/save` | POST | 新增/修改合同 | `crm:contract:add` / `crm:contract:edit` |
| `/api/crm/contract/delete/:id` | DELETE | 删除合同（草稿状态） | `crm:contract:delete` |
| `/api/crm/contract/submit/:id` | POST | 提交审批 | `crm:contract:submit` |
| `/api/crm/contract/approve/:id` | POST | 审批通过/驳回 | `crm:contract:approve` |
| `/api/crm/contract/terminate/:id` | POST | 终止合同 | `crm:contract:terminate` |
| `/api/crm/contract/expiring` | GET | 即将到期合同列表 | `crm:contract:list` |

**收款计划接口：**

| 路径 | 方法 | 功能 | 权限标识 |
|------|------|------|----------|
| `/api/crm/payment/plan/list/:contractId` | GET | 收款计划列表 | `crm:payment:plan:list` |
| `/api/crm/payment/plan/save` | POST | 新增/修改收款计划 | `crm:payment:plan:save` |
| `/api/crm/payment/plan/delete/:id` | DELETE | 删除收款计划 | `crm:payment:plan:delete` |

**收款记录接口：**

| 路径 | 方法 | 功能 | 权限标识 |
|------|------|------|----------|
| `/api/crm/payment/record/list/:contractId` | GET | 收款记录列表 | `crm:payment:record:list` |
| `/api/crm/payment/record/:id` | GET | 收款记录详情 | `crm:payment:record:query` |
| `/api/crm/payment/record/save` | POST | 新增收款记录 | `crm:payment:record:add` |
| `/api/crm/payment/record/confirm/:id` | POST | 确认收款 | `crm:payment:record:confirm` |

**发票接口：**

| 路径 | 方法 | 功能 | 权限标识 |
|------|------|------|----------|
| `/api/crm/invoice/list/:contractId` | GET | 发票列表 | `crm:invoice:list` |
| `/api/crm/invoice/save` | POST | 新增发票 | `crm:invoice:add` |
| `/api/crm/invoice/cancel/:id` | POST | 作废发票 | `crm:invoice:cancel` |

---

## 7. Phase 5：数据分析模块

### 7.1 报表体系

**销售业绩报表：**

| 报表名称 | 说明 | 数据来源 |
|------|------|----------|
| 销售员业绩统计 | 按销售员统计成交额、商机数、合同数 | crm_opportunity + crm_contract |
| 团队业绩统计 | 按部门统计团队业绩 | sys_dept + crm_opportunity |
| 业绩趋势分析 | 月度/季度/年度业绩趋势图表 | crm_contract（按时间聚合） |
| 业绩排名分析 | 销售员/团队业绩排名 | crm_opportunity |

**客户分析报表：**

| 报表名称 | 说明 | 数据来源 |
|------|------|----------|
| 客户分布分析 | 按行业/地区/等级统计客户分布 | crm_customer |
| 客户价值分析 | 客户贡献金额排序，识别高价值客户 | crm_contract（按customer聚合） |
| 客户活跃度分析 | 跟进频率、最近跟进时间分析 | crm_customer_follow |
| 客户流失预警 | 无跟进超过阈值客户预警 | crm_customer + crm_customer_follow |

**商机漏斗报表：**

| 报表名称 | 说明 | 数据来源 |
|------|------|----------|
| 销售漏斗分析 | 各阶段商机数量、金额分布 | crm_opportunity + crm_opportunity_stage |
| 转化率分析 | 各阶段转化率、整体转化率 | crm_opportunity（按stage统计） |
| 成交预测 | 基于商机金额×概率计算预测成交额 | crm_opportunity（expected_amount × probability） |

**合同收款报表：**

| 报表名称 | 说明 | 数据来源 |
|------|------|----------|
| 合同执行分析 | 合同状态分布、到期预警 | crm_contract |
| 收款进度分析 | 合同收款率、逾期收款统计 | crm_contract + crm_payment_plan |
| 应收账款分析 | 待收款金额、账龄分析 | crm_contract + crm_payment_plan |

### 7.2 查询维度参数

```java
@Data
@Schema(description = "分析查询条件")
public class AnalysisQueryDTO {
    @Schema(description = "租户ID")
    private Long tenantId;                  // 自动注入

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "销售员ID")
    private Long userId;

    @Schema(description = "客户类型")
    private Integer customerType;

    @Schema(description = "时间维度：MONTH/QUARTER/YEAR")
    private String timeDimension;

    @Schema(description = "行业")
    private String industry;

    @Schema(description = "地区")
    private String province;
}
```

### 7.3 API接口设计

**销售业绩接口：**

| 路径 | 方法 | 功能 |
|------|------|------|
| `/api/crm/analysis/sales/performance` | GET | 销售员业绩统计 |
| `/api/crm/analysis/sales/team` | GET | 团队业绩统计 |
| `/api/crm/analysis/sales/trend` | GET | 业绩趋势分析 |
| `/api/crm/analysis/sales/ranking` | GET | 业绩排名 |

**客户分析接口：**

| 路径 | 方法 | 功能 |
|------|------|------|
| `/api/crm/analysis/customer/distribution` | GET | 客户分布分析 |
| `/api/crm/analysis/customer/value` | GET | 客户价值分析 |
| `/api/crm/analysis/customer/activity` | GET | 客户活跃度 |
| `/api/crm/analysis/customer/churn-warning` | GET | 流失预警列表 |

**商机分析接口：**

| 路径 | 方法 | 功能 |
|------|------|------|
| `/api/crm/analysis/opportunity/funnel` | GET | 销售漏斗分析 |
| `/api/crm/analysis/opportunity/conversion` | GET | 转化率分析 |
| `/api/crm/analysis/opportunity/prediction` | GET | 成交预测 |

**合同收款分析接口：**

| 路径 | 方法 | 功能 |
|------|------|------|
| `/api/crm/analysis/contract/execution` | GET | 合同执行分析 |
| `/api/crm/analysis/contract/receivable` | GET | 应收账款分析 |
| `/api/crm/analysis/payment/progress` | GET | 收款进度分析 |

**综合仪表盘接口：**

| 路径 | 方法 | 功能 |
|------|------|------|
| `/api/crm/analysis/dashboard` | GET | 首页仪表盘汇总数据 |

### 7.4 首页仪表盘设计

| 区域 | 内容 | 更新频率 |
|------|------|----------|
| 顶部卡片 | 本月新增客户数、本月成交金额、本月收款金额、待跟进客户数 | 实时 |
| 销售漏斗 | 可视化漏斗图（各阶段商机金额分布） | 实时 |
| 业绩趋势 | 近12个月业绩折线图 | 实时 |
| 预警提醒 | 即将到期合同、逾期收款、流失预警客户 | 实时 |
| 客户分布 | 按行业饼图、按地区柱状图 | 实时 |

---

## 8. 菜单配置

新增CRM菜单需插入sys_menu表：

```sql
-- CRM一级菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status) VALUES
(100, 0, 'CRM管理', 1, '/crm', '', '', 'Briefcase', 0, 1, 1);

-- 客户管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status) VALUES
(101, 100, '客户管理', 2, '/crm/customer', 'crm/customer/index', 'crm:customer:list', 'User', 0, 1, 1),
(102, 101, '客户查询', 3, '', '', 'crm:customer:query', '', 0, 1, 1),
(103, 101, '客户新增', 3, '', '', 'crm:customer:add', '', 1, 1, 1),
(104, 101, '客户修改', 3, '', '', 'crm:customer:edit', '', 2, 1, 1),
(105, 101, '客户删除', 3, '', '', 'crm:customer:delete', '', 3, 1, 1),
(106, 101, '领取客户', 3, '', '', 'crm:customer:pool:take', '', 4, 1, 1),
(107, 101, '释放客户', 3, '', '', 'crm:customer:pool:release', '', 5, 1, 1),
(108, 101, '转移客户', 3, '', '', 'crm:customer:pool:transfer', '', 6, 1, 1);

-- 公海池
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status) VALUES
(109, 100, '公海池', 2, '/crm/pool', 'crm/pool/index', 'crm:customer:pool', 'FolderOpened', 1, 1, 1);

-- 产品管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status) VALUES
(110, 100, '产品管理', 2, '/crm/product', 'crm/product/index', 'crm:product:list', 'Goods', 2, 1, 1),
(111, 110, '产品查询', 3, '', '', 'crm:product:query', '', 0, 1, 1),
(112, 110, '产品新增', 3, '', '', 'crm:product:add', '', 1, 1, 1),
(113, 110, '产品修改', 3, '', '', 'crm:product:edit', '', 2, 1, 1),
(114, 110, '产品删除', 3, '', '', 'crm:product:delete', '', 3, 1, 1);

-- 线索管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status) VALUES
(115, 100, '线索管理', 2, '/crm/lead', 'crm/lead/index', 'crm:lead:list', 'Guide', 3, 1, 1),
(116, 115, '线索查询', 3, '', '', 'crm:lead:query', '', 0, 1, 1),
(117, 115, '线索新增', 3, '', '', 'crm:lead:add', '', 1, 1, 1),
(118, 115, '线索修改', 3, '', '', 'crm:lead:edit', '', 2, 1, 1),
(119, 115, '线索删除', 3, '', '', 'crm:lead:delete', '', 3, 1, 1),
(120, 115, '转商机', 3, '', '', 'crm:lead:convert', '', 4, 1, 1);

-- 商机管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status) VALUES
(121, 100, '商机管理', 2, '/crm/opportunity', 'crm/opportunity/index', 'crm:opportunity:list', 'TrendCharts', 4, 1, 1),
(122, 121, '商机查询', 3, '', '', 'crm:opportunity:query', '', 0, 1, 1),
(123, 121, '商機新增', 3, '', '', 'crm:opportunity:add', '', 1, 1, 1),
(124, 121, '商机修改', 3, '', '', 'crm:opportunity:edit', '', 2, 1, 1),
(125, 121, '商机删除', 3, '', '', 'crm:opportunity:delete', '', 3, 1, 1),
(126, 121, '阶段推进', 3, '', '', 'crm:opportunity:advance', '', 4, 1, 1),
(127, 121, '成交/失败', 3, '', '', 'crm:opportunity:close', '', 5, 1, 1);

-- 报价管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status) VALUES
(128, 100, '报价管理', 2, '/crm/quote', 'crm/quote/index', 'crm:quote:list', 'Tickets', 5, 1, 1),
(129, 128, '报价查询', 3, '', '', 'crm:quote:query', '', 0, 1, 1),
(130, 128, '报价新增', 3, '', '', 'crm:quote:add', '', 1, 1, 1),
(131, 128, '报价修改', 3, '', '', 'crm:quote:edit', '', 2, 1, 1),
(132, 128, '报价删除', 3, '', '', 'crm:quote:delete', '', 3, 1, 1),
(133, 128, '发送报价', 3, '', '', 'crm:quote:send', '', 4, 1, 1),
(134, 128, '报价中标', 3, '', '', 'crm:quote:accept', '', 5, 1, 1);

-- 合同管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status) VALUES
(135, 100, '合同管理', 2, '/crm/contract', 'crm/contract/index', 'crm:contract:list', 'Document', 6, 1, 1),
(136, 135, '合同查询', 3, '', '', 'crm:contract:query', '', 0, 1, 1),
(137, 135, '合同新增', 3, '', '', 'crm:contract:add', '', 1, 1, 1),
(138, 135, '合同修改', 3, '', '', 'crm:contract:edit', '', 2, 1, 1),
(139, 135, '合同删除', 3, '', '', 'crm:contract:delete', '', 3, 1, 1),
(140, 135, '提交审批', 3, '', '', 'crm:contract:submit', '', 4, 1, 1),
(141, 135, '审批合同', 3, '', '', 'crm:contract:approve', '', 5, 1, 1),
(142, 135, '终止合同', 3, '', '', 'crm:contract:terminate', '', 6, 1, 1);

-- 收款管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status) VALUES
(143, 100, '收款管理', 2, '/crm/payment', 'crm/payment/index', 'crm:payment:record:list', 'Money', 7, 1, 1),
(144, 143, '收款查询', 3, '', '', 'crm:payment:record:query', '', 0, 1, 1),
(145, 143, '收款新增', 3, '', '', 'crm:payment:record:add', '', 1, 1, 1),
(146, 143, '确认收款', 3, '', '', 'crm:payment:record:confirm', '', 2, 1, 1);

-- 数据分析
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status) VALUES
(147, 100, '数据分析', 1, '/crm/analysis', '', '', 'DataAnalysis', 8, 1, 1),
(148, 147, '销售分析', 2, '/crm/analysis/sales', 'crm/analysis/sales', 'crm:analysis:sales', 'TrendCharts', 0, 1, 1),
(149, 147, '客户分析', 2, '/crm/analysis/customer', 'crm/analysis/customer', 'crm:analysis:customer', 'PieChart', 1, 1, 1),
(150, 147, '商机分析', 2, '/crm/analysis/opportunity', 'crm/analysis/opportunity', 'crm:analysis:opportunity', 'DataLine', 2, 1, 1),
(151, 147, '合同分析', 2, '/crm/analysis/contract', 'crm/analysis/contract', 'crm:analysis:contract', 'Document', 3, 1, 1);
```

---

## 9. 技术实现要点

### 9.1 复用现有基础设施

| 基础设施 | CRM模块应用 |
|------|------|
| UserContextHolder | 自动注入tenantId、获取当前用户 |
| DataScopeAspect | 客户、商机、合同数据权限过滤 |
| MyBatisPlusConfig | 分页、自动填充createBy/updateBy |
| OperLogAspect | CRM操作日志记录 |
| RedisLockService | 公海池领取防并发 |

### 9.2 新增公共组件

| 组件 | 说明 |
|------|------|
| CustomerOwnerAspect | 私有池归属权限校验切面 |
| CRMConverter | CRM模块MapStruct转换器 |
| CRMConstants | 客户类型、池类型、状态常量 |
| CRMEnums | 客户状态、商机状态、合同状态枚举 |

### 9.3 定时任务

| 任务 | 执行频率 | 说明 |
|------|----------|------|
| CustomerPoolReleaseTask | 每小时 | 自动释放无跟进客户到公海池 |
| ContractExpireTask | 每天 | 合同到期状态更新 |
| PaymentRemindTask | 每天 | 收款计划到期前提醒 |

---

## 10. 开发顺序建议

按Phase顺序开发，每个Phase完成后进行验收：

1. **Phase 1：客户管理** - 基础模块，其他模块依赖
2. **Phase 2：产品管理** - 独立模块，报价依赖
3. **Phase 3：商机管理** - 核心业务，依赖客户+产品
4. **Phase 4：合同+收款** - 商业闭环，依赖商机
5. **Phase 5：数据分析** - BI能力，依赖所有业务数据

每个Phase的开发流程遵循现有TDD+Code Review规范。