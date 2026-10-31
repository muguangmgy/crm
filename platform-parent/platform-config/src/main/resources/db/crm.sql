/*
 Navicat Premium Dump SQL

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80411 (8.4.11)
 Source Host           : localhost:3306
 Source Schema         : crm

 Target Server Type    : MySQL
 Target Server Version : 80411 (8.4.11)
 File Encoding         : 65001

 Date: 31/10/2026 13:12:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for crm_contract
-- ----------------------------
DROP TABLE IF EXISTS `crm_contract`;
CREATE TABLE `crm_contract`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `customer_id` bigint NOT NULL COMMENT '关联客户',
  `opportunity_id` bigint NULL DEFAULT NULL COMMENT '关联商机',
  `quote_id` bigint NULL DEFAULT NULL COMMENT '关联报价单',
  `contract_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '合同编号（自动生成）',
  `contract_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '合同名称',
  `contract_type` tinyint NULL DEFAULT 1 COMMENT '合同类型：1销售合同 2采购合同',
  `contract_amount` decimal(12, 2) NOT NULL COMMENT '合同金额',
  `received_amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '已收款金额',
  `invoiced_amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '已开票金额',
  `sign_date` date NULL DEFAULT NULL COMMENT '签订日期',
  `start_date` date NOT NULL COMMENT '合同开始日期',
  `end_date` date NOT NULL COMMENT '合同结束日期',
  `sign_user_id` bigint NULL DEFAULT NULL COMMENT '客户方签约人',
  `our_sign_user_id` bigint NULL DEFAULT NULL COMMENT '我方签约人',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1草稿 2审批中 3生效 4到期 5终止',
  `owner_id` bigint NOT NULL COMMENT '负责人',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '合同文件URL',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_opportunity_id`(`opportunity_id` ASC) USING BTREE,
  INDEX `idx_quote_id`(`quote_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sign_date`(`sign_date` ASC) USING BTREE,
  INDEX `idx_dept_id`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 110 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '合同表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_contract
-- ----------------------------
INSERT INTO `crm_contract` VALUES (1, 5, 1, 1, 1, 'HT202610300001', '张三-合同', 1, 10000.00, 10000.00, 10000.00, '2026-10-30', '2026-10-30', '2026-10-30', NULL, NULL, 3, 17, 26, NULL, '', '2026-10-30 23:06:12', '2026-10-31 09:38:48', 17, 17);
INSERT INTO `crm_contract` VALUES (100, 5, 100, 107, NULL, 'HT-AN-100', '华信运维服务合同', 1, 48000.00, 20000.00, 0.00, '2026-10-18', '2026-10-18', '2026-11-20', NULL, NULL, 3, 17, 26, NULL, '即将到期+部分收款', '2026-10-18 14:00:00', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_contract` VALUES (101, 5, 101, 108, NULL, 'HT-AN-101', '蓝海巡检合同', 1, 60000.00, 30000.00, 0.00, '2026-10-22', '2026-10-22', '2027-04-29', NULL, NULL, 3, 18, 26, NULL, '正常分期收款', '2026-10-22 15:00:00', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_contract` VALUES (102, 5, 102, NULL, NULL, 'HT-AN-102', '金桥一期合同', 1, 120000.00, 0.00, 0.00, '2026-07-01', '2026-07-01', '2027-07-01', NULL, NULL, 3, 17, 26, NULL, '长期未回款', '2026-07-01 10:00:00', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_contract` VALUES (103, 5, 103, NULL, NULL, 'HT-AN-103', '星辰短期服务', 1, 35000.00, 35000.00, 0.00, '2026-09-01', '2026-09-01', '2026-11-10', NULL, NULL, 3, 18, 26, NULL, '10天后到期且已收完', '2026-09-01 10:00:00', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_contract` VALUES (104, 5, 104, 104, NULL, 'HT-AN-104', '安康HIS草稿合同', 1, 180000.00, 0.00, 0.00, NULL, '2026-10-31', '2027-10-31', NULL, NULL, 1, 17, 26, NULL, '草稿', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_contract` VALUES (105, 5, 107, 105, NULL, 'HT-AN-105', '天成风控审批中', 1, 300000.00, 0.00, 0.00, NULL, '2026-10-31', '2027-10-31', NULL, NULL, 2, 18, 26, NULL, '审批中', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_contract` VALUES (106, 5, 105, NULL, NULL, 'HT-AN-106', '远航历史合同', 1, 45000.00, 15000.00, 0.00, '2025-10-01', '2025-10-01', '2026-10-16', NULL, NULL, 4, 18, 26, NULL, '到期有欠款', '2025-10-01 10:00:00', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_contract` VALUES (107, 5, 106, NULL, NULL, 'HT-AN-107', '青云终止合同', 1, 20000.00, 5000.00, 0.00, '2026-05-01', '2026-05-01', '2026-08-01', NULL, NULL, 5, 17, 26, NULL, '已终止', '2026-05-01 10:00:00', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_contract` VALUES (108, 5, 111, 109, NULL, 'HT-AN-108', '八月成交合同', 1, 40000.00, 40000.00, 0.00, '2026-08-25', '2026-08-25', '2027-08-25', NULL, NULL, 3, 17, 26, NULL, '8月合同', '2026-08-25 16:00:00', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_contract` VALUES (109, 5, 112, 110, NULL, 'HT-AN-109', '九月成交合同', 1, 55000.00, 55000.00, 0.00, '2026-09-20', '2026-09-20', '2027-09-20', NULL, NULL, 3, 18, 26, NULL, '9月合同', '2026-09-20 16:00:00', '2026-10-31 10:45:42', 18, 18);

-- ----------------------------
-- Table structure for crm_customer
-- ----------------------------
DROP TABLE IF EXISTS `crm_customer`;
CREATE TABLE `crm_customer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `customer_type` tinyint NOT NULL COMMENT '客户类型：1企业 2个人',
  `customer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户名称（企业名/个人姓名）',
  `customer_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户编号（自动生成）',
  `industry` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '行业',
  `company_size` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '企业规模',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `website` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '网站',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：0未知 1男 2女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `occupation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职业',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户来源',
  `level` tinyint NULL DEFAULT 1 COMMENT '客户等级：1普通 2重要 3VIP',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `pool_type` tinyint NULL DEFAULT 1 COMMENT '池类型：1私有池 2公海池',
  `owner_id` bigint NULL DEFAULT NULL COMMENT '负责人ID（私有池时必填）',
  `lock_time` datetime NULL DEFAULT NULL COMMENT '锁定时间（公海领取后）',
  `next_follow_time` datetime NULL DEFAULT NULL COMMENT '下次跟进时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_dept_id`(`dept_id` ASC) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE,
  INDEX `idx_pool_type`(`pool_type` ASC) USING BTREE,
  INDEX `idx_customer_no`(`customer_no` ASC) USING BTREE,
  INDEX `idx_next_follow_time`(`next_follow_time` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 114 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_customer
-- ----------------------------
INSERT INTO `crm_customer` VALUES (1, 5, 26, 1, '张三', 'CUS1793371326157', 'IT', '1-50人', '湖南', '长沙', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '推荐', 1, 1, 1, 17, '2026-10-30 22:42:06', NULL, '', '2026-10-30 22:42:06', '2026-10-30 22:42:06', 17, 17);
INSERT INTO `crm_customer` VALUES (100, 5, 26, 1, '华信科技', 'CUS-AN-100', 'IT', '100-500人', '广东', '深圳', NULL, NULL, NULL, NULL, NULL, '13800000100', 'hx@demo.com', '展会', 3, 1, 1, 17, NULL, '2026-11-02 10:45:42', '高活跃VIP', '2026-10-11 10:45:42', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_customer` VALUES (101, 5, 26, 1, '蓝海制造', 'CUS-AN-101', '制造', '500-1000人', '江苏', '苏州', NULL, NULL, NULL, NULL, NULL, '13800000101', 'lh@demo.com', '推荐', 2, 1, 1, 18, NULL, '2026-11-01 10:45:42', '高活跃重要客户', '2026-09-16 10:45:42', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_customer` VALUES (102, 5, 26, 1, '金桥地产', 'CUS-AN-102', '房地产', '50-100人', '浙江', '杭州', NULL, NULL, NULL, NULL, NULL, '13800000102', 'jq@demo.com', '广告', 2, 1, 1, 17, NULL, NULL, '中活跃', '2026-09-01 10:45:42', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_customer` VALUES (103, 5, 26, 1, '星辰教育', 'CUS-AN-103', '教育', '1-50人', '北京', '北京', NULL, NULL, NULL, NULL, NULL, '13800000103', 'xc@demo.com', '官网', 1, 1, 1, 18, NULL, NULL, '中活跃', '2026-09-21 10:45:42', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_customer` VALUES (104, 5, 26, 1, '安康医疗', 'CUS-AN-104', '医疗', '100-500人', '上海', '上海', NULL, NULL, NULL, NULL, NULL, '13800000104', 'ak@demo.com', '转介绍', 2, 1, 1, 17, NULL, NULL, '低活跃', '2026-07-23 10:45:42', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_customer` VALUES (105, 5, 26, 1, '远航物流', 'CUS-AN-105', '物流', '50-100人', '四川', '成都', NULL, NULL, NULL, NULL, NULL, '13800000105', 'yh@demo.com', '展会', 1, 1, 1, 18, NULL, NULL, '不活跃流失风险', '2026-06-03 10:45:42', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_customer` VALUES (106, 5, 26, 1, '青云餐饮', 'CUS-AN-106', '餐饮', '1-50人', '湖南', '长沙', NULL, NULL, NULL, NULL, NULL, '13800000106', 'qy@demo.com', '地推', 1, 1, 1, 17, NULL, '2026-10-30 10:45:42', '未跟进待跟进', '2026-09-16 10:45:42', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_customer` VALUES (107, 5, 26, 1, '天成金融', 'CUS-AN-107', '金融', '1000人以上', '广东', '广州', NULL, NULL, NULL, NULL, NULL, '13800000107', 'tc@demo.com', '推荐', 3, 1, 1, 18, NULL, NULL, '高价值未跟进', '2026-08-12 10:45:42', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_customer` VALUES (108, 5, 26, 2, '王小明', 'CUS-AN-108', NULL, NULL, '安徽', '合肥', NULL, NULL, NULL, NULL, NULL, '13800000108', 'wxm@demo.com', '微信', 1, 1, 1, 17, NULL, NULL, '个人客户', '2026-10-21 10:45:42', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_customer` VALUES (109, 5, 26, 1, '本月新客户A', 'CUS-AN-109', 'IT', '1-50人', '广东', '东莞', NULL, NULL, NULL, NULL, NULL, '13800000109', 'na@demo.com', '官网', 1, 1, 1, 17, NULL, '2026-11-03 10:45:42', '本月新增', '2026-10-05 10:00:00', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_customer` VALUES (110, 5, 26, 1, '本月新客户B', 'CUS-AN-110', '制造', '50-100人', '江苏', '南京', NULL, NULL, NULL, NULL, NULL, '13800000110', 'nb@demo.com', '展会', 2, 1, 1, 18, NULL, NULL, '本月新增', '2026-10-12 11:00:00', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_customer` VALUES (111, 5, 26, 1, '八月客户', 'CUS-AN-111', 'IT', '1-50人', '浙江', '宁波', NULL, NULL, NULL, NULL, NULL, '13800000111', 'aug@demo.com', '广告', 1, 1, 1, 17, NULL, NULL, '趋势-8月', '2026-08-15 09:00:00', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_customer` VALUES (112, 5, 26, 1, '九月客户', 'CUS-AN-112', '教育', '1-50人', '北京', '北京', NULL, NULL, NULL, NULL, NULL, '13800000112', 'sep@demo.com', '推荐', 1, 1, 1, 18, NULL, NULL, '趋势-9月', '2026-09-18 09:00:00', '2026-10-31 10:45:42', 18, 18);

-- ----------------------------
-- Table structure for crm_customer_contact
-- ----------------------------
DROP TABLE IF EXISTS `crm_customer_contact`;
CREATE TABLE `crm_customer_contact`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人姓名',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：0未知 1男 2女',
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职位',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部门',
  `is_primary` tinyint NULL DEFAULT 0 COMMENT '是否主要联系人：0否 1是',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `wechat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户联系人表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_customer_contact
-- ----------------------------

-- ----------------------------
-- Table structure for crm_customer_follow
-- ----------------------------
DROP TABLE IF EXISTS `crm_customer_follow`;
CREATE TABLE `crm_customer_follow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `follow_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跟进方式（电话、邮件、拜访等）',
  `follow_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '跟进内容',
  `next_follow_time` datetime NULL DEFAULT NULL COMMENT '下次跟进时间',
  `owner_id` bigint NOT NULL COMMENT '跟进人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE,
  INDEX `idx_next_follow_time`(`next_follow_time` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 109 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户跟进记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_customer_follow
-- ----------------------------
INSERT INTO `crm_customer_follow` VALUES (100, 5, 100, '电话', '确认续约意向', '2026-11-02 10:45:42', 17, '2026-10-29 10:45:42', '2026-10-31 10:45:42');
INSERT INTO `crm_customer_follow` VALUES (101, 5, 100, '拜访', '现场演示产品', '2026-11-05 10:45:42', 17, '2026-10-30 10:45:42', '2026-10-31 10:45:42');
INSERT INTO `crm_customer_follow` VALUES (102, 5, 101, '微信', '发送方案资料', '2026-11-01 10:45:42', 18, '2026-10-28 10:45:42', '2026-10-31 10:45:42');
INSERT INTO `crm_customer_follow` VALUES (103, 5, 102, '电话', '跟进预算确认', NULL, 17, '2026-10-16 10:45:42', '2026-10-31 10:45:42');
INSERT INTO `crm_customer_follow` VALUES (104, 5, 103, '邮件', '发送报价单', NULL, 18, '2026-10-11 10:45:42', '2026-10-31 10:45:42');
INSERT INTO `crm_customer_follow` VALUES (105, 5, 104, '拜访', '季度回访', NULL, 17, '2026-09-16 10:45:42', '2026-10-31 10:45:42');
INSERT INTO `crm_customer_follow` VALUES (106, 5, 105, '电话', '早期接触后失联', NULL, 18, '2026-07-03 10:45:42', '2026-10-31 10:45:42');
INSERT INTO `crm_customer_follow` VALUES (107, 5, 1, '电话', '合同执行回访', '2026-11-07 10:45:42', 17, '2026-10-30 10:45:42', '2026-10-31 10:45:42');
INSERT INTO `crm_customer_follow` VALUES (108, 5, 109, '微信', '首次建联', '2026-11-03 10:45:42', 17, '2026-10-29 10:45:42', '2026-10-31 10:45:42');

-- ----------------------------
-- Table structure for crm_customer_tag
-- ----------------------------
DROP TABLE IF EXISTS `crm_customer_tag`;
CREATE TABLE `crm_customer_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `tag_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签颜色',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_customer_tag
-- ----------------------------
INSERT INTO `crm_customer_tag` VALUES (1, 1, '高级客户', '#409EFF', 0, 1, '2026-04-29 15:49:40', '2026-04-29 15:49:40');
INSERT INTO `crm_customer_tag` VALUES (2, 1, '顶级客户', '#f53a1e', 0, 1, '2026-04-29 15:49:52', '2026-04-29 15:49:52');

-- ----------------------------
-- Table structure for crm_customer_tag_rel
-- ----------------------------
DROP TABLE IF EXISTS `crm_customer_tag_rel`;
CREATE TABLE `crm_customer_tag_rel`  (
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`customer_id`, `tag_id`) USING BTREE,
  INDEX `idx_tag_id`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_customer_tag_rel
-- ----------------------------

-- ----------------------------
-- Table structure for crm_invoice
-- ----------------------------
DROP TABLE IF EXISTS `crm_invoice`;
CREATE TABLE `crm_invoice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `contract_id` bigint NOT NULL COMMENT '关联合同',
  `invoice_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发票编号（自动生成）',
  `invoice_type` tinyint NOT NULL COMMENT '发票类型：1普通发票 2增值税专用发票',
  `invoice_amount` decimal(12, 2) NOT NULL COMMENT '发票金额',
  `invoice_date` date NOT NULL COMMENT '开票日期',
  `invoice_status` tinyint NULL DEFAULT 1 COMMENT '发票状态：1已开票 2已作废',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `file_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发票文件URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_contract_id`(`contract_id` ASC) USING BTREE,
  INDEX `idx_invoice_status`(`invoice_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '发票表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_invoice
-- ----------------------------
INSERT INTO `crm_invoice` VALUES (1, 5, 1, 'INV1793374670868', 1, 10000.00, '2026-10-30', 1, '', '', '2026-10-30 23:37:51', '2026-10-30 23:37:51', 19, 19);

-- ----------------------------
-- Table structure for crm_lead
-- ----------------------------
DROP TABLE IF EXISTS `crm_lead`;
CREATE TABLE `crm_lead`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `customer_id` bigint NULL DEFAULT NULL COMMENT '关联客户（可空，线索转商机时关联）',
  `contact_id` bigint NULL DEFAULT NULL COMMENT '关联联系人',
  `lead_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '线索名称',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '线索来源（展会、广告、推荐等）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1新建 2跟进中 3已转商机 4已失效',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '线索描述',
  `owner_id` bigint NOT NULL COMMENT '负责人',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID',
  `converted_time` datetime NULL DEFAULT NULL COMMENT '转商机时间',
  `converted_opp_id` bigint NULL DEFAULT NULL COMMENT '转换后的商机ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_dept_id`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售线索表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_lead
-- ----------------------------
INSERT INTO `crm_lead` VALUES (2, 5, NULL, NULL, '张三要买电脑', '', 3, '', '', '', '', 17, 26, '2026-10-30 22:42:20', 1, '2026-10-30 22:41:50', '2026-10-30 22:41:50', 17, 17);

-- ----------------------------
-- Table structure for crm_opportunity
-- ----------------------------
DROP TABLE IF EXISTS `crm_opportunity`;
CREATE TABLE `crm_opportunity`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `customer_id` bigint NOT NULL COMMENT '关联客户',
  `contact_id` bigint NULL DEFAULT NULL COMMENT '主要联系人',
  `opp_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商机名称',
  `opp_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商机编号（自动生成）',
  `stage_id` bigint NOT NULL COMMENT '当前阶段ID',
  `stage_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '当前阶段名称（冗余）',
  `probability` int NULL DEFAULT 0 COMMENT '成交概率（继承阶段配置）',
  `expected_amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '预期金额',
  `actual_amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '实际成交金额',
  `expected_date` date NULL DEFAULT NULL COMMENT '预期成交日期',
  `close_date` date NULL DEFAULT NULL COMMENT '实际成交日期',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商机来源',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1进行中 2已成交 3已失败',
  `owner_id` bigint NOT NULL COMMENT '负责人',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID',
  `winner_quote_id` bigint NULL DEFAULT NULL COMMENT '中标报价单ID',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商机描述',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE,
  INDEX `idx_stage_id`(`stage_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_dept_id`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 113 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商机表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_opportunity
-- ----------------------------
INSERT INTO `crm_opportunity` VALUES (1, 5, 1, NULL, '张三要买电脑-商机', 'OPP1793371340358', 17, '成交', 100, 10000.00, 10000.00, '2026-10-30', '2026-10-30', '', 2, 17, 26, 1, '', '报价中标自动成交', '2026-10-30 22:42:20', '2026-10-30 22:42:20', 17, 17);
INSERT INTO `crm_opportunity` VALUES (100, 5, 100, NULL, '华信-CRM系统采购', 'OPP-AN-100', 13, '初步接触', 10, 80000.00, NULL, '2026-12-15', NULL, '展会', 1, 17, 26, NULL, '初步接触中', '', '2026-10-13 10:45:42', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_opportunity` VALUES (101, 5, 101, NULL, '蓝海-产线数字化', 'OPP-AN-101', 14, '需求确认', 30, 150000.00, NULL, '2026-11-30', NULL, '推荐', 1, 18, 26, NULL, '需求确认', '', '2026-10-06 10:45:42', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_opportunity` VALUES (102, 5, 102, NULL, '金桥-智慧园区', 'OPP-AN-102', 15, '方案报价', 50, 220000.00, NULL, '2026-11-20', NULL, '广告', 1, 17, 26, NULL, '方案报价', '', '2026-10-01 10:45:42', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_opportunity` VALUES (103, 5, 103, NULL, '星辰-校园管理系统', 'OPP-AN-103', 16, '商务谈判', 70, 95000.00, NULL, '2026-11-10', NULL, '官网', 1, 18, 26, NULL, '商务谈判高概率', '', '2026-10-09 10:45:42', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_opportunity` VALUES (104, 5, 104, NULL, '安康-HIS对接', 'OPP-AN-104', 16, '商务谈判', 70, 180000.00, NULL, '2026-12-01', NULL, '转介绍', 1, 17, 26, NULL, '高金额谈判', '', '2026-09-26 10:45:42', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_opportunity` VALUES (105, 5, 107, NULL, '天成-风控平台', 'OPP-AN-105', 15, '方案报价', 50, 300000.00, NULL, '2026-11-25', NULL, '推荐', 1, 18, 26, NULL, '大单方案', '', '2026-09-21 10:45:42', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_opportunity` VALUES (106, 5, 109, NULL, '新客户A-官网建设', 'OPP-AN-106', 13, '初步接触', 10, 35000.00, NULL, '2026-12-20', NULL, '官网', 1, 17, 26, NULL, '本月新建商机', '', '2026-10-08 10:00:00', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_opportunity` VALUES (107, 5, 100, NULL, '华信-运维服务包', 'OPP-AN-107', 17, '成交', 100, 50000.00, 48000.00, '2026-10-20', '2026-10-18', '展会', 2, 17, 26, NULL, '本月成交', '', '2026-09-11 10:45:42', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_opportunity` VALUES (108, 5, 101, NULL, '蓝海-设备巡检', 'OPP-AN-108', 17, '成交', 100, 60000.00, 60000.00, '2026-10-15', '2026-10-22', '推荐', 2, 18, 26, NULL, '本月成交', '', '2026-09-06 10:45:42', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_opportunity` VALUES (109, 5, 111, NULL, '八月成交单', 'OPP-AN-109', 17, '成交', 100, 40000.00, 40000.00, '2026-08-20', '2026-08-25', '广告', 2, 17, 26, NULL, '8月成交', '', '2026-08-01 10:00:00', '2026-10-31 10:45:42', 17, 17);
INSERT INTO `crm_opportunity` VALUES (110, 5, 112, NULL, '九月成交单', 'OPP-AN-110', 17, '成交', 100, 55000.00, 55000.00, '2026-09-15', '2026-09-20', '推荐', 2, 18, 26, NULL, '9月成交', '', '2026-09-01 10:00:00', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_opportunity` VALUES (111, 5, 105, NULL, '远航-WMS升级', 'OPP-AN-111', 18, '失败', 0, 70000.00, NULL, '2026-09-01', '2026-09-10', '展会', 3, 18, 26, NULL, '丢单', '', '2026-07-01 10:00:00', '2026-10-31 10:45:42', 18, 18);
INSERT INTO `crm_opportunity` VALUES (112, 5, 106, NULL, '青云-点餐系统', 'OPP-AN-112', 14, '需求确认', 30, 25000.00, NULL, '2026-11-15', NULL, '地推', 1, 17, 26, NULL, '低概率', '', '2026-10-19 10:45:42', '2026-10-31 10:45:42', 17, 17);

-- ----------------------------
-- Table structure for crm_opportunity_stage
-- ----------------------------
DROP TABLE IF EXISTS `crm_opportunity_stage`;
CREATE TABLE `crm_opportunity_stage`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `stage_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '阶段名称',
  `stage_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '阶段编码',
  `probability` int NULL DEFAULT 0 COMMENT '默认成交概率（0-100）',
  `sort` int NULL DEFAULT 0 COMMENT '排序（决定流程顺序）',
  `is_win` tinyint NULL DEFAULT 0 COMMENT '是否成交阶段：0否 1是',
  `is_lost` tinyint NULL DEFAULT 0 COMMENT '是否失败阶段：0否 1是',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商机阶段配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_opportunity_stage
-- ----------------------------
INSERT INTO `crm_opportunity_stage` VALUES (1, 1, '初步接触', 'CONTACT', 10, 1, 0, 0, 1, '2026-04-30 15:09:07', '2026-04-30 15:09:07');
INSERT INTO `crm_opportunity_stage` VALUES (2, 1, '需求确认', 'NEED', 30, 2, 0, 0, 1, '2026-04-30 15:09:07', '2026-04-30 15:09:07');
INSERT INTO `crm_opportunity_stage` VALUES (3, 1, '方案报价', 'QUOTE', 50, 3, 0, 0, 1, '2026-04-30 15:09:07', '2026-04-30 15:09:07');
INSERT INTO `crm_opportunity_stage` VALUES (4, 1, '商务谈判', 'NEGOTIATE', 70, 4, 0, 0, 1, '2026-04-30 15:09:07', '2026-04-30 15:09:07');
INSERT INTO `crm_opportunity_stage` VALUES (5, 1, '成交', 'WIN', 100, 5, 1, 0, 1, '2026-04-30 15:09:07', '2026-04-30 15:09:07');
INSERT INTO `crm_opportunity_stage` VALUES (6, 1, '失败', 'LOST', 0, 6, 0, 1, 1, '2026-04-30 15:09:07', '2026-04-30 15:09:07');
INSERT INTO `crm_opportunity_stage` VALUES (13, 5, '初步接触', 'CONTACT', 10, 1, 0, 0, 1, '2026-10-30 19:45:40', '2026-10-30 19:45:40');
INSERT INTO `crm_opportunity_stage` VALUES (14, 5, '需求确认', 'NEED', 30, 2, 0, 0, 1, '2026-10-30 19:45:40', '2026-10-30 19:45:40');
INSERT INTO `crm_opportunity_stage` VALUES (15, 5, '方案报价', 'QUOTE', 50, 3, 0, 0, 1, '2026-10-30 19:45:40', '2026-10-30 19:45:40');
INSERT INTO `crm_opportunity_stage` VALUES (16, 5, '商务谈判', 'NEGOTIATE', 70, 4, 0, 0, 1, '2026-10-30 19:45:40', '2026-10-30 19:45:40');
INSERT INTO `crm_opportunity_stage` VALUES (17, 5, '成交', 'WIN', 100, 5, 1, 0, 1, '2026-10-30 19:45:40', '2026-10-30 19:45:40');
INSERT INTO `crm_opportunity_stage` VALUES (18, 5, '失败', 'LOST', 0, 6, 0, 1, 1, '2026-10-30 19:45:40', '2026-10-30 19:45:40');

-- ----------------------------
-- Table structure for crm_payment_plan
-- ----------------------------
DROP TABLE IF EXISTS `crm_payment_plan`;
CREATE TABLE `crm_payment_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `contract_id` bigint NOT NULL COMMENT '关联合同',
  `plan_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '计划编号（自动生成）',
  `plan_amount` decimal(12, 2) NOT NULL COMMENT '计划收款金额',
  `plan_date` date NOT NULL COMMENT '计划收款日期',
  `received_amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '已收款金额',
  `received_status` tinyint NULL DEFAULT 0 COMMENT '收款状态：0未收 1部分收款 2已收完',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_contract_id`(`contract_id` ASC) USING BTREE,
  INDEX `idx_plan_date`(`plan_date` ASC) USING BTREE,
  INDEX `idx_received_status`(`received_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 108 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收款计划表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_payment_plan
-- ----------------------------
INSERT INTO `crm_payment_plan` VALUES (1, 5, 1, 'PP1793374866150', 10000.00, '2026-10-30', 10000.00, 2, '', '2026-10-30 23:41:06', '2026-10-30 23:41:06', 19, 19);
INSERT INTO `crm_payment_plan` VALUES (100, 5, 100, 'PP-AN-100', 20000.00, '2026-10-11', 20000.00, 2, '首期已收', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 19, 19);
INSERT INTO `crm_payment_plan` VALUES (101, 5, 100, 'PP-AN-101', 15000.00, '2026-10-26', 0.00, 0, '逾期未收', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 19, 19);
INSERT INTO `crm_payment_plan` VALUES (102, 5, 100, 'PP-AN-102', 13000.00, '2026-11-30', 0.00, 0, '未来计划', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 19, 19);
INSERT INTO `crm_payment_plan` VALUES (103, 5, 101, 'PP-AN-103', 30000.00, '2026-10-26', 30000.00, 2, '已收', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 19, 19);
INSERT INTO `crm_payment_plan` VALUES (104, 5, 101, 'PP-AN-104', 30000.00, '2026-12-15', 0.00, 0, '待收', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 19, 19);
INSERT INTO `crm_payment_plan` VALUES (105, 5, 102, 'PP-AN-105', 60000.00, '2026-04-14', 0.00, 0, '严重逾期', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 19, 19);
INSERT INTO `crm_payment_plan` VALUES (106, 5, 102, 'PP-AN-106', 60000.00, '2026-07-23', 0.00, 0, '逾期', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 19, 19);
INSERT INTO `crm_payment_plan` VALUES (107, 5, 106, 'PP-AN-107', 30000.00, '2026-09-21', 0.00, 0, '到期欠款', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 19, 19);

-- ----------------------------
-- Table structure for crm_payment_record
-- ----------------------------
DROP TABLE IF EXISTS `crm_payment_record`;
CREATE TABLE `crm_payment_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `contract_id` bigint NOT NULL COMMENT '关联合同',
  `plan_id` bigint NULL DEFAULT NULL COMMENT '关联收款计划（可选）',
  `payment_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收款编号（自动生成）',
  `payment_amount` decimal(12, 2) NOT NULL COMMENT '实际收款金额',
  `payment_date` date NOT NULL COMMENT '实际收款日期',
  `payment_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收款方式：银行转账/现金/支票等',
  `payer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '付款人名称',
  `payer_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '付款账户',
  `our_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收款账户',
  `status` tinyint NULL DEFAULT 2 COMMENT '状态：1已确认 2待确认',
  `voucher_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '凭证附件URL',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '确认时间',
  `confirm_by` bigint NULL DEFAULT NULL COMMENT '确认人',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_contract_id`(`contract_id` ASC) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_payment_date`(`payment_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收款记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_payment_record
-- ----------------------------
INSERT INTO `crm_payment_record` VALUES (1, 5, 1, 1, 'PR1793374881718', 1000.00, '2026-10-30', '', '', '', '', 1, '', '2026-10-30 23:41:27', 19, '', '2026-10-30 23:41:22', '2026-10-30 23:41:22', 19, 19);
INSERT INTO `crm_payment_record` VALUES (3, 5, 1, 1, 'PR1793410725826', 9000.00, '2026-10-31', '', '', '', '', 1, '', '2026-10-31 09:38:48', 16, '', '2026-10-31 09:38:46', '2026-10-31 09:38:46', 16, 16);
INSERT INTO `crm_payment_record` VALUES (100, 5, 100, 100, 'PR-AN-100', 20000.00, '2026-10-13', '银行转账', NULL, NULL, NULL, 1, NULL, '2026-10-31 10:45:42', 19, '本月收款', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 19, 19);
INSERT INTO `crm_payment_record` VALUES (101, 5, 101, 103, 'PR-AN-101', 30000.00, '2026-10-28', '银行转账', NULL, NULL, NULL, 1, NULL, '2026-10-31 10:45:42', 19, '本月收款', '2026-10-31 10:45:42', '2026-10-31 10:45:42', 19, 19);
INSERT INTO `crm_payment_record` VALUES (102, 5, 108, NULL, 'PR-AN-102', 40000.00, '2026-08-28', '银行转账', NULL, NULL, NULL, 1, NULL, '2026-08-28 12:00:00', 19, '8月收款', '2026-08-28 12:00:00', '2026-10-31 10:45:42', 19, 19);
INSERT INTO `crm_payment_record` VALUES (103, 5, 109, NULL, 'PR-AN-103', 55000.00, '2026-09-25', '银行转账', NULL, NULL, NULL, 1, NULL, '2026-09-25 12:00:00', 19, '9月收款', '2026-09-25 12:00:00', '2026-10-31 10:45:42', 19, 19);
INSERT INTO `crm_payment_record` VALUES (104, 5, 103, NULL, 'PR-AN-104', 35000.00, '2026-09-05', '银行转账', NULL, NULL, NULL, 1, NULL, '2026-09-05 12:00:00', 19, '已收完', '2026-09-05 12:00:00', '2026-10-31 10:45:42', 19, 19);

-- ----------------------------
-- Table structure for crm_product
-- ----------------------------
DROP TABLE IF EXISTS `crm_product`;
CREATE TABLE `crm_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品名称',
  `product_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产品编号（自动生成）',
  `product_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产品编码',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `spec` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格',
  `model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '型号',
  `price` decimal(12, 2) NOT NULL COMMENT '标准价格',
  `min_price` decimal(12, 2) NULL DEFAULT NULL COMMENT '最低价格',
  `cost_price` decimal(12, 2) NULL DEFAULT NULL COMMENT '成本价格',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产品描述',
  `image_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产品图片',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0停售 1在售',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_product_no`(`product_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '产品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_product
-- ----------------------------
INSERT INTO `crm_product` VALUES (1, 5, 1, '商务笔记本电脑', 'PROD1793360825623', 'NB-001', '台', NULL, NULL, 8000.00, 7000.00, 5000.00, NULL, NULL, 1, 1, '2026-10-30 19:47:06', '2026-10-30 19:47:06', 18, 18);
INSERT INTO `crm_product` VALUES (2, 5, 2, '商务笔记本电脑E2E', 'PROD1793360862913', 'NB-E2E', '台', NULL, NULL, 8000.00, 7000.00, 5000.00, NULL, NULL, 1, 1, '2026-10-30 19:47:43', '2026-10-30 19:47:43', 18, 18);
INSERT INTO `crm_product` VALUES (3, 5, 4, 'ROG 枪神 16', 'PROD1793372015240', '', '台', '', '', 10000.00, NULL, NULL, '', '', 1, 0, '2026-10-30 22:53:35', '2026-10-30 22:53:35', 16, 16);

-- ----------------------------
-- Table structure for crm_product_category
-- ----------------------------
DROP TABLE IF EXISTS `crm_product_category`;
CREATE TABLE `crm_product_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID（0为顶级）',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '产品分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_product_category
-- ----------------------------
INSERT INTO `crm_product_category` VALUES (1, 5, 0, '整机', 1, 1, '2026-10-30 19:47:06', '2026-10-30 19:47:06', 18, 18);
INSERT INTO `crm_product_category` VALUES (2, 5, 0, '整机E2E', 1, 1, '2026-10-30 19:47:43', '2026-10-30 19:47:43', 18, 18);
INSERT INTO `crm_product_category` VALUES (3, 5, 0, '计算机', 0, 1, '2026-10-30 21:48:40', '2026-10-30 21:48:40', 16, 16);
INSERT INTO `crm_product_category` VALUES (4, 5, 3, '笔记本', 0, 1, '2026-10-30 21:48:51', '2026-10-30 21:48:51', 16, 16);

-- ----------------------------
-- Table structure for crm_quote
-- ----------------------------
DROP TABLE IF EXISTS `crm_quote`;
CREATE TABLE `crm_quote`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `opportunity_id` bigint NOT NULL COMMENT '关联商机',
  `customer_id` bigint NOT NULL COMMENT '关联客户',
  `quote_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报价单编号（自动生成）',
  `quote_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报价单名称',
  `total_amount` decimal(12, 2) NOT NULL COMMENT '报价总金额',
  `discount_rate` decimal(5, 2) NULL DEFAULT NULL COMMENT '整体折扣率（%）',
  `final_amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '最终金额',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1草稿 2已发送 3已接受 4已拒绝',
  `expire_date` date NULL DEFAULT NULL COMMENT '报价有效期',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `accept_time` datetime NULL DEFAULT NULL COMMENT '接受时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `owner_id` bigint NOT NULL COMMENT '负责人',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_opportunity_id`(`opportunity_id` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_dept_id`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '报价单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_quote
-- ----------------------------
INSERT INTO `crm_quote` VALUES (1, 5, 1, 1, 'QT1793372330437', '张三要买电脑-商机-报价单', 10000.00, 0.00, 10000.00, 3, '2026-10-30', '2026-10-30 22:58:59', '2026-10-30 23:00:06', '', 17, 26, '2026-10-30 22:58:50', '2026-10-30 22:58:50', 17, 17);

-- ----------------------------
-- Table structure for crm_quote_product
-- ----------------------------
DROP TABLE IF EXISTS `crm_quote_product`;
CREATE TABLE `crm_quote_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `quote_id` bigint NOT NULL COMMENT '报价单ID',
  `product_id` bigint NOT NULL COMMENT '产品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产品名称（冗余）',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `quantity` decimal(10, 2) NOT NULL COMMENT '数量',
  `unit_price` decimal(12, 2) NOT NULL COMMENT '单价',
  `discount_rate` decimal(5, 2) NULL DEFAULT NULL COMMENT '行折扣（%）',
  `total_price` decimal(12, 2) NULL DEFAULT NULL COMMENT '行总价',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_quote_id`(`quote_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '报价单产品明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_quote_product
-- ----------------------------
INSERT INTO `crm_quote_product` VALUES (1, 1, 3, 'ROG 枪神 16', '台', 1.00, 10000.00, 0.00, 10000.00, '');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户ID，0表示全局共享',
  `group_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分组编码',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参数键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参数名称',
  `config_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参数值',
  `value_type` tinyint NOT NULL DEFAULT 1 COMMENT '值类型：1-string 2-number 3-json',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建人ID',
  `update_by` bigint NOT NULL DEFAULT 0 COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_config_key`(`tenant_id` ASC, `config_key` ASC) USING BTREE,
  INDEX `idx_group_tenant_status`(`group_code` ASC, `tenant_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '业务参数表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT 1 COMMENT '租户ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父部门ID（0为顶级）',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `dept_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '部门路径（如/1/2/3）',
  `leader_id` bigint NULL DEFAULT 0 COMMENT '负责人ID',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, 1, 0, '平台总公司', '/0/1', 1, 0, 1, '2026-04-18 22:38:01', '2026-04-28 12:39:18', 1, 1);
INSERT INTO `sys_dept` VALUES (24, 5, 0, '金石小区', '/0', 0, 0, 1, '2026-10-30 18:48:26', '2026-10-30 18:48:26', 1, 1);
INSERT INTO `sys_dept` VALUES (25, 5, 24, '财务部', '/0/25', 20, 0, 1, '2026-10-30 18:53:30', '2026-10-30 18:53:44', 1, 1);
INSERT INTO `sys_dept` VALUES (26, 5, 24, '销售部', '/0/26', 18, 0, 1, '2026-10-30 18:53:36', '2026-10-30 18:53:52', 1, 1);
INSERT INTO `sys_dept` VALUES (27, 5, 24, '管理部', '/0/27', 16, 0, 1, '2026-10-30 18:54:52', '2026-10-30 18:54:52', 1, 1);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  `dict_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典编码',
  `item_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典值',
  `item_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典标签',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建人ID',
  `update_by` bigint NOT NULL DEFAULT 0 COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_dict_value`(`tenant_id` ASC, `dict_code` ASC, `item_value` ASC) USING BTREE,
  INDEX `idx_dict_status_sort`(`dict_code` ASC, `status` ASC, `sort` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 224 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 0, 'CONFIG_GROUP', 'basic', '基础配置', 0, 1, '', '2026-04-28 13:57:46', '2026-04-28 20:53:05', 1, 1);
INSERT INTO `sys_dict_data` VALUES (2, 0, 'CONFIG_GROUP', 'email', '邮件配置', 1, 1, '', '2026-04-28 13:57:46', '2026-04-28 20:53:05', 1, 1);
INSERT INTO `sys_dict_data` VALUES (3, 0, 'CONFIG_GROUP', 'storage', '存储配置', 2, 1, '', '2026-04-28 13:57:46', '2026-04-28 20:53:05', 1, 1);
INSERT INTO `sys_dict_data` VALUES (4, 0, 'crm_customer_type', '1', '企业', 1, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (5, 0, 'crm_customer_type', '2', '个人', 2, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (6, 0, 'crm_customer_level', '1', '普通', 1, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (7, 0, 'crm_customer_level', '2', '重要', 2, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (8, 0, 'crm_customer_level', '3', 'VIP', 3, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (9, 0, 'crm_company_size', '1-50人', '1-50人', 1, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (10, 0, 'crm_company_size', '50-100人', '50-100人', 2, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (11, 0, 'crm_company_size', '100-500人', '100-500人', 3, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (12, 0, 'crm_company_size', '500-1000人', '500-1000人', 4, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (13, 0, 'crm_company_size', '1000人以上', '1000人以上', 5, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (14, 0, 'crm_customer_source', '展会', '展会', 1, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (15, 0, 'crm_customer_source', '广告', '广告', 2, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (16, 0, 'crm_customer_source', '推荐', '客户推荐', 3, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (17, 0, 'crm_customer_source', '官网', '官网注册', 4, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (18, 0, 'crm_customer_source', '电话', '电话咨询', 5, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (19, 0, 'crm_customer_source', '微信', '微信咨询', 6, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (20, 0, 'crm_customer_source', '其他', '其他', 7, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (21, 0, 'crm_industry', 'IT', 'IT/互联网', 1, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (22, 0, 'crm_industry', '金融', '金融/银行', 2, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (23, 0, 'crm_industry', '制造', '制造业', 3, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (24, 0, 'crm_industry', '零售', '零售/电商', 4, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (25, 0, 'crm_industry', '教育', '教育培训', 5, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (26, 0, 'crm_industry', '医疗', '医疗健康', 6, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (27, 0, 'crm_industry', '房地产', '房地产', 7, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (28, 0, 'crm_industry', '物流', '物流运输', 8, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (29, 0, 'crm_industry', '能源', '能源/环保', 9, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (30, 0, 'crm_industry', '其他', '其他行业', 10, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (31, 0, 'crm_follow_type', '电话', '电话沟通', 1, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (32, 0, 'crm_follow_type', '邮件', '邮件联系', 2, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (33, 0, 'crm_follow_type', '拜访', '上门拜访', 3, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (34, 0, 'crm_follow_type', '微信', '微信沟通', 4, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (35, 0, 'crm_follow_type', '视频', '视频会议', 5, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (36, 0, 'crm_follow_type', '其他', '其他方式', 6, 1, NULL, '2026-04-29 16:29:16', '2026-04-29 16:29:16', 0, 0);
INSERT INTO `sys_dict_data` VALUES (37, 0, 'crm_product_unit', '件', '件', 1, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (38, 0, 'crm_product_unit', '台', '台', 2, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (39, 0, 'crm_product_unit', '套', '套', 3, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (40, 0, 'crm_product_unit', '箱', '箱', 4, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (41, 0, 'crm_product_unit', '盒', '盒', 5, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (42, 0, 'crm_product_unit', '包', '包', 6, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (43, 0, 'crm_product_unit', '袋', '袋', 7, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (44, 0, 'crm_product_unit', '瓶', '瓶', 8, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (45, 0, 'crm_product_unit', 'kg', '千克', 9, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (46, 0, 'crm_product_unit', 'm', '米', 10, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (47, 0, 'crm_product_status', '1', '在售', 1, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (48, 0, 'crm_product_status', '0', '停售', 2, 1, NULL, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (49, 0, 'crm_lead_source', '展会', '展会', 1, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (50, 0, 'crm_lead_source', '广告', '广告', 2, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (51, 0, 'crm_lead_source', '推荐', '推荐', 3, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (52, 0, 'crm_lead_source', '电话咨询', '电话咨询', 4, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (53, 0, 'crm_lead_source', '网络搜索', '网络搜索', 5, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (54, 0, 'crm_lead_source', '邮件营销', '邮件营销', 6, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (55, 0, 'crm_lead_source', '社交媒体', '社交媒体', 7, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (56, 0, 'crm_lead_source', '合作伙伴', '合作伙伴', 8, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (57, 0, 'crm_lead_source', '其他', '其他', 9, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (58, 0, 'crm_lead_status', '1', '新建', 1, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (59, 0, 'crm_lead_status', '2', '跟进中', 2, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (60, 0, 'crm_lead_status', '3', '已转商机', 3, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (61, 0, 'crm_lead_status', '4', '已失效', 4, 1, NULL, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (62, 0, 'crm_opportunity_status', '1', '进行中', 1, 1, NULL, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (63, 0, 'crm_opportunity_status', '2', '已成交', 2, 1, NULL, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (64, 0, 'crm_opportunity_status', '3', '已失败', 3, 1, NULL, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (65, 0, 'crm_opportunity_source', 'website', '官网咨询', 1, 1, NULL, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (66, 0, 'crm_opportunity_source', 'referral', '客户推荐', 2, 1, NULL, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (67, 0, 'crm_opportunity_source', 'exhibition', '展会', 3, 1, NULL, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (68, 0, 'crm_opportunity_source', 'advertisement', '广告投放', 4, 1, NULL, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (69, 0, 'crm_opportunity_source', 'cold_call', '电话销售', 5, 1, NULL, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (70, 0, 'crm_opportunity_source', 'partner', '合作伙伴', 6, 1, NULL, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (71, 0, 'crm_opportunity_source', 'other', '其他', 7, 1, NULL, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (72, 0, 'crm_quote_status', '1', '草稿', 1, 1, NULL, '2026-04-30 15:17:18', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (73, 0, 'crm_quote_status', '2', '已发送', 2, 1, NULL, '2026-04-30 15:17:18', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (74, 0, 'crm_quote_status', '3', '已接受', 3, 1, NULL, '2026-04-30 15:17:18', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (75, 0, 'crm_quote_status', '4', '已拒绝', 4, 1, NULL, '2026-04-30 15:17:18', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (76, 0, 'crm_contract_status', '1', '草稿', 1, 1, NULL, '2026-04-30 15:17:27', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (77, 0, 'crm_contract_status', '2', '审批中', 2, 1, NULL, '2026-04-30 15:17:27', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (78, 0, 'crm_contract_status', '3', '生效', 3, 1, NULL, '2026-04-30 15:17:27', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (79, 0, 'crm_contract_status', '4', '到期', 4, 1, NULL, '2026-04-30 15:17:27', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (80, 0, 'crm_contract_status', '5', '终止', 5, 1, NULL, '2026-04-30 15:17:27', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (81, 0, 'crm_contract_type', '1', '销售合同', 1, 1, NULL, '2026-04-30 15:17:27', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (82, 0, 'crm_contract_type', '2', '采购合同', 2, 1, NULL, '2026-04-30 15:17:27', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (83, 0, 'crm_invoice_type', '1', '普通发票', 1, 1, NULL, '2026-04-30 15:17:35', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (84, 0, 'crm_invoice_type', '2', '增值税专用发票', 2, 1, NULL, '2026-04-30 15:17:35', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (85, 0, 'crm_invoice_status', '1', '已开票', 1, 1, NULL, '2026-04-30 15:17:35', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (86, 0, 'crm_invoice_status', '2', '已作废', 2, 1, NULL, '2026-04-30 15:17:35', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (87, 0, 'crm_payment_status', '0', '未收款', 1, 1, NULL, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (88, 0, 'crm_payment_status', '1', '部分收款', 2, 1, NULL, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (89, 0, 'crm_payment_status', '2', '已收完', 3, 1, NULL, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (90, 0, 'crm_record_status', '1', '已确认', 1, 1, NULL, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (91, 0, 'crm_record_status', '2', '待确认', 2, 1, NULL, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (92, 0, 'crm_payment_type', 'bank_transfer', '银行转账', 1, 1, NULL, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (93, 0, 'crm_payment_type', 'cash', '现金', 2, 1, NULL, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (94, 0, 'crm_payment_type', 'check', '支票', 3, 1, NULL, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (95, 0, 'crm_payment_type', 'alipay', '支付宝', 4, 1, NULL, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (96, 0, 'crm_payment_type', 'wechat', '微信支付', 5, 1, NULL, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_data` VALUES (97, 5, 'crm_customer_type', '1', '企业', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (98, 5, 'crm_customer_type', '2', '个人', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (99, 5, 'crm_customer_level', '1', '普通', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (100, 5, 'crm_customer_level', '2', '重要', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (101, 5, 'crm_customer_level', '3', 'VIP', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (102, 5, 'crm_company_size', '1-50人', '1-50人', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (103, 5, 'crm_company_size', '50-100人', '50-100人', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (104, 5, 'crm_company_size', '100-500人', '100-500人', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (105, 5, 'crm_company_size', '500-1000人', '500-1000人', 4, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (106, 5, 'crm_company_size', '1000人以上', '1000人以上', 5, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (107, 5, 'crm_customer_source', '展会', '展会', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (108, 5, 'crm_customer_source', '广告', '广告', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (109, 5, 'crm_customer_source', '推荐', '客户推荐', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (110, 5, 'crm_customer_source', '官网', '官网注册', 4, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (111, 5, 'crm_customer_source', '电话', '电话咨询', 5, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (112, 5, 'crm_customer_source', '微信', '微信咨询', 6, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (113, 5, 'crm_customer_source', '其他', '其他', 7, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (114, 5, 'crm_industry', 'IT', 'IT/互联网', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (115, 5, 'crm_industry', '金融', '金融/银行', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (116, 5, 'crm_industry', '制造', '制造业', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (117, 5, 'crm_industry', '零售', '零售/电商', 4, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (118, 5, 'crm_industry', '教育', '教育培训', 5, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (119, 5, 'crm_industry', '医疗', '医疗健康', 6, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (120, 5, 'crm_industry', '房地产', '房地产', 7, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (121, 5, 'crm_industry', '物流', '物流运输', 8, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (122, 5, 'crm_industry', '能源', '能源/环保', 9, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (123, 5, 'crm_industry', '其他', '其他行业', 10, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (124, 5, 'crm_follow_type', '电话', '电话沟通', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (125, 5, 'crm_follow_type', '邮件', '邮件联系', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (126, 5, 'crm_follow_type', '拜访', '上门拜访', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (127, 5, 'crm_follow_type', '微信', '微信沟通', 4, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (128, 5, 'crm_follow_type', '视频', '视频会议', 5, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (129, 5, 'crm_follow_type', '其他', '其他方式', 6, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (130, 5, 'crm_product_unit', '件', '件', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (131, 5, 'crm_product_unit', '台', '台', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (132, 5, 'crm_product_unit', '套', '套', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (133, 5, 'crm_product_unit', '箱', '箱', 4, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (134, 5, 'crm_product_unit', '盒', '盒', 5, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (135, 5, 'crm_product_unit', '包', '包', 6, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (136, 5, 'crm_product_unit', '袋', '袋', 7, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (137, 5, 'crm_product_unit', '瓶', '瓶', 8, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (138, 5, 'crm_product_unit', 'kg', '千克', 9, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (139, 5, 'crm_product_unit', 'm', '米', 10, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (140, 5, 'crm_product_status', '1', '在售', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (141, 5, 'crm_product_status', '0', '停售', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (142, 5, 'crm_lead_source', '展会', '展会', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (143, 5, 'crm_lead_source', '广告', '广告', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (144, 5, 'crm_lead_source', '推荐', '推荐', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (145, 5, 'crm_lead_source', '电话咨询', '电话咨询', 4, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (146, 5, 'crm_lead_source', '网络搜索', '网络搜索', 5, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (147, 5, 'crm_lead_source', '邮件营销', '邮件营销', 6, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (148, 5, 'crm_lead_source', '社交媒体', '社交媒体', 7, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (149, 5, 'crm_lead_source', '合作伙伴', '合作伙伴', 8, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (150, 5, 'crm_lead_source', '其他', '其他', 9, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (151, 5, 'crm_lead_status', '1', '新建', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (152, 5, 'crm_lead_status', '2', '跟进中', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (153, 5, 'crm_lead_status', '3', '已转商机', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (154, 5, 'crm_lead_status', '4', '已失效', 4, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (155, 5, 'crm_opportunity_status', '1', '进行中', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (156, 5, 'crm_opportunity_status', '2', '已成交', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (157, 5, 'crm_opportunity_status', '3', '已失败', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (158, 5, 'crm_opportunity_source', 'website', '官网咨询', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (159, 5, 'crm_opportunity_source', 'referral', '客户推荐', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (160, 5, 'crm_opportunity_source', 'exhibition', '展会', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (161, 5, 'crm_opportunity_source', 'advertisement', '广告投放', 4, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (162, 5, 'crm_opportunity_source', 'cold_call', '电话销售', 5, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (163, 5, 'crm_opportunity_source', 'partner', '合作伙伴', 6, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (164, 5, 'crm_opportunity_source', 'other', '其他', 7, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (165, 5, 'crm_quote_status', '1', '草稿', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (166, 5, 'crm_quote_status', '2', '已发送', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (167, 5, 'crm_quote_status', '3', '已接受', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (168, 5, 'crm_quote_status', '4', '已拒绝', 4, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (169, 5, 'crm_contract_status', '1', '草稿', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (170, 5, 'crm_contract_status', '2', '审批中', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (171, 5, 'crm_contract_status', '3', '生效', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (172, 5, 'crm_contract_status', '4', '到期', 4, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (173, 5, 'crm_contract_status', '5', '终止', 5, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (174, 5, 'crm_contract_type', '1', '销售合同', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (175, 5, 'crm_contract_type', '2', '采购合同', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (176, 5, 'crm_invoice_type', '1', '普通发票', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (177, 5, 'crm_invoice_type', '2', '增值税专用发票', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (178, 5, 'crm_invoice_status', '1', '已开票', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (179, 5, 'crm_invoice_status', '2', '已作废', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (180, 5, 'crm_payment_status', '0', '未收款', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (181, 5, 'crm_payment_status', '1', '部分收款', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (182, 5, 'crm_payment_status', '2', '已收完', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (183, 5, 'crm_record_status', '1', '已确认', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (184, 5, 'crm_record_status', '2', '待确认', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (185, 5, 'crm_payment_type', 'bank_transfer', '银行转账', 1, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (186, 5, 'crm_payment_type', 'cash', '现金', 2, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (187, 5, 'crm_payment_type', 'check', '支票', 3, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (188, 5, 'crm_payment_type', 'alipay', '支付宝', 4, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_data` VALUES (189, 5, 'crm_payment_type', 'wechat', '微信支付', 5, 1, NULL, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户ID，0表示全局共享',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父字典类型ID（0为根节点）',
  `dict_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典编码',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典名称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `sort` int NULL DEFAULT 0 COMMENT '排序号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建人ID',
  `update_by` bigint NOT NULL DEFAULT 0 COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_dict_code`(`tenant_id` ASC, `dict_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, 0, 0, 'CONFIG_GROUP', '参数分组', 1, '业务参数分组配置', 0, '2026-04-28 13:57:46', '2026-04-28 20:01:59', 1, 1);
INSERT INTO `sys_dict_type` VALUES (2, 0, 0, 'crm', 'CRM字典', 1, 'CRM业务字典根节点', 0, '2026-04-29 16:26:32', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (3, 0, 2, 'crm_customer_type', '客户类型', 1, '客户类型分类', 1, '2026-04-29 16:27:30', '2026-04-29 16:27:30', 0, 0);
INSERT INTO `sys_dict_type` VALUES (4, 0, 2, 'crm_customer_level', '客户等级', 1, '客户等级分类', 2, '2026-04-29 16:27:30', '2026-04-29 16:27:30', 0, 0);
INSERT INTO `sys_dict_type` VALUES (5, 0, 2, 'crm_company_size', '企业规模', 1, '企业规模分类', 3, '2026-04-29 16:27:30', '2026-04-29 16:27:30', 0, 0);
INSERT INTO `sys_dict_type` VALUES (6, 0, 2, 'crm_customer_source', '客户来源', 1, '客户来源分类', 4, '2026-04-29 16:27:30', '2026-04-29 16:27:30', 0, 0);
INSERT INTO `sys_dict_type` VALUES (7, 0, 2, 'crm_industry', '行业类型', 1, '行业分类', 5, '2026-04-29 16:27:30', '2026-04-29 16:27:30', 0, 0);
INSERT INTO `sys_dict_type` VALUES (8, 0, 2, 'crm_follow_type', '跟进方式', 1, '跟进方式分类', 6, '2026-04-29 16:27:30', '2026-04-29 16:27:30', 0, 0);
INSERT INTO `sys_dict_type` VALUES (10, 0, 2, 'crm_product_unit', '产品单位', 1, '产品单位分类', 7, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (11, 0, 2, 'crm_product_status', '产品状态', 1, '产品状态分类', 8, '2026-04-30 15:08:55', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (12, 0, 2, 'crm_lead_source', '线索来源', 1, '线索来源分类', 9, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (13, 0, 2, 'crm_lead_status', '线索状态', 1, '线索状态分类', 10, '2026-04-30 15:09:01', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (14, 0, 2, 'crm_opportunity_status', '商机状态', 1, '商机状态分类', 9, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (15, 0, 2, 'crm_opportunity_source', '商机来源', 1, '商机来源分类', 10, '2026-04-30 15:17:09', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (16, 0, 2, 'crm_quote_status', '报价状态', 1, '报价状态分类', 9, '2026-04-30 15:17:18', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (17, 0, 2, 'crm_contract_status', '合同状态', 1, '合同状态分类', 10, '2026-04-30 15:17:27', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (18, 0, 2, 'crm_contract_type', '合同类型', 1, '合同类型分类', 11, '2026-04-30 15:17:27', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (19, 0, 2, 'crm_invoice_type', '发票类型', 1, '发票类型分类', 10, '2026-04-30 15:17:35', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (20, 0, 2, 'crm_invoice_status', '发票状态', 1, '发票状态分类', 11, '2026-04-30 15:17:35', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (21, 0, 2, 'crm_payment_status', '收款状态', 1, '收款计划状态', 10, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (22, 0, 2, 'crm_record_status', '收款记录状态', 1, '收款记录确认状态', 11, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (23, 0, 2, 'crm_payment_type', '收款方式', 1, '收款方式分类', 12, '2026-04-30 15:17:41', '2026-10-30 21:56:46', 0, 0);
INSERT INTO `sys_dict_type` VALUES (24, 5, 0, 'crm', 'CRM字典', 1, 'CRM业务字典根节点', 0, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (25, 5, 24, 'crm_customer_type', '客户类型', 1, '客户类型分类', 1, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (26, 5, 24, 'crm_customer_level', '客户等级', 1, '客户等级分类', 2, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (27, 5, 24, 'crm_company_size', '企业规模', 1, '企业规模分类', 3, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (28, 5, 24, 'crm_customer_source', '客户来源', 1, '客户来源分类', 4, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (29, 5, 24, 'crm_industry', '行业类型', 1, '行业分类', 5, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (30, 5, 24, 'crm_follow_type', '跟进方式', 1, '跟进方式分类', 6, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (31, 5, 24, 'crm_product_unit', '产品单位', 1, '产品单位分类', 7, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (32, 5, 24, 'crm_product_status', '产品状态', 1, '产品状态分类', 8, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (33, 5, 24, 'crm_lead_source', '线索来源', 1, '线索来源分类', 9, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (34, 5, 24, 'crm_lead_status', '线索状态', 1, '线索状态分类', 10, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (35, 5, 24, 'crm_opportunity_status', '商机状态', 1, '商机状态分类', 9, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (36, 5, 24, 'crm_opportunity_source', '商机来源', 1, '商机来源分类', 10, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (37, 5, 24, 'crm_quote_status', '报价状态', 1, '报价状态分类', 9, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (38, 5, 24, 'crm_contract_status', '合同状态', 1, '合同状态分类', 10, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (39, 5, 24, 'crm_contract_type', '合同类型', 1, '合同类型分类', 11, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (40, 5, 24, 'crm_invoice_type', '发票类型', 1, '发票类型分类', 10, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (41, 5, 24, 'crm_invoice_status', '发票状态', 1, '发票状态分类', 11, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (42, 5, 24, 'crm_payment_status', '收款状态', 1, '收款计划状态', 10, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (43, 5, 24, 'crm_record_status', '收款记录状态', 1, '收款记录确认状态', 11, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);
INSERT INTO `sys_dict_type` VALUES (44, 5, 24, 'crm_payment_type', '收款方式', 1, '收款方式分类', 12, '2026-10-30 22:06:33', '2026-10-30 22:06:33', 0, 0);

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `user_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `login_type` tinyint NOT NULL COMMENT '登录类型：1登录 2注销 3刷新',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `device_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备信息',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统',
  `login_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1成功 0失败',
  `message` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '提示消息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_code`(`user_code` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_login_time`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 134 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------
INSERT INTO `sys_login_log` VALUES (1, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 19:47:07', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (2, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 19:47:07', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (3, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 19:47:43', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (4, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 19:47:43', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (5, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 19:47:58', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (6, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 19:47:59', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (7, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:07:08', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (8, 5, 'finance_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:07:08', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (9, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:07:08', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (10, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:07:08', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (11, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:07:08', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (12, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:07:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (13, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:07:47', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (14, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:07:47', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (15, 5, 'finance_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:07:48', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (16, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:07:58', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (17, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:08:22', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (18, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:08:41', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (19, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:08:57', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (20, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:09:26', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (21, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:11:56', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (22, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:17:49', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (23, 5, 'finance_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:18:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (24, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:18:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (25, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:18:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (26, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:18:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (27, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:18:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (28, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:19:09', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (29, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:20:09', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (30, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:20:09', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (31, 5, 'finance_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:20:09', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (32, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:20:09', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (33, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:20:09', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (34, 5, 'finance_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:31:01', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (35, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:31:01', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (36, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:31:01', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (37, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:31:01', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (38, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:31:01', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (39, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:49:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (40, 5, 'finance_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:49:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (41, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:49:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (42, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:49:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (43, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-30 20:49:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (44, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 21:39:59', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (45, 5, 'sale', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 21:48:11', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (46, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 21:48:25', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (47, 5, 'ptgly', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 21:48:54', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (48, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 21:49:06', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (49, 5, 'sale', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 22:00:27', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (50, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 22:00:41', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (51, 5, 'ptgly', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 22:30:01', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (52, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 22:30:10', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (53, 5, 'sale', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 22:47:52', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (54, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 22:48:04', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (55, 5, 'ptgly', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 22:53:39', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (56, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 22:53:48', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (57, 5, 'sale', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:06:27', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (58, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:06:41', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (59, 5, 'sale_m', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:07:30', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (60, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:07:38', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (61, 5, 'sale', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:09:46', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (62, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:09:58', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (63, 5, 'ptgly', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:12:57', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (64, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:13:09', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (65, 5, 'finance', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:17:05', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (66, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:17:17', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (67, 5, 'ptgly', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:17:38', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (68, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:17:48', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (69, 5, 'finance', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:24:26', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (70, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:24:40', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (71, 5, 'ptgly', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:26:14', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (72, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:26:24', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (73, 5, 'finance', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:26:30', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (74, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:27:12', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (75, 5, 'ptgly', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:29:58', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (76, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:30:17', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (77, 5, 'finance', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:38:35', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (78, 5, 'finance_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:38:46', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (79, 5, 'finance_m', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:38:54', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (80, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:39:14', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (81, 5, 'sale', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:39:21', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (82, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-30 23:39:34', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (83, 5, 'finance', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 08:47:28', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (84, 1, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 08:47:39', 0, '验证码错误或已过期');
INSERT INTO `sys_login_log` VALUES (85, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 08:47:45', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (86, 5, 'sale', 2, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 08:48:03', 1, '注销成功');
INSERT INTO `sys_login_log` VALUES (87, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 08:48:19', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (88, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 08:55:21', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (89, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 10:43:49', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (90, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 10:43:57', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (91, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 10:46:03', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (92, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 10:55:37', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (93, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 10:56:17', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (94, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 10:56:26', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (95, 1, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 10:56:38', 0, '验证码错误或已过期');
INSERT INTO `sys_login_log` VALUES (96, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 10:56:42', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (97, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 10:59:10', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (98, 1, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 10:59:23', 0, '验证码错误或已过期');
INSERT INTO `sys_login_log` VALUES (99, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/156.0.0.0 Sa', 'Chrome', 'Windows NT', '2026-10-31 10:59:34', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (100, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:20:22', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (101, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:20:24', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (102, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:20:27', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (103, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:20:31', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (104, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:20:42', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (105, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:21:17', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (106, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:21:21', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (107, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:21:25', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (108, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:21:29', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (109, 5, 'finance_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:21:33', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (110, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:23:05', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (111, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:23:09', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (112, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:23:12', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (113, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:24:23', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (114, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:24:26', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (115, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:24:30', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (116, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:24:34', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (117, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:24:53', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (118, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:24:57', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (119, 5, 'finance', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:25:00', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (120, 5, 'ptgly', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:31:38', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (121, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:31:40', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (122, 5, 'sale_m', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 11:31:44', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (123, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 12:25:44', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (124, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 12:25:51', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (125, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 12:25:51', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (126, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 12:30:50', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (127, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 12:37:52', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (128, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 12:38:03', 0, '用户名或密码错误');
INSERT INTO `sys_login_log` VALUES (129, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 12:38:48', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (130, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 12:46:27', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (131, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 12:49:12', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (132, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 12:50:35', 1, '登录成功');
INSERT INTO `sys_login_log` VALUES (133, 5, 'sale', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT; Windows NT 10.0; zh-CN) WindowsPowerShell/5.1.19041.6456', 'WindowsPowerShell', 'Windows NT', '2026-10-31 12:51:53', 1, '登录成功');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID（0为顶级）',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `menu_type` tinyint NOT NULL COMMENT '菜单类型：1目录 2菜单 3按钮',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由路径',
  `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '组件路径',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '权限标识',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图标',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `visible` tinyint NULL DEFAULT 1 COMMENT '对租户是否可见：0隐藏 1显示',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 181 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (2, 0, '系统管理', 1, '/system', '', '', 'Tools', 2, 1, 1, '2026-04-18 22:38:02', '2026-07-06 21:12:04');
INSERT INTO `sys_menu` VALUES (3, 2, '用户管理', 2, '/system/user', 'system/user/index', '', 'user', 1, 1, 1, '2026-04-18 22:38:02', '2026-04-22 15:48:42');
INSERT INTO `sys_menu` VALUES (4, 3, '用户查询', 3, '', '', 'system:user:list', '', 0, 1, 1, '2026-04-18 22:38:02', NULL);
INSERT INTO `sys_menu` VALUES (5, 3, '用户新增', 3, '', '', 'system:user:add', '', 1, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (6, 3, '用户修改', 3, '', '', 'system:user:edit', '', 2, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (7, 3, '用户删除', 3, '', '', 'system:user:delete', '', 3, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (8, 2, '角色管理', 2, '/system/role', 'system/role/index', '', 'Avatar', 2, 1, 1, '2026-04-18 22:38:02', '2026-04-22 15:31:47');
INSERT INTO `sys_menu` VALUES (9, 8, '角色查询', 3, '', '', 'system:role:list', '', 0, 1, 1, '2026-04-18 22:38:02', NULL);
INSERT INTO `sys_menu` VALUES (10, 8, '角色新增', 3, '', '', 'system:role:add', '', 1, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (11, 8, '角色修改', 3, '', '', 'system:role:edit', '', 2, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (12, 8, '角色删除', 3, '', '', 'system:role:delete', '', 3, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (13, 8, '分配权限', 3, '', '', 'system:role:permission', '', 4, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (14, 2, '菜单管理', 2, '/system/menu', 'system/menu/index', '', 'menu', 3, 0, 1, '2026-04-18 22:38:02', '2026-04-22 16:13:56');
INSERT INTO `sys_menu` VALUES (15, 14, '菜单查询', 3, '', '', 'system:menu:list', '', 0, 1, 1, '2026-04-18 22:38:02', NULL);
INSERT INTO `sys_menu` VALUES (16, 14, '菜单新增', 3, '', '', 'system:menu:add', '', 1, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (17, 14, '菜单修改', 3, '', '', 'system:menu:edit', '', 2, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (18, 14, '菜单删除', 3, '', '', 'system:menu:delete', '', 3, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (19, 2, '部门管理', 2, '/system/dept', 'system/dept/index', '', 'Connection', 0, 1, 1, '2026-04-18 22:38:02', '2026-04-22 15:31:36');
INSERT INTO `sys_menu` VALUES (20, 19, '部门查询', 3, '', '', 'system:dept:list', '', 0, 1, 1, '2026-04-18 22:38:02', '2026-04-28 11:57:43');
INSERT INTO `sys_menu` VALUES (21, 19, '部门新增', 3, '', '', 'system:dept:add', '', 1, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (22, 19, '部门修改', 3, '', '', 'system:dept:edit', '', 2, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (23, 19, '部门删除', 3, '', '', 'system:dept:delete', '', 3, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (24, 2, '租户管理', 2, '/system/tenant', 'system/tenant/index', '', 'OfficeBuilding', 4, 0, 1, '2026-04-18 22:38:02', '2026-04-22 16:17:03');
INSERT INTO `sys_menu` VALUES (25, 24, '租户查询', 3, '', '', 'system:tenant:list', '', 0, 1, 1, '2026-04-18 22:38:02', '2026-04-22 16:17:06');
INSERT INTO `sys_menu` VALUES (26, 24, '租户新增', 3, '', '', 'system:tenant:add', '', 1, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (27, 24, '租户修改', 3, '', '', 'system:tenant:edit', '', 2, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (28, 24, '租户删除', 3, '', '', 'system:tenant:delete', '', 3, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (29, 24, '租户延期', 3, '', '', 'system:tenant:extend', '', 4, 1, 1, '2026-04-18 22:38:02', '2026-04-18 22:38:02');
INSERT INTO `sys_menu` VALUES (35, 2, '日志管理', 1, '/system/log', '', '', 'View', 5, 1, 1, '2026-04-18 22:38:02', '2026-04-22 15:31:59');
INSERT INTO `sys_menu` VALUES (36, 35, '登录日志', 2, '/system/log/login', 'system/log/login', 'system:log:login:list', 'Unlock', 0, 1, 1, '2026-04-18 22:38:02', NULL);
INSERT INTO `sys_menu` VALUES (37, 35, '操作日志', 2, '/system/log/oper', 'system/log/oper', 'system:log:oper:list', 'Operation', 1, 1, 1, '2026-04-18 22:38:02', NULL);
INSERT INTO `sys_menu` VALUES (40, 36, '清理日志', 3, '', '', 'system:log:login:clean', '', 0, 1, 1, NULL, NULL);
INSERT INTO `sys_menu` VALUES (41, 37, '清理日志', 3, '', '', 'system:log:oper:clean', '', 0, 1, 1, NULL, NULL);
INSERT INTO `sys_menu` VALUES (43, 2, '字典管理', 2, '/system/dict-type', 'system/dict-type/index', 'system:dict:list', 'SetUp', 6, 1, 1, '2026-04-28 14:49:27', '2026-04-28 20:02:37');
INSERT INTO `sys_menu` VALUES (44, 43, '字典类型查询', 3, '', '', 'system:dict:query', '', 0, 1, 1, '2026-04-28 14:49:27', '2026-04-28 14:49:27');
INSERT INTO `sys_menu` VALUES (45, 43, '字典类型新增', 3, '', '', 'system:dict:add', '', 1, 1, 1, '2026-04-28 14:49:27', '2026-04-28 14:49:27');
INSERT INTO `sys_menu` VALUES (46, 43, '字典类型修改', 3, '', '', 'system:dict:edit', '', 2, 1, 1, '2026-04-28 14:49:27', '2026-04-28 14:49:27');
INSERT INTO `sys_menu` VALUES (47, 43, '字典类型删除', 3, '', '', 'system:dict:delete', '', 3, 1, 1, '2026-04-28 14:49:27', '2026-04-28 14:49:27');
INSERT INTO `sys_menu` VALUES (53, 2, '参数管理', 2, '/system/config', 'system/config/index', 'system:config:list', 'Postcard', 8, 1, 1, '2026-04-28 14:49:27', '2026-04-28 20:03:01');
INSERT INTO `sys_menu` VALUES (54, 53, '参数查询', 3, '', '', 'system:config:query', '', 0, 1, 1, '2026-04-28 14:49:27', '2026-04-28 14:49:27');
INSERT INTO `sys_menu` VALUES (55, 53, '参数新增', 3, '', '', 'system:config:add', '', 1, 1, 1, '2026-04-28 14:49:27', '2026-04-28 14:49:27');
INSERT INTO `sys_menu` VALUES (56, 53, '参数修改', 3, '', '', 'system:config:edit', '', 2, 1, 1, '2026-04-28 14:49:27', '2026-04-28 14:49:27');
INSERT INTO `sys_menu` VALUES (57, 53, '参数删除', 3, '', '', 'system:config:delete', '', 3, 1, 1, '2026-04-28 14:49:27', '2026-04-28 14:49:27');
INSERT INTO `sys_menu` VALUES (100, 0, 'CRM管理', 1, '/crm', '', '', 'Briefcase', 1, 1, 1, '2026-04-29 10:38:02', '2026-07-06 21:11:44');
INSERT INTO `sys_menu` VALUES (101, 100, '客户管理', 2, '/crm/customer', 'crm/customer/index', 'crm:customer:list', 'User', 1, 1, 1, '2026-04-29 10:38:02', '2026-04-30 16:48:48');
INSERT INTO `sys_menu` VALUES (102, 101, '客户查询', 3, '', '', 'crm:customer:query', '', 0, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (103, 101, '客户新增', 3, '', '', 'crm:customer:add', '', 1, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (104, 101, '客户修改', 3, '', '', 'crm:customer:edit', '', 2, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (105, 101, '客户删除', 3, '', '', 'crm:customer:delete', '', 3, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (106, 101, '领取客户', 3, '', '', 'crm:customer:pool:take', '', 4, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (107, 101, '释放客户', 3, '', '', 'crm:customer:pool:release', '', 5, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (108, 101, '转移客户', 3, '', '', 'crm:customer:pool:transfer', '', 6, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (109, 100, '公海池', 2, '/crm/pool', 'crm/pool/index', 'crm:customer:pool', 'FolderOpened', 1, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (110, 100, '客户标签', 2, '/crm/tag', 'crm/tag/index', 'crm:customer:tag:list', 'PriceTag', 2, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (111, 110, '标签查询', 3, '', '', 'crm:customer:tag:query', '', 0, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (112, 110, '标签新增', 3, '', '', 'crm:customer:tag:add', '', 1, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (113, 110, '标签修改', 3, '', '', 'crm:customer:tag:edit', '', 2, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (114, 110, '标签删除', 3, '', '', 'crm:customer:tag:delete', '', 3, 1, 1, '2026-04-29 10:38:02', '2026-04-29 10:38:02');
INSERT INTO `sys_menu` VALUES (115, 100, '产品分类', 2, '/crm/product/category', 'crm/product/category/index', 'crm:product:category:list', 'Grid', 3, 1, 1, '2026-04-30 15:08:55', '2026-04-30 15:08:55');
INSERT INTO `sys_menu` VALUES (116, 115, '分类查询', 3, '', '', 'crm:product:category:query', '', 0, 1, 1, '2026-04-30 15:08:55', '2026-04-30 15:08:55');
INSERT INTO `sys_menu` VALUES (117, 115, '分类新增', 3, '', '', 'crm:product:category:add', '', 1, 1, 1, '2026-04-30 15:08:55', '2026-04-30 15:08:55');
INSERT INTO `sys_menu` VALUES (118, 115, '分类修改', 3, '', '', 'crm:product:category:edit', '', 2, 1, 1, '2026-04-30 15:08:55', '2026-04-30 15:08:55');
INSERT INTO `sys_menu` VALUES (119, 115, '分类删除', 3, '', '', 'crm:product:category:delete', '', 3, 1, 1, '2026-04-30 15:08:55', '2026-04-30 15:08:55');
INSERT INTO `sys_menu` VALUES (120, 100, '产品管理', 2, '/crm/product', 'crm/product/index', 'crm:product:list', 'ShoppingCart', 4, 1, 1, '2026-04-30 15:08:55', '2026-04-30 15:08:55');
INSERT INTO `sys_menu` VALUES (121, 120, '产品查询', 3, '', '', 'crm:product:query', '', 0, 1, 1, '2026-04-30 15:08:55', '2026-04-30 15:08:55');
INSERT INTO `sys_menu` VALUES (122, 120, '产品新增', 3, '', '', 'crm:product:add', '', 1, 1, 1, '2026-04-30 15:08:55', '2026-04-30 15:08:55');
INSERT INTO `sys_menu` VALUES (123, 120, '产品修改', 3, '', '', 'crm:product:edit', '', 2, 1, 1, '2026-04-30 15:08:55', '2026-04-30 15:08:55');
INSERT INTO `sys_menu` VALUES (124, 120, '产品删除', 3, '', '', 'crm:product:delete', '', 3, 1, 1, '2026-04-30 15:08:55', '2026-04-30 15:08:55');
INSERT INTO `sys_menu` VALUES (125, 100, '线索管理', 2, '/crm/lead', 'crm/lead/index', 'crm:lead:list', 'Guide', 5, 1, 1, '2026-04-30 15:09:01', '2026-04-30 15:09:01');
INSERT INTO `sys_menu` VALUES (126, 125, '线索查询', 3, '', '', 'crm:lead:query', '', 0, 1, 1, '2026-04-30 15:09:01', '2026-04-30 15:09:01');
INSERT INTO `sys_menu` VALUES (127, 125, '线索新增', 3, '', '', 'crm:lead:add', '', 1, 1, 1, '2026-04-30 15:09:01', '2026-04-30 15:09:01');
INSERT INTO `sys_menu` VALUES (128, 125, '线索修改', 3, '', '', 'crm:lead:edit', '', 2, 1, 1, '2026-04-30 15:09:01', '2026-04-30 15:09:01');
INSERT INTO `sys_menu` VALUES (129, 125, '线索删除', 3, '', '', 'crm:lead:delete', '', 3, 1, 1, '2026-04-30 15:09:01', '2026-04-30 15:09:01');
INSERT INTO `sys_menu` VALUES (130, 125, '转商机', 3, '', '', 'crm:lead:convert', '', 4, 1, 1, '2026-04-30 15:09:01', '2026-04-30 15:09:01');
INSERT INTO `sys_menu` VALUES (131, 100, '商机管理', 2, '/crm/opportunity', 'crm/opportunity/index', 'crm:opportunity:list', 'TrendCharts', 4, 1, 1, '2026-04-30 15:17:09', '2026-04-30 15:17:09');
INSERT INTO `sys_menu` VALUES (132, 131, '商机查询', 3, '', '', 'crm:opportunity:query', '', 0, 1, 1, '2026-04-30 15:17:09', '2026-04-30 15:17:09');
INSERT INTO `sys_menu` VALUES (133, 131, '商机新增', 3, '', '', 'crm:opportunity:add', '', 1, 1, 1, '2026-04-30 15:17:09', '2026-04-30 15:17:09');
INSERT INTO `sys_menu` VALUES (134, 131, '商机修改', 3, '', '', 'crm:opportunity:edit', '', 2, 1, 1, '2026-04-30 15:17:09', '2026-04-30 15:17:09');
INSERT INTO `sys_menu` VALUES (135, 131, '商机删除', 3, '', '', 'crm:opportunity:delete', '', 3, 1, 1, '2026-04-30 15:17:09', '2026-04-30 15:17:09');
INSERT INTO `sys_menu` VALUES (136, 131, '阶段推进', 3, '', '', 'crm:opportunity:advance', '', 4, 1, 1, '2026-04-30 15:17:09', '2026-04-30 15:17:09');
INSERT INTO `sys_menu` VALUES (137, 131, '成交/失败', 3, '', '', 'crm:opportunity:close', '', 5, 1, 1, '2026-04-30 15:17:09', '2026-04-30 15:17:09');
INSERT INTO `sys_menu` VALUES (138, 100, '报价管理', 2, '/crm/quote', 'crm/quote/index', 'crm:quote:list', 'Tickets', 5, 1, 1, '2026-04-30 15:17:18', '2026-04-30 15:17:18');
INSERT INTO `sys_menu` VALUES (139, 138, '报价查询', 3, '', '', 'crm:quote:query', '', 0, 1, 1, '2026-04-30 15:17:18', '2026-04-30 15:17:18');
INSERT INTO `sys_menu` VALUES (140, 138, '报价新增', 3, '', '', 'crm:quote:add', '', 1, 1, 1, '2026-04-30 15:17:18', '2026-04-30 15:17:18');
INSERT INTO `sys_menu` VALUES (141, 138, '报价修改', 3, '', '', 'crm:quote:edit', '', 2, 1, 1, '2026-04-30 15:17:18', '2026-04-30 15:17:18');
INSERT INTO `sys_menu` VALUES (142, 138, '报价删除', 3, '', '', 'crm:quote:delete', '', 3, 1, 1, '2026-04-30 15:17:18', '2026-04-30 15:17:18');
INSERT INTO `sys_menu` VALUES (143, 138, '发送报价', 3, '', '', 'crm:quote:send', '', 4, 1, 1, '2026-04-30 15:17:18', '2026-04-30 15:17:18');
INSERT INTO `sys_menu` VALUES (144, 138, '报价中标', 3, '', '', 'crm:quote:accept', '', 5, 1, 1, '2026-04-30 15:17:18', '2026-04-30 15:17:18');
INSERT INTO `sys_menu` VALUES (145, 138, '报价拒绝', 3, '', '', 'crm:quote:reject', '', 6, 1, 1, '2026-04-30 15:17:18', '2026-04-30 15:17:18');
INSERT INTO `sys_menu` VALUES (146, 100, '合同管理', 2, '/crm/contract', 'crm/contract/index', 'crm:contract:list', 'Document', 6, 1, 1, '2026-04-30 15:17:27', '2026-04-30 15:17:27');
INSERT INTO `sys_menu` VALUES (147, 146, '合同查询', 3, '', '', 'crm:contract:query', '', 0, 1, 1, '2026-04-30 15:17:27', '2026-04-30 15:17:27');
INSERT INTO `sys_menu` VALUES (148, 146, '合同新增', 3, '', '', 'crm:contract:add', '', 1, 1, 1, '2026-04-30 15:17:27', '2026-04-30 15:17:27');
INSERT INTO `sys_menu` VALUES (149, 146, '合同修改', 3, '', '', 'crm:contract:edit', '', 2, 1, 1, '2026-04-30 15:17:27', '2026-04-30 15:17:27');
INSERT INTO `sys_menu` VALUES (150, 146, '合同删除', 3, '', '', 'crm:contract:delete', '', 3, 1, 1, '2026-04-30 15:17:27', '2026-04-30 15:17:27');
INSERT INTO `sys_menu` VALUES (151, 146, '提交审批', 3, '', '', 'crm:contract:submit', '', 4, 1, 1, '2026-04-30 15:17:27', '2026-04-30 15:17:27');
INSERT INTO `sys_menu` VALUES (152, 146, '审批通过', 3, '', '', 'crm:contract:approve', '', 5, 1, 1, '2026-04-30 15:17:27', '2026-04-30 15:17:27');
INSERT INTO `sys_menu` VALUES (153, 146, '终止合同', 3, '', '', 'crm:contract:terminate', '', 6, 1, 1, '2026-04-30 15:17:27', '2026-04-30 15:17:27');
INSERT INTO `sys_menu` VALUES (154, 100, '发票管理', 2, '/crm/invoice', 'crm/invoice/index', 'crm:invoice:list', 'ToiletPaper', 7, 1, 1, '2026-04-30 15:17:35', '2026-04-30 16:45:12');
INSERT INTO `sys_menu` VALUES (155, 154, '发票查询', 3, '', '', 'crm:invoice:query', '', 0, 1, 1, '2026-04-30 15:17:35', '2026-04-30 15:17:35');
INSERT INTO `sys_menu` VALUES (156, 154, '发票新增', 3, '', '', 'crm:invoice:add', '', 1, 1, 1, '2026-04-30 15:17:35', '2026-04-30 15:17:35');
INSERT INTO `sys_menu` VALUES (157, 154, '发票修改', 3, '', '', 'crm:invoice:edit', '', 2, 1, 1, '2026-04-30 15:17:35', '2026-04-30 15:17:35');
INSERT INTO `sys_menu` VALUES (158, 154, '发票删除', 3, '', '', 'crm:invoice:delete', '', 3, 1, 1, '2026-04-30 15:17:35', '2026-04-30 15:17:35');
INSERT INTO `sys_menu` VALUES (159, 154, '发票作废', 3, '', '', 'crm:invoice:cancel', '', 4, 1, 1, '2026-04-30 15:17:35', '2026-04-30 15:17:35');
INSERT INTO `sys_menu` VALUES (160, 100, '收款计划', 2, '/crm/payment/plan', 'crm/payment/plan/index', 'crm:payment:plan:list', 'Money', 8, 1, 1, '2026-04-30 15:17:41', '2026-04-30 15:17:41');
INSERT INTO `sys_menu` VALUES (161, 160, '计划查询', 3, '', '', 'crm:payment:plan:query', '', 0, 1, 1, '2026-04-30 15:17:41', '2026-04-30 15:17:41');
INSERT INTO `sys_menu` VALUES (162, 160, '计划新增', 3, '', '', 'crm:payment:plan:add', '', 1, 1, 1, '2026-04-30 15:17:41', '2026-04-30 15:17:41');
INSERT INTO `sys_menu` VALUES (163, 160, '计划修改', 3, '', '', 'crm:payment:plan:edit', '', 2, 1, 1, '2026-04-30 15:17:41', '2026-04-30 15:17:41');
INSERT INTO `sys_menu` VALUES (164, 160, '计划删除', 3, '', '', 'crm:payment:plan:delete', '', 3, 1, 1, '2026-04-30 15:17:41', '2026-04-30 15:17:41');
INSERT INTO `sys_menu` VALUES (165, 100, '收款记录', 2, '/crm/payment/record', 'crm/payment/record/index', 'crm:payment:record:list', 'Wallet', 9, 1, 1, '2026-04-30 15:17:41', '2026-04-30 15:17:41');
INSERT INTO `sys_menu` VALUES (166, 165, '记录查询', 3, '', '', 'crm:payment:record:query', '', 0, 1, 1, '2026-04-30 15:17:41', '2026-04-30 15:17:41');
INSERT INTO `sys_menu` VALUES (167, 165, '记录新增', 3, '', '', 'crm:payment:record:add', '', 1, 1, 1, '2026-04-30 15:17:41', '2026-04-30 15:17:41');
INSERT INTO `sys_menu` VALUES (168, 165, '记录修改', 3, '', '', 'crm:payment:record:edit', '', 2, 1, 1, '2026-04-30 15:17:41', '2026-04-30 15:17:41');
INSERT INTO `sys_menu` VALUES (169, 165, '记录删除', 3, '', '', 'crm:payment:record:delete', '', 3, 1, 1, '2026-04-30 15:17:41', '2026-04-30 15:17:41');
INSERT INTO `sys_menu` VALUES (170, 165, '收款确认', 3, '', '', 'crm:payment:record:confirm', '', 4, 1, 1, '2026-04-30 15:17:41', '2026-04-30 15:17:41');
INSERT INTO `sys_menu` VALUES (171, 100, '数据分析', 1, '/crm/analysis', '', '', 'DataAnalysis', 10, 1, 1, '2026-04-30 15:17:48', '2026-04-30 15:17:48');
INSERT INTO `sys_menu` VALUES (172, 171, '销售分析', 2, '/crm/analysis/sales', 'crm/analysis/sales', 'crm:analysis:sales', 'TrendCharts', 0, 1, 1, '2026-04-30 15:17:48', '2026-04-30 15:17:48');
INSERT INTO `sys_menu` VALUES (173, 171, '客户分析', 2, '/crm/analysis/customer', 'crm/analysis/customer', 'crm:analysis:customer', 'PieChart', 1, 1, 1, '2026-04-30 15:17:48', '2026-04-30 15:17:48');
INSERT INTO `sys_menu` VALUES (174, 171, '商机分析', 2, '/crm/analysis/opportunity', 'crm/analysis/opportunity', 'crm:analysis:opportunity', 'DataLine', 2, 1, 1, '2026-04-30 15:17:48', '2026-04-30 15:17:48');
INSERT INTO `sys_menu` VALUES (175, 171, '合同分析', 2, '/crm/analysis/contract', 'crm/analysis/contract', 'crm:analysis:contract', 'Document', 3, 1, 1, '2026-04-30 15:17:48', '2026-04-30 15:17:48');
INSERT INTO `sys_menu` VALUES (176, 0, '仪表盘', 2, '/crm/dashboard', 'crm/dashboard/index', 'crm:dashboard', 'HomeFilled', 0, 1, 1, '2026-04-30 15:17:48', '2026-07-06 21:11:31');
INSERT INTO `sys_menu` VALUES (177, 172, '查看销售分析', 3, '', '', 'crm:analysis:sales:view', '', 0, 1, 1, '2026-04-30 15:17:48', '2026-04-30 15:17:48');
INSERT INTO `sys_menu` VALUES (178, 173, '查看客户分析', 3, '', '', 'crm:analysis:customer:view', '', 0, 1, 1, '2026-04-30 15:17:48', '2026-04-30 15:17:48');
INSERT INTO `sys_menu` VALUES (179, 174, '查看商机分析', 3, '', '', 'crm:analysis:opportunity:view', '', 0, 1, 1, '2026-04-30 15:17:48', '2026-04-30 15:17:48');
INSERT INTO `sys_menu` VALUES (180, 175, '查看合同分析', 3, '', '', 'crm:analysis:contract:view', '', 0, 1, 1, '2026-04-30 15:17:48', '2026-04-30 15:17:48');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `user_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模块名称',
  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作类型',
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作对象类型',
  `target_id` bigint NULL DEFAULT NULL COMMENT '操作对象ID',
  `request_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数（JSON）',
  `response_status` tinyint NULL DEFAULT 1 COMMENT '响应状态：1成功 0失败',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `oper_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `duration` int NULL DEFAULT NULL COMMENT '执行耗时(ms)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_code` ASC) USING BTREE,
  INDEX `idx_oper_time`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 125 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (1, 1, 'admin', '租户管理', '删除', '租户', 4, '[4]', 1, '127.0.0.1', '2026-10-30 18:43:52', 11);
INSERT INTO `sys_oper_log` VALUES (2, 1, 'admin', '租户管理', '删除', '租户', 3, '[3]', 1, '127.0.0.1', '2026-10-30 18:43:56', 8);
INSERT INTO `sys_oper_log` VALUES (3, 1, 'admin', '角色管理', '删除', '角色', 2, '[2]', 1, '127.0.0.1', '2026-10-30 18:44:06', 14);
INSERT INTO `sys_oper_log` VALUES (4, 1, 'admin', '角色管理', '删除', '角色', 3, '[3]', 1, '127.0.0.1', '2026-10-30 18:44:08', 12);
INSERT INTO `sys_oper_log` VALUES (5, 1, 'admin', '角色管理', '删除', '角色', 4, '[4]', 1, '127.0.0.1', '2026-10-30 18:44:10', 7);
INSERT INTO `sys_oper_log` VALUES (6, 1, 'admin', '角色管理', '删除', '角色', 5, '[5]', 1, '127.0.0.1', '2026-10-30 18:44:12', 12);
INSERT INTO `sys_oper_log` VALUES (7, 1, 'admin', '角色管理', '删除', '角色', 8, '[8]', 1, '127.0.0.1', '2026-10-30 18:44:14', 11);
INSERT INTO `sys_oper_log` VALUES (8, 1, 'admin', '角色管理', '删除', '角色', 9, '[9]', 1, '127.0.0.1', '2026-10-30 18:44:15', 12);
INSERT INTO `sys_oper_log` VALUES (9, 1, 'admin', '角色管理', '删除', '角色', 10, '[10]', 1, '127.0.0.1', '2026-10-30 18:44:17', 11);
INSERT INTO `sys_oper_log` VALUES (10, 1, 'admin', '租户管理', '新增', '租户', 5, '[{\"id\":null,\"tenantName\":\"金石小区\",\"tenantCode\":\"JINSHI\",\"status\":1,\"expireTime\":null,\"contactName\":\"\",\"contactPhone\":\"\"}]', 1, '127.0.0.1', '2026-10-30 18:48:26', 32);
INSERT INTO `sys_oper_log` VALUES (11, 1, 'admin', '用户管理', '新增', '用户', 16, '[{\"id\":null,\"userCode\":\"ptgly\",\"password\":\"123456\",\"userName\":\"平台管理员\",\"email\":\"\",\"phone\":\"\",\"avatar\":null,\"gender\":null,\"status\":1,\"deptId\":24,\"roleIds\":[]}]', 1, '127.0.0.1', '2026-10-30 18:49:29', 81);
INSERT INTO `sys_oper_log` VALUES (12, 1, 'admin', '角色管理', '新增', '角色', 11, '[{\"id\":null,\"tenantId\":5,\"roleName\":\"平台管理员\",\"roleCode\":\"1\",\"dataScope\":2,\"status\":1,\"remark\":\"\"}]', 1, '127.0.0.1', '2026-10-30 18:50:23', 14);
INSERT INTO `sys_oper_log` VALUES (13, 1, 'admin', '角色管理', '新增', '角色', 12, '[{\"id\":null,\"tenantId\":5,\"roleName\":\"财务\",\"roleCode\":\"2\",\"dataScope\":5,\"status\":1,\"remark\":\"\"}]', 1, '127.0.0.1', '2026-10-30 18:50:48', 10);
INSERT INTO `sys_oper_log` VALUES (14, 1, 'admin', '角色管理', '新增', '角色', 13, '[{\"id\":null,\"tenantId\":5,\"roleName\":\"财务主管\",\"roleCode\":\"3\",\"dataScope\":4,\"status\":1,\"remark\":\"\"}]', 1, '127.0.0.1', '2026-10-30 18:51:04', 29);
INSERT INTO `sys_oper_log` VALUES (15, 1, 'admin', '角色管理', '新增', '角色', 14, '[{\"id\":null,\"tenantId\":5,\"roleName\":\"销售\",\"roleCode\":\"4\",\"dataScope\":5,\"status\":1,\"remark\":\"\"}]', 1, '127.0.0.1', '2026-10-30 18:51:20', 12);
INSERT INTO `sys_oper_log` VALUES (16, 1, 'admin', '角色管理', '新增', '角色', 15, '[{\"id\":null,\"tenantId\":5,\"roleName\":\"销售主管\",\"roleCode\":\"5\",\"dataScope\":4,\"status\":1,\"remark\":\"\"}]', 1, '127.0.0.1', '2026-10-30 18:51:33', 11);
INSERT INTO `sys_oper_log` VALUES (17, 1, 'admin', '用户管理', '新增', '用户', 17, '[{\"id\":null,\"userCode\":\"sale\",\"password\":\"123456\",\"userName\":\"销售\",\"email\":\"\",\"phone\":\"\",\"avatar\":null,\"gender\":null,\"status\":1,\"deptId\":24,\"roleIds\":[14]}]', 1, '127.0.0.1', '2026-10-30 18:52:08', 105);
INSERT INTO `sys_oper_log` VALUES (18, 1, 'admin', '用户管理', '新增', '用户', 18, '[{\"id\":null,\"userCode\":\"sale_m\",\"password\":\"123456\",\"userName\":\"销售主管\",\"email\":\"\",\"phone\":\"\",\"avatar\":null,\"gender\":null,\"status\":1,\"deptId\":24,\"roleIds\":[13]}]', 1, '127.0.0.1', '2026-10-30 18:52:32', 86);
INSERT INTO `sys_oper_log` VALUES (19, 1, 'admin', '用户管理', '新增', '用户', 19, '[{\"id\":null,\"userCode\":\"finance\",\"password\":\"123456\",\"userName\":\"财务\",\"email\":\"\",\"phone\":\"\",\"avatar\":null,\"gender\":null,\"status\":1,\"deptId\":24,\"roleIds\":[12]}]', 1, '127.0.0.1', '2026-10-30 18:52:49', 91);
INSERT INTO `sys_oper_log` VALUES (20, 1, 'admin', '用户管理', '新增', '用户', 20, '[{\"id\":null,\"userCode\":\"finance_m\",\"password\":\"123456\",\"userName\":\"财务主管\",\"email\":\"\",\"phone\":\"\",\"avatar\":null,\"gender\":null,\"status\":1,\"deptId\":24,\"roleIds\":[13]}]', 1, '127.0.0.1', '2026-10-30 18:53:08', 82);
INSERT INTO `sys_oper_log` VALUES (21, 1, 'admin', '部门管理', '新增', '部门', 25, '[{\"id\":null,\"tenantId\":5,\"parentId\":24,\"deptName\":\"财务部\",\"leaderId\":null,\"sort\":0,\"status\":1}]', 1, '127.0.0.1', '2026-10-30 18:53:30', 17);
INSERT INTO `sys_oper_log` VALUES (22, 1, 'admin', '部门管理', '新增', '部门', 26, '[{\"id\":null,\"tenantId\":5,\"parentId\":24,\"deptName\":\"销售部\",\"leaderId\":null,\"sort\":0,\"status\":1}]', 1, '127.0.0.1', '2026-10-30 18:53:36', 13);
INSERT INTO `sys_oper_log` VALUES (23, 1, 'admin', '部门管理', '修改', '部门', 25, '[{\"id\":25,\"tenantId\":5,\"parentId\":24,\"deptName\":\"财务部\",\"leaderId\":20,\"sort\":0,\"status\":1}]', 1, '127.0.0.1', '2026-10-30 18:53:44', 11);
INSERT INTO `sys_oper_log` VALUES (24, 1, 'admin', '部门管理', '修改', '部门', 26, '[{\"id\":26,\"tenantId\":5,\"parentId\":24,\"deptName\":\"销售部\",\"leaderId\":18,\"sort\":0,\"status\":1}]', 1, '127.0.0.1', '2026-10-30 18:53:52', 11);
INSERT INTO `sys_oper_log` VALUES (25, 1, 'admin', '用户管理', '修改', '用户', 20, '[{\"id\":20,\"userCode\":\"finance_m\",\"password\":\"\",\"userName\":\"财务主管\",\"email\":\"\",\"phone\":\"\",\"avatar\":null,\"gender\":null,\"status\":1,\"deptId\":25,\"roleIds\":[13]}]', 1, '127.0.0.1', '2026-10-30 18:54:11', 9);
INSERT INTO `sys_oper_log` VALUES (26, 1, 'admin', '用户管理', '修改', '用户', 19, '[{\"id\":19,\"userCode\":\"finance\",\"password\":\"\",\"userName\":\"财务\",\"email\":\"\",\"phone\":\"\",\"avatar\":null,\"gender\":null,\"status\":1,\"deptId\":25,\"roleIds\":[12]}]', 1, '127.0.0.1', '2026-10-30 18:54:17', 15);
INSERT INTO `sys_oper_log` VALUES (27, 1, 'admin', '部门管理', '新增', '部门', 27, '[{\"id\":null,\"tenantId\":5,\"parentId\":24,\"deptName\":\"管理部\",\"leaderId\":16,\"sort\":0,\"status\":1}]', 1, '127.0.0.1', '2026-10-30 18:54:52', 17);
INSERT INTO `sys_oper_log` VALUES (28, 1, 'admin', '用户管理', '修改', '用户', 17, '[{\"id\":17,\"userCode\":\"sale\",\"password\":\"\",\"userName\":\"销售\",\"email\":\"\",\"phone\":\"\",\"avatar\":null,\"gender\":null,\"status\":1,\"deptId\":26,\"roleIds\":[14]}]', 1, '127.0.0.1', '2026-10-30 18:55:01', 11);
INSERT INTO `sys_oper_log` VALUES (29, 1, 'admin', '用户管理', '修改', '用户', 18, '[{\"id\":18,\"userCode\":\"sale_m\",\"password\":\"\",\"userName\":\"销售主管\",\"email\":\"\",\"phone\":\"\",\"avatar\":null,\"gender\":null,\"status\":1,\"deptId\":26,\"roleIds\":[15]}]', 1, '127.0.0.1', '2026-10-30 18:55:17', 14);
INSERT INTO `sys_oper_log` VALUES (30, 1, 'admin', '用户管理', '修改', '用户', 16, '[{\"id\":16,\"userCode\":\"ptgly\",\"password\":\"\",\"userName\":\"平台管理员\",\"email\":\"\",\"phone\":\"\",\"avatar\":null,\"gender\":null,\"status\":1,\"deptId\":27,\"roleIds\":[11]}]', 1, '127.0.0.1', '2026-10-30 18:55:26', 11);
INSERT INTO `sys_oper_log` VALUES (31, 5, 'sale_m', '产品管理', '新增分类', '分类', 1, '[{\"id\":null,\"parentId\":0,\"categoryName\":\"整机\",\"sort\":1,\"status\":1}]', 1, '127.0.0.1', '2026-10-30 19:47:06', 52);
INSERT INTO `sys_oper_log` VALUES (32, 5, 'sale_m', '产品管理', '新增', '产品', 1, '[{\"id\":null,\"categoryId\":1,\"productName\":\"商务笔记本电脑\",\"productCode\":\"NB-001\",\"unit\":\"台\",\"spec\":null,\"model\":null,\"price\":8000,\"minPrice\":7000,\"costPrice\":5000,\"description\":null,\"imageUrl\":null,\"status\":1,\"sort\":1}]', 1, '127.0.0.1', '2026-10-30 19:47:06', 12);
INSERT INTO `sys_oper_log` VALUES (33, 5, 'sale_m', '产品管理', '新增分类', '分类', 2, '[{\"id\":null,\"parentId\":0,\"categoryName\":\"整机E2E\",\"sort\":1,\"status\":1}]', 1, '127.0.0.1', '2026-10-30 19:47:43', 6);
INSERT INTO `sys_oper_log` VALUES (34, 5, 'sale_m', '产品管理', '新增', '产品', 2, '[{\"id\":null,\"categoryId\":2,\"productName\":\"商务笔记本电脑E2E\",\"productCode\":\"NB-E2E\",\"unit\":\"台\",\"spec\":null,\"model\":null,\"price\":8000,\"minPrice\":7000,\"costPrice\":5000,\"description\":null,\"imageUrl\":null,\"status\":1,\"sort\":1}]', 1, '127.0.0.1', '2026-10-30 19:47:43', 5);
INSERT INTO `sys_oper_log` VALUES (35, 5, 'sale', '线索管理', '新增', '线索', 1, '[{\"id\":null,\"customerId\":null,\"contactId\":null,\"leadName\":\"华创科技采购意向-E2E2\",\"source\":\"展会\",\"status\":1,\"phone\":\"13800002222\",\"email\":\"hc2@example.com\",\"company\":\"华创科技有限公司\",\"description\":\"需要采购办公笔记本一批\",\"ownerId\":17}]', 1, '127.0.0.1', '2026-10-30 19:47:43', 8);
INSERT INTO `sys_oper_log` VALUES (36, 5, 'sale', '客户管理', '新增', '客户', 1, '[{\"id\":null,\"customerType\":1,\"customerName\":\"华创科技有限公司E2E\",\"industry\":\"IT\",\"companySize\":\"100-500人\",\"province\":\"广东省\",\"city\":\"深圳市\",\"address\":null,\"website\":null,\"gender\":null,\"birthday\":null,\"occupation\":null,\"phone\":\"13800002222\",\"email\":\"hc2@example.com\",\"source\":\"展会\",\"level\":2,\"status\":1,\"remark\":\"E2E客户\",\"tagIds\":null}]', 1, '127.0.0.1', '2026-10-30 19:47:44', 10);
INSERT INTO `sys_oper_log` VALUES (37, 5, 'sale', '线索管理', '修改', '线索', 1, '[{\"id\":1,\"customerId\":1,\"contactId\":null,\"leadName\":\"华创科技采购意向-E2E2\",\"source\":\"展会\",\"status\":2,\"phone\":\"13800002222\",\"email\":\"hc2@example.com\",\"company\":\"华创科技有限公司\",\"description\":\"需要采购办公笔记本一批\",\"ownerId\":17}]', 1, '127.0.0.1', '2026-10-30 19:47:44', 13);
INSERT INTO `sys_oper_log` VALUES (38, 5, 'sale', '客户管理', '跟进', '跟进记录', 1, '[{\"id\":null,\"customerId\":1,\"followType\":\"电话\",\"followContent\":\"首次电话沟通，客户有明确采购意向\",\"nextFollowTime\":null}]', 1, '127.0.0.1', '2026-10-30 19:47:44', 15);
INSERT INTO `sys_oper_log` VALUES (39, 5, 'sale', '线索管理', '转商机', '线索', 1, '[{\"leadId\":1,\"customerId\":1,\"oppName\":\"华创科技笔记本采购商机\",\"expectedAmount\":100000,\"expectedDate\":\"2026-12-31\"}]', 1, '127.0.0.1', '2026-10-30 19:47:44', 25);
INSERT INTO `sys_oper_log` VALUES (40, 5, 'sale', '商机管理', '阶段推进', '商机', 1, '[1,16]', 1, '127.0.0.1', '2026-10-30 19:47:44', 18);
INSERT INTO `sys_oper_log` VALUES (41, 5, 'sale', '线索管理', '新增', '线索', 2, '[{\"id\":null,\"customerId\":null,\"contactId\":null,\"leadName\":\"E2E线索200747\",\"source\":\"官网\",\"status\":1,\"phone\":\"13800138000\",\"email\":\"e2e200747@test.com\",\"company\":\"E2E测试公司200747\",\"description\":\"全流程测试线索\",\"ownerId\":17}]', 1, '127.0.0.1', '2026-10-30 20:07:48', 16);
INSERT INTO `sys_oper_log` VALUES (42, 5, 'sale', '线索管理', '转商机', '线索', 2, '[{\"leadId\":2,\"customerId\":null,\"oppName\":\"E2E商机200747\",\"expectedAmount\":80000,\"expectedDate\":\"2026-11-30\"}]', 1, '127.0.0.1', '2026-10-30 20:07:48', 37);
INSERT INTO `sys_oper_log` VALUES (43, 5, 'sale', '线索管理', '新增', '线索', 3, '[{\"id\":null,\"customerId\":null,\"contactId\":null,\"leadName\":\"E2E-Lead-201846\",\"source\":\"fair\",\"status\":null,\"phone\":\"13900001234\",\"email\":null,\"company\":\"E2E-Co-201846\",\"description\":\"e2e lead\",\"ownerId\":17}]', 1, '127.0.0.1', '2026-10-30 20:18:46', 19);
INSERT INTO `sys_oper_log` VALUES (44, 5, 'sale', '线索管理', '转商机', '线索', 3, '[{\"leadId\":3,\"customerId\":null,\"oppName\":\"E2E-Opp-201846\",\"expectedAmount\":16000,\"expectedDate\":\"2026-11-30\"}]', 1, '127.0.0.1', '2026-10-30 20:18:46', 39);
INSERT INTO `sys_oper_log` VALUES (45, 5, 'sale', '客户管理', '跟进', '跟进记录', 2, '[{\"id\":null,\"customerId\":3,\"followType\":\"phone\",\"followContent\":\"E2E follow-up call\",\"nextFollowTime\":null}]', 1, '127.0.0.1', '2026-10-30 20:18:46', 17);
INSERT INTO `sys_oper_log` VALUES (46, 5, 'sale', '商机管理', '阶段推进', '商机', 3, '[3,14]', 1, '127.0.0.1', '2026-10-30 20:18:46', 24);
INSERT INTO `sys_oper_log` VALUES (47, 5, 'sale', '商机管理', '阶段推进', '商机', 3, '[3,15]', 1, '127.0.0.1', '2026-10-30 20:18:47', 17);
INSERT INTO `sys_oper_log` VALUES (48, 5, 'sale', '商机管理', '阶段推进', '商机', 3, '[3,16]', 1, '127.0.0.1', '2026-10-30 20:18:47', 17);
INSERT INTO `sys_oper_log` VALUES (49, 5, 'sale', '商机管理', '阶段推进', '商机', 3, '[3,17]', 1, '127.0.0.1', '2026-10-30 20:18:47', 15);
INSERT INTO `sys_oper_log` VALUES (50, 5, 'sale', '报价管理', '新增', '报价单', 1, '[{\"id\":null,\"opportunityId\":3,\"customerId\":3,\"quoteName\":\"E2E-Quote-201846\",\"totalAmount\":16000.00,\"discountRate\":0,\"finalAmount\":16000.00,\"expireDate\":\"2026-11-30\",\"remark\":null,\"ownerId\":17,\"products\":[{\"id\":null,\"productId\":2,\"productName\":\"åå¡ç¬è®°æ¬çµèE2E\",\"unit\":\"å°\",\"quantity\":2,\"unitPrice\":8000.00,\"discountRate\":0,\"totalPrice\":16000.00,\"remark\":null}]}]', 1, '127.0.0.1', '2026-10-30 20:18:47', 15);
INSERT INTO `sys_oper_log` VALUES (51, 5, 'sale', '报价管理', '发送', '报价单', 1, '[1]', 1, '127.0.0.1', '2026-10-30 20:18:47', 27);
INSERT INTO `sys_oper_log` VALUES (52, 5, 'sale', '报价管理', '中标', '报价单', 1, '[1]', 1, '127.0.0.1', '2026-10-30 20:18:47', 17);
INSERT INTO `sys_oper_log` VALUES (53, 5, 'sale', '合同管理', '新增', '合同', 1, '[{\"id\":null,\"customerId\":3,\"opportunityId\":3,\"quoteId\":1,\"contractName\":\"E2E-Contract-201846\",\"contractType\":1,\"contractAmount\":16000.00,\"signDate\":\"2026-10-30\",\"startDate\":\"2026-10-30\",\"endDate\":\"2027-10-30\",\"signUserId\":null,\"ourSignUserId\":null,\"ownerId\":17,\"fileUrl\":null,\"remark\":\"e2e contract\"}]', 1, '127.0.0.1', '2026-10-30 20:18:47', 19);
INSERT INTO `sys_oper_log` VALUES (54, 5, 'sale', '合同管理', '提交审批', '合同', 1, '[1]', 1, '127.0.0.1', '2026-10-30 20:18:47', 12);
INSERT INTO `sys_oper_log` VALUES (55, 5, 'sale_m', '合同管理', '审批通过', '合同', 1, '[1]', 1, '127.0.0.1', '2026-10-30 20:18:47', 9);
INSERT INTO `sys_oper_log` VALUES (56, 5, 'finance', '发票管理', '新增', '发票', 1, '[{\"id\":null,\"contractId\":1,\"invoiceType\":2,\"invoiceAmount\":16000.00,\"invoiceDate\":\"2026-10-30\",\"remark\":\"e2e invoice\",\"fileUrl\":null}]', 1, '127.0.0.1', '2026-10-30 20:18:47', 6);
INSERT INTO `sys_oper_log` VALUES (57, 5, 'sale', '合同管理', '审批通过', '合同', NULL, '[1]', 0, '127.0.0.1', '2026-10-30 20:18:47', 0);
INSERT INTO `sys_oper_log` VALUES (58, 5, 'sale', '合同管理', '审批通过', '合同', NULL, '[1]', 0, '127.0.0.1', '2026-10-30 20:19:09', 0);
INSERT INTO `sys_oper_log` VALUES (59, 5, 'sale', '线索管理', '新增', '线索', 4, '[{\"id\":null,\"customerId\":null,\"contactId\":null,\"leadName\":\"E2E-Lead-202009\",\"source\":\"fair\",\"status\":null,\"phone\":\"13900001234\",\"email\":null,\"company\":\"E2E-Co-202009\",\"description\":\"e2e lead\",\"ownerId\":17}]', 1, '127.0.0.1', '2026-10-30 20:20:09', 17);
INSERT INTO `sys_oper_log` VALUES (60, 5, 'sale', '线索管理', '转商机', '线索', 4, '[{\"leadId\":4,\"customerId\":null,\"oppName\":\"E2E-Opp-202009\",\"expectedAmount\":16000,\"expectedDate\":\"2026-11-30\"}]', 1, '127.0.0.1', '2026-10-30 20:20:09', 42);
INSERT INTO `sys_oper_log` VALUES (61, 5, 'sale', '客户管理', '跟进', '跟进记录', 3, '[{\"id\":null,\"customerId\":4,\"followType\":\"phone\",\"followContent\":\"E2E follow-up call\",\"nextFollowTime\":null}]', 1, '127.0.0.1', '2026-10-30 20:20:10', 12);
INSERT INTO `sys_oper_log` VALUES (62, 5, 'sale', '商机管理', '阶段推进', '商机', 4, '[4,14]', 1, '127.0.0.1', '2026-10-30 20:20:10', 21);
INSERT INTO `sys_oper_log` VALUES (63, 5, 'sale', '商机管理', '阶段推进', '商机', 4, '[4,15]', 1, '127.0.0.1', '2026-10-30 20:20:10', 15);
INSERT INTO `sys_oper_log` VALUES (64, 5, 'sale', '商机管理', '阶段推进', '商机', 4, '[4,16]', 1, '127.0.0.1', '2026-10-30 20:20:10', 16);
INSERT INTO `sys_oper_log` VALUES (65, 5, 'sale', '商机管理', '阶段推进', '商机', 4, '[4,17]', 1, '127.0.0.1', '2026-10-30 20:20:10', 15);
INSERT INTO `sys_oper_log` VALUES (66, 5, 'sale', '报价管理', '新增', '报价单', 2, '[{\"id\":null,\"opportunityId\":4,\"customerId\":4,\"quoteName\":\"E2E-Quote-202009\",\"totalAmount\":16000.00,\"discountRate\":0,\"finalAmount\":16000.00,\"expireDate\":\"2026-11-30\",\"remark\":null,\"ownerId\":17,\"products\":[{\"id\":null,\"productId\":2,\"productName\":\"åå¡ç¬è®°æ¬çµèE2E\",\"unit\":\"å°\",\"quantity\":2,\"unitPrice\":8000.00,\"discountRate\":0,\"totalPrice\":16000.00,\"remark\":null}]}]', 1, '127.0.0.1', '2026-10-30 20:20:10', 15);
INSERT INTO `sys_oper_log` VALUES (67, 5, 'sale', '报价管理', '发送', '报价单', 2, '[2]', 1, '127.0.0.1', '2026-10-30 20:20:10', 12);
INSERT INTO `sys_oper_log` VALUES (68, 5, 'sale', '报价管理', '中标', '报价单', 2, '[2]', 1, '127.0.0.1', '2026-10-30 20:20:10', 16);
INSERT INTO `sys_oper_log` VALUES (69, 5, 'sale', '合同管理', '新增', '合同', 2, '[{\"id\":null,\"customerId\":4,\"opportunityId\":4,\"quoteId\":2,\"contractName\":\"E2E-Contract-202009\",\"contractType\":1,\"contractAmount\":16000.00,\"signDate\":\"2026-10-30\",\"startDate\":\"2026-10-30\",\"endDate\":\"2027-10-30\",\"signUserId\":null,\"ourSignUserId\":null,\"ownerId\":17,\"fileUrl\":null,\"remark\":\"e2e contract\"}]', 1, '127.0.0.1', '2026-10-30 20:20:10', 19);
INSERT INTO `sys_oper_log` VALUES (70, 5, 'sale', '合同管理', '提交审批', '合同', 2, '[2]', 1, '127.0.0.1', '2026-10-30 20:20:10', 12);
INSERT INTO `sys_oper_log` VALUES (71, 5, 'sale_m', '合同管理', '审批通过', '合同', 2, '[2]', 1, '127.0.0.1', '2026-10-30 20:20:10', 10);
INSERT INTO `sys_oper_log` VALUES (72, 5, 'finance', '发票管理', '新增', '发票', 2, '[{\"id\":null,\"contractId\":2,\"invoiceType\":2,\"invoiceAmount\":16000.00,\"invoiceDate\":\"2026-10-30\",\"remark\":\"e2e invoice\",\"fileUrl\":null}]', 1, '127.0.0.1', '2026-10-30 20:20:10', 10);
INSERT INTO `sys_oper_log` VALUES (73, 5, 'sale', '合同管理', '审批通过', '合同', NULL, '[2]', 0, '127.0.0.1', '2026-10-30 20:20:10', 1);
INSERT INTO `sys_oper_log` VALUES (74, 5, 'sale', '线索管理', '新增', '线索', 5, '[{\"id\":null,\"customerId\":null,\"contactId\":null,\"leadName\":\"E2E-Lead-203101\",\"source\":\"fair\",\"status\":null,\"phone\":\"13900001234\",\"email\":null,\"company\":\"E2E-Co-203101\",\"description\":\"e2e lead\",\"ownerId\":17}]', 1, '127.0.0.1', '2026-10-30 20:31:01', 50);
INSERT INTO `sys_oper_log` VALUES (75, 5, 'sale', '线索管理', '转商机', '线索', 5, '[{\"leadId\":5,\"customerId\":null,\"oppName\":\"E2E-Opp-203101\",\"expectedAmount\":16000,\"expectedDate\":\"2026-11-30\"}]', 1, '127.0.0.1', '2026-10-30 20:31:01', 41);
INSERT INTO `sys_oper_log` VALUES (76, 5, 'sale', '客户管理', '跟进', '跟进记录', 4, '[{\"id\":null,\"customerId\":5,\"followType\":\"phone\",\"followContent\":\"E2E follow-up call\",\"nextFollowTime\":null}]', 1, '127.0.0.1', '2026-10-30 20:31:02', 14);
INSERT INTO `sys_oper_log` VALUES (77, 5, 'sale', '商机管理', '阶段推进', '商机', 5, '[5,14]', 1, '127.0.0.1', '2026-10-30 20:31:02', 16);
INSERT INTO `sys_oper_log` VALUES (78, 5, 'sale', '商机管理', '阶段推进', '商机', 5, '[5,15]', 1, '127.0.0.1', '2026-10-30 20:31:02', 16);
INSERT INTO `sys_oper_log` VALUES (79, 5, 'sale', '商机管理', '阶段推进', '商机', 5, '[5,16]', 1, '127.0.0.1', '2026-10-30 20:31:02', 16);
INSERT INTO `sys_oper_log` VALUES (80, 5, 'sale', '商机管理', '阶段推进', '商机', 5, '[5,17]', 1, '127.0.0.1', '2026-10-30 20:31:02', 18);
INSERT INTO `sys_oper_log` VALUES (81, 5, 'sale', '报价管理', '新增', '报价单', 3, '[{\"id\":null,\"opportunityId\":5,\"customerId\":5,\"quoteName\":\"E2E-Quote-203101\",\"totalAmount\":16000.00,\"discountRate\":0,\"finalAmount\":16000.00,\"expireDate\":\"2026-11-30\",\"remark\":null,\"ownerId\":17,\"products\":[{\"id\":null,\"productId\":2,\"productName\":\"åå¡ç¬è®°æ¬çµèE2E\",\"unit\":\"å°\",\"quantity\":2,\"unitPrice\":8000.00,\"discountRate\":0,\"totalPrice\":16000.00,\"remark\":null}]}]', 1, '127.0.0.1', '2026-10-30 20:31:02', 13);
INSERT INTO `sys_oper_log` VALUES (82, 5, 'sale', '报价管理', '发送', '报价单', 3, '[3]', 1, '127.0.0.1', '2026-10-30 20:31:02', 13);
INSERT INTO `sys_oper_log` VALUES (83, 5, 'sale', '报价管理', '中标', '报价单', 3, '[3]', 1, '127.0.0.1', '2026-10-30 20:31:02', 28);
INSERT INTO `sys_oper_log` VALUES (84, 5, 'sale', '合同管理', '新增', '合同', 3, '[{\"id\":null,\"customerId\":5,\"opportunityId\":5,\"quoteId\":3,\"contractName\":\"E2E-Contract-203101\",\"contractType\":1,\"contractAmount\":16000.00,\"signDate\":\"2026-10-30\",\"startDate\":\"2026-10-30\",\"endDate\":\"2027-10-30\",\"signUserId\":null,\"ourSignUserId\":null,\"ownerId\":17,\"fileUrl\":null,\"remark\":\"e2e contract\"}]', 1, '127.0.0.1', '2026-10-30 20:31:02', 23);
INSERT INTO `sys_oper_log` VALUES (85, 5, 'sale', '合同管理', '提交审批', '合同', 3, '[3]', 1, '127.0.0.1', '2026-10-30 20:31:02', 12);
INSERT INTO `sys_oper_log` VALUES (86, 5, 'sale_m', '合同管理', '审批通过', '合同', 3, '[3]', 1, '127.0.0.1', '2026-10-30 20:31:02', 10);
INSERT INTO `sys_oper_log` VALUES (87, 5, 'finance', '发票管理', '新增', '发票', 3, '[{\"id\":null,\"contractId\":3,\"invoiceType\":2,\"invoiceAmount\":16000.00,\"invoiceDate\":\"2026-10-30\",\"remark\":\"e2e invoice\",\"fileUrl\":null}]', 1, '127.0.0.1', '2026-10-30 20:31:02', 9);
INSERT INTO `sys_oper_log` VALUES (88, 5, 'sale', '合同管理', '审批通过', '合同', NULL, '[3]', 0, '127.0.0.1', '2026-10-30 20:31:02', 1);
INSERT INTO `sys_oper_log` VALUES (89, 5, 'sale', '线索管理', '新增', '线索', 1, '[{\"id\":null,\"customerId\":null,\"contactId\":null,\"leadName\":\"E2E-Lead-204945\",\"source\":\"fair\",\"status\":null,\"phone\":\"13900001234\",\"email\":null,\"company\":\"E2E-Co-204945\",\"description\":\"e2e\",\"ownerId\":17}]', 1, '127.0.0.1', '2026-10-30 20:49:46', 29);
INSERT INTO `sys_oper_log` VALUES (90, 5, 'sale', '线索管理', '转商机', '线索', 1, '[{\"leadId\":1,\"customerId\":null,\"oppName\":\"E2E-Opp-204945\",\"expectedAmount\":16000,\"expectedDate\":\"2026-11-30\"}]', 1, '127.0.0.1', '2026-10-30 20:49:46', 42);
INSERT INTO `sys_oper_log` VALUES (91, 5, 'sale', '客户管理', '跟进', '跟进记录', 1, '[{\"id\":null,\"customerId\":1,\"followType\":\"phone\",\"followContent\":\"E2E follow\",\"nextFollowTime\":null}]', 1, '127.0.0.1', '2026-10-30 20:49:46', 13);
INSERT INTO `sys_oper_log` VALUES (92, 5, 'sale', '商机管理', '阶段推进', '商机', 1, '[1,14]', 1, '127.0.0.1', '2026-10-30 20:49:46', 17);
INSERT INTO `sys_oper_log` VALUES (93, 5, 'sale', '商机管理', '阶段推进', '商机', 1, '[1,15]', 1, '127.0.0.1', '2026-10-30 20:49:46', 18);
INSERT INTO `sys_oper_log` VALUES (94, 5, 'sale', '商机管理', '阶段推进', '商机', 1, '[1,16]', 1, '127.0.0.1', '2026-10-30 20:49:46', 15);
INSERT INTO `sys_oper_log` VALUES (95, 5, 'sale', '商机管理', '阶段推进', '商机', 1, '[1,17]', 1, '127.0.0.1', '2026-10-30 20:49:46', 16);
INSERT INTO `sys_oper_log` VALUES (96, 5, 'sale', '报价管理', '新增', '报价单', 1, '[{\"id\":null,\"opportunityId\":1,\"customerId\":1,\"quoteName\":\"E2E-Quote-204945\",\"totalAmount\":16000.00,\"discountRate\":0,\"finalAmount\":16000.00,\"expireDate\":\"2026-11-30\",\"remark\":null,\"ownerId\":17,\"products\":[{\"id\":null,\"productId\":2,\"productName\":\"åå¡ç¬è®°æ¬çµèE2E\",\"unit\":\"pcs\",\"quantity\":2,\"unitPrice\":8000.00,\"discountRate\":0,\"totalPrice\":16000.00,\"remark\":null}]}]', 1, '127.0.0.1', '2026-10-30 20:49:46', 16);
INSERT INTO `sys_oper_log` VALUES (97, 5, 'sale', '报价管理', '发送', '报价单', 1, '[1]', 1, '127.0.0.1', '2026-10-30 20:49:46', 12);
INSERT INTO `sys_oper_log` VALUES (98, 5, 'sale', '报价管理', '中标', '报价单', 1, '[1]', 1, '127.0.0.1', '2026-10-30 20:49:46', 17);
INSERT INTO `sys_oper_log` VALUES (99, 5, 'sale', '合同管理', '新增', '合同', 1, '[{\"id\":null,\"customerId\":1,\"opportunityId\":1,\"quoteId\":1,\"contractName\":\"E2E-Contract-204945\",\"contractType\":1,\"contractAmount\":16000.00,\"signDate\":\"2026-10-30\",\"startDate\":\"2026-10-30\",\"endDate\":\"2027-10-30\",\"signUserId\":null,\"ourSignUserId\":null,\"ownerId\":17,\"fileUrl\":null,\"remark\":\"e2e\"}]', 1, '127.0.0.1', '2026-10-30 20:49:46', 19);
INSERT INTO `sys_oper_log` VALUES (100, 5, 'sale', '合同管理', '提交审批', '合同', 1, '[1]', 1, '127.0.0.1', '2026-10-30 20:49:46', 12);
INSERT INTO `sys_oper_log` VALUES (101, 5, 'sale_m', '合同管理', '审批通过', '合同', 1, '[1]', 1, '127.0.0.1', '2026-10-30 20:49:46', 10);
INSERT INTO `sys_oper_log` VALUES (102, 5, 'finance', '发票管理', '新增', '发票', 1, '[{\"id\":null,\"contractId\":1,\"invoiceType\":2,\"invoiceAmount\":16000.00,\"invoiceDate\":\"2026-10-30\",\"remark\":\"e2e invoice\",\"fileUrl\":null}]', 1, '127.0.0.1', '2026-10-30 20:49:47', 9);
INSERT INTO `sys_oper_log` VALUES (103, 5, 'sale', '线索管理', '新增', '线索', 1, '[{\"id\":null,\"customerId\":null,\"contactId\":null,\"leadName\":\"张三要买电脑\",\"source\":\"\",\"status\":1,\"phone\":\"\",\"email\":\"\",\"company\":\"\",\"description\":\"\",\"ownerId\":0}]', 1, '127.0.0.1', '2026-10-30 21:47:58', 29);
INSERT INTO `sys_oper_log` VALUES (104, 5, 'ptgly', '产品管理', '新增分类', '分类', 3, '[{\"id\":null,\"parentId\":0,\"categoryName\":\"计算机\",\"sort\":0,\"status\":1}]', 1, '127.0.0.1', '2026-10-30 21:48:40', 17);
INSERT INTO `sys_oper_log` VALUES (105, 5, 'ptgly', '产品管理', '新增分类', '分类', 4, '[{\"id\":null,\"parentId\":3,\"categoryName\":\"笔记本\",\"sort\":0,\"status\":1}]', 1, '127.0.0.1', '2026-10-30 21:48:51', 11);
INSERT INTO `sys_oper_log` VALUES (106, 5, 'sale', '线索管理', '新增', '线索', 2, '[{\"id\":null,\"customerId\":null,\"contactId\":null,\"leadName\":\"张三要买电脑\",\"source\":\"\",\"status\":1,\"phone\":\"\",\"email\":\"\",\"company\":\"\",\"description\":\"\",\"ownerId\":17}]', 1, '127.0.0.1', '2026-10-30 22:41:50', 19);
INSERT INTO `sys_oper_log` VALUES (107, 5, 'sale', '客户管理', '新增', '客户', 1, '[{\"id\":null,\"customerType\":2,\"customerName\":\"张三\",\"industry\":null,\"companySize\":null,\"province\":null,\"city\":null,\"address\":null,\"website\":null,\"gender\":null,\"birthday\":null,\"occupation\":null,\"phone\":null,\"email\":null,\"source\":\"推荐\",\"level\":1,\"status\":1,\"remark\":\"\",\"tagIds\":[]}]', 1, '127.0.0.1', '2026-10-30 22:42:06', 21);
INSERT INTO `sys_oper_log` VALUES (108, 5, 'sale', '线索管理', '转商机', '线索', 2, '[{\"leadId\":2,\"customerId\":1,\"oppName\":\"张三要买电脑-商机\",\"expectedAmount\":10000,\"expectedDate\":\"2026-10-30\"}]', 1, '127.0.0.1', '2026-10-30 22:42:20', 31);
INSERT INTO `sys_oper_log` VALUES (109, 5, 'sale', '商机管理', '阶段推进', '商机', 1, '[1,14]', 1, '127.0.0.1', '2026-10-30 22:42:36', 24);
INSERT INTO `sys_oper_log` VALUES (110, 5, 'sale', '商机管理', '阶段推进', '商机', 1, '[1,15]', 1, '127.0.0.1', '2026-10-30 22:42:43', 26);
INSERT INTO `sys_oper_log` VALUES (111, 5, 'ptgly', '产品管理', '新增', '产品', 3, '[{\"id\":null,\"categoryId\":4,\"productName\":\"ROG 枪神 16\",\"productCode\":\"\",\"unit\":\"台\",\"spec\":\"\",\"model\":\"\",\"price\":10000,\"minPrice\":null,\"costPrice\":null,\"description\":\"\",\"imageUrl\":\"\",\"status\":1,\"sort\":0}]', 1, '127.0.0.1', '2026-10-30 22:53:35', 14);
INSERT INTO `sys_oper_log` VALUES (112, 5, 'sale', '报价管理', '新增', '报价单', 1, '[{\"id\":null,\"opportunityId\":1,\"customerId\":1,\"quoteName\":\"张三要买电脑-商机-报价单\",\"totalAmount\":10000,\"discountRate\":0,\"finalAmount\":10000,\"expireDate\":\"2026-10-30\",\"remark\":\"\",\"ownerId\":17,\"products\":[{\"id\":null,\"productId\":3,\"productName\":\"ROG 枪神 16\",\"unit\":\"台\",\"quantity\":1,\"unitPrice\":10000,\"discountRate\":0,\"totalPrice\":10000,\"remark\":\"\"}]}]', 1, '127.0.0.1', '2026-10-30 22:58:50', 53);
INSERT INTO `sys_oper_log` VALUES (113, 5, 'sale', '报价管理', '发送', '报价单', 1, '[1]', 1, '127.0.0.1', '2026-10-30 22:58:59', 18);
INSERT INTO `sys_oper_log` VALUES (114, 5, 'sale', '商机管理', '阶段推进', '商机', 1, '[1,16]', 1, '127.0.0.1', '2026-10-30 22:59:27', 24);
INSERT INTO `sys_oper_log` VALUES (115, 5, 'sale', '报价管理', '中标', '报价单', 1, '[1]', 1, '127.0.0.1', '2026-10-30 23:00:06', 35);
INSERT INTO `sys_oper_log` VALUES (116, 5, 'sale', '合同管理', '新增', '合同', 1, '[{\"id\":null,\"customerId\":1,\"opportunityId\":1,\"quoteId\":1,\"contractName\":\"张三-合同\",\"contractType\":null,\"contractAmount\":10000,\"signDate\":\"2026-10-30\",\"startDate\":\"2026-10-30\",\"endDate\":\"2026-10-30\",\"signUserId\":null,\"ourSignUserId\":null,\"ownerId\":17,\"fileUrl\":null,\"remark\":\"\"}]', 1, '127.0.0.1', '2026-10-30 23:06:12', 19);
INSERT INTO `sys_oper_log` VALUES (117, 5, 'sale', '合同管理', '提交审批', '合同', 1, '[1]', 1, '127.0.0.1', '2026-10-30 23:06:16', 17);
INSERT INTO `sys_oper_log` VALUES (118, 5, 'sale_m', '合同管理', '审批通过', '合同', 1, '[1]', 1, '127.0.0.1', '2026-10-30 23:07:27', 16);
INSERT INTO `sys_oper_log` VALUES (119, 5, 'ptgly', '角色管理', '分配权限', '角色', 11, '[12,[176,102,116,121,147,155,156,157,159,160,161,162,163,164,165,166,167,168,169,170,172,177,175,180,100,101,115,120,146,154,171]]', 1, '127.0.0.1', '2026-10-30 23:17:35', 75);
INSERT INTO `sys_oper_log` VALUES (120, 5, 'ptgly', '角色管理', '分配权限', '角色', 11, '[12,[176,102,116,121,147,155,156,157,159,160,161,162,163,164,165,166,167,168,169,170,175,180,100,101,115,120,146,154,171]]', 1, '127.0.0.1', '2026-10-30 23:26:13', 49);
INSERT INTO `sys_oper_log` VALUES (121, 5, 'ptgly', '角色管理', '分配权限', '角色', 11, '[12,[176,102,116,121,147,155,156,157,159,160,161,162,163,164,165,166,167,168,169,170,172,177,175,180,100,101,115,120,146,154,171]]', 1, '127.0.0.1', '2026-10-30 23:27:33', 55);
INSERT INTO `sys_oper_log` VALUES (122, 5, 'finance', '发票管理', '新增', '发票', 1, '[{\"id\":null,\"contractId\":1,\"invoiceType\":1,\"invoiceAmount\":10000,\"invoiceDate\":\"2026-10-30\",\"remark\":\"\",\"fileUrl\":\"\"}]', 1, '127.0.0.1', '2026-10-30 23:37:51', 46);
INSERT INTO `sys_oper_log` VALUES (123, 5, 'ptgly', '客户管理', '修改', '客户', 1, '[{\"id\":1,\"customerType\":1,\"customerName\":\"张三\",\"industry\":\"IT\",\"companySize\":\"1-50人\",\"province\":\"湖南\",\"city\":\"长沙\",\"address\":null,\"website\":null,\"gender\":null,\"birthday\":null,\"occupation\":null,\"phone\":null,\"email\":null,\"source\":\"推荐\",\"level\":1,\"status\":1,\"remark\":\"\",\"tagIds\":[]}]', 1, '127.0.0.1', '2026-10-31 08:54:04', 44);
INSERT INTO `sys_oper_log` VALUES (124, 5, 'ptgly', '线索管理', '删除', '线索', 1, '[1]', 1, '127.0.0.1', '2026-10-31 09:45:10', 14);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT 1 COMMENT '租户ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `data_scope` tinyint NULL DEFAULT 1 COMMENT '数据范围：1全部 2租户 3部门 4部门及下级 5本人 6自定义',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code_tenant`(`role_code` ASC, `tenant_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 1, '超级管理员', 'PLATFORM_ADMIN', 1, 1, '系统超级管理员，拥有所有权限', '2026-04-18 22:38:02', '2026-04-28 11:57:11', 1, 1);
INSERT INTO `sys_role` VALUES (11, 5, '平台管理员', 'admin', 2, 1, '平台管理员：本租户用户/角色/部门/字典/日志管理 + CRM全业务', '2026-10-30 18:50:23', '2026-10-30 19:00:02', 1, 1);
INSERT INTO `sys_role` VALUES (12, 5, '财务', 'finance', 2, 1, '财务：合同查看、发票/收款管理', '2026-10-30 18:50:48', '2026-10-30 18:58:07', 1, 1);
INSERT INTO `sys_role` VALUES (13, 5, '财务主管', 'finance_m', 2, 1, '财务主管：财务全权限，合同审批/终止', '2026-10-30 18:51:04', '2026-10-30 18:58:07', 1, 1);
INSERT INTO `sys_role` VALUES (14, 5, '销售', 'sale', 5, 1, '销售：客户/线索/商机/报价/合同提报，仅本人数据', '2026-10-30 18:51:20', '2026-10-30 18:58:07', 1, 1);
INSERT INTO `sys_role` VALUES (15, 5, '销售主管', 'sale_m', 4, 1, '销售主管：销售全流程+客户转移/合同审批，部门及下级数据', '2026-10-30 18:51:33', '2026-10-30 18:58:07', 1, 1);

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE,
  INDEX `idx_dept_id`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色部门关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE,
  INDEX `idx_menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1);
INSERT INTO `sys_role_menu` VALUES (1, 2);
INSERT INTO `sys_role_menu` VALUES (11, 2);
INSERT INTO `sys_role_menu` VALUES (1, 3);
INSERT INTO `sys_role_menu` VALUES (11, 3);
INSERT INTO `sys_role_menu` VALUES (1, 4);
INSERT INTO `sys_role_menu` VALUES (11, 4);
INSERT INTO `sys_role_menu` VALUES (1, 5);
INSERT INTO `sys_role_menu` VALUES (11, 5);
INSERT INTO `sys_role_menu` VALUES (1, 6);
INSERT INTO `sys_role_menu` VALUES (11, 6);
INSERT INTO `sys_role_menu` VALUES (1, 7);
INSERT INTO `sys_role_menu` VALUES (11, 7);
INSERT INTO `sys_role_menu` VALUES (1, 8);
INSERT INTO `sys_role_menu` VALUES (11, 8);
INSERT INTO `sys_role_menu` VALUES (1, 9);
INSERT INTO `sys_role_menu` VALUES (11, 9);
INSERT INTO `sys_role_menu` VALUES (1, 10);
INSERT INTO `sys_role_menu` VALUES (11, 10);
INSERT INTO `sys_role_menu` VALUES (1, 11);
INSERT INTO `sys_role_menu` VALUES (11, 11);
INSERT INTO `sys_role_menu` VALUES (1, 12);
INSERT INTO `sys_role_menu` VALUES (11, 12);
INSERT INTO `sys_role_menu` VALUES (1, 13);
INSERT INTO `sys_role_menu` VALUES (11, 13);
INSERT INTO `sys_role_menu` VALUES (1, 14);
INSERT INTO `sys_role_menu` VALUES (1, 15);
INSERT INTO `sys_role_menu` VALUES (1, 16);
INSERT INTO `sys_role_menu` VALUES (1, 17);
INSERT INTO `sys_role_menu` VALUES (1, 18);
INSERT INTO `sys_role_menu` VALUES (1, 19);
INSERT INTO `sys_role_menu` VALUES (11, 19);
INSERT INTO `sys_role_menu` VALUES (1, 20);
INSERT INTO `sys_role_menu` VALUES (11, 20);
INSERT INTO `sys_role_menu` VALUES (1, 21);
INSERT INTO `sys_role_menu` VALUES (11, 21);
INSERT INTO `sys_role_menu` VALUES (1, 22);
INSERT INTO `sys_role_menu` VALUES (11, 22);
INSERT INTO `sys_role_menu` VALUES (1, 23);
INSERT INTO `sys_role_menu` VALUES (11, 23);
INSERT INTO `sys_role_menu` VALUES (1, 24);
INSERT INTO `sys_role_menu` VALUES (1, 25);
INSERT INTO `sys_role_menu` VALUES (1, 26);
INSERT INTO `sys_role_menu` VALUES (1, 27);
INSERT INTO `sys_role_menu` VALUES (1, 28);
INSERT INTO `sys_role_menu` VALUES (1, 29);
INSERT INTO `sys_role_menu` VALUES (1, 35);
INSERT INTO `sys_role_menu` VALUES (11, 35);
INSERT INTO `sys_role_menu` VALUES (1, 36);
INSERT INTO `sys_role_menu` VALUES (11, 36);
INSERT INTO `sys_role_menu` VALUES (1, 37);
INSERT INTO `sys_role_menu` VALUES (11, 37);
INSERT INTO `sys_role_menu` VALUES (1, 40);
INSERT INTO `sys_role_menu` VALUES (11, 40);
INSERT INTO `sys_role_menu` VALUES (1, 41);
INSERT INTO `sys_role_menu` VALUES (11, 41);
INSERT INTO `sys_role_menu` VALUES (1, 43);
INSERT INTO `sys_role_menu` VALUES (11, 43);
INSERT INTO `sys_role_menu` VALUES (1, 44);
INSERT INTO `sys_role_menu` VALUES (11, 44);
INSERT INTO `sys_role_menu` VALUES (1, 45);
INSERT INTO `sys_role_menu` VALUES (11, 45);
INSERT INTO `sys_role_menu` VALUES (1, 46);
INSERT INTO `sys_role_menu` VALUES (11, 46);
INSERT INTO `sys_role_menu` VALUES (1, 47);
INSERT INTO `sys_role_menu` VALUES (11, 47);
INSERT INTO `sys_role_menu` VALUES (1, 53);
INSERT INTO `sys_role_menu` VALUES (11, 53);
INSERT INTO `sys_role_menu` VALUES (1, 54);
INSERT INTO `sys_role_menu` VALUES (11, 54);
INSERT INTO `sys_role_menu` VALUES (1, 55);
INSERT INTO `sys_role_menu` VALUES (11, 55);
INSERT INTO `sys_role_menu` VALUES (1, 56);
INSERT INTO `sys_role_menu` VALUES (11, 56);
INSERT INTO `sys_role_menu` VALUES (1, 57);
INSERT INTO `sys_role_menu` VALUES (11, 57);
INSERT INTO `sys_role_menu` VALUES (1, 100);
INSERT INTO `sys_role_menu` VALUES (11, 100);
INSERT INTO `sys_role_menu` VALUES (12, 100);
INSERT INTO `sys_role_menu` VALUES (13, 100);
INSERT INTO `sys_role_menu` VALUES (14, 100);
INSERT INTO `sys_role_menu` VALUES (15, 100);
INSERT INTO `sys_role_menu` VALUES (1, 101);
INSERT INTO `sys_role_menu` VALUES (11, 101);
INSERT INTO `sys_role_menu` VALUES (12, 101);
INSERT INTO `sys_role_menu` VALUES (13, 101);
INSERT INTO `sys_role_menu` VALUES (14, 101);
INSERT INTO `sys_role_menu` VALUES (15, 101);
INSERT INTO `sys_role_menu` VALUES (1, 102);
INSERT INTO `sys_role_menu` VALUES (11, 102);
INSERT INTO `sys_role_menu` VALUES (12, 102);
INSERT INTO `sys_role_menu` VALUES (13, 102);
INSERT INTO `sys_role_menu` VALUES (14, 102);
INSERT INTO `sys_role_menu` VALUES (15, 102);
INSERT INTO `sys_role_menu` VALUES (1, 103);
INSERT INTO `sys_role_menu` VALUES (11, 103);
INSERT INTO `sys_role_menu` VALUES (14, 103);
INSERT INTO `sys_role_menu` VALUES (15, 103);
INSERT INTO `sys_role_menu` VALUES (1, 104);
INSERT INTO `sys_role_menu` VALUES (11, 104);
INSERT INTO `sys_role_menu` VALUES (14, 104);
INSERT INTO `sys_role_menu` VALUES (15, 104);
INSERT INTO `sys_role_menu` VALUES (1, 105);
INSERT INTO `sys_role_menu` VALUES (11, 105);
INSERT INTO `sys_role_menu` VALUES (15, 105);
INSERT INTO `sys_role_menu` VALUES (1, 106);
INSERT INTO `sys_role_menu` VALUES (11, 106);
INSERT INTO `sys_role_menu` VALUES (14, 106);
INSERT INTO `sys_role_menu` VALUES (15, 106);
INSERT INTO `sys_role_menu` VALUES (1, 107);
INSERT INTO `sys_role_menu` VALUES (11, 107);
INSERT INTO `sys_role_menu` VALUES (14, 107);
INSERT INTO `sys_role_menu` VALUES (15, 107);
INSERT INTO `sys_role_menu` VALUES (1, 108);
INSERT INTO `sys_role_menu` VALUES (11, 108);
INSERT INTO `sys_role_menu` VALUES (15, 108);
INSERT INTO `sys_role_menu` VALUES (1, 109);
INSERT INTO `sys_role_menu` VALUES (11, 109);
INSERT INTO `sys_role_menu` VALUES (14, 109);
INSERT INTO `sys_role_menu` VALUES (15, 109);
INSERT INTO `sys_role_menu` VALUES (1, 110);
INSERT INTO `sys_role_menu` VALUES (11, 110);
INSERT INTO `sys_role_menu` VALUES (14, 110);
INSERT INTO `sys_role_menu` VALUES (15, 110);
INSERT INTO `sys_role_menu` VALUES (1, 111);
INSERT INTO `sys_role_menu` VALUES (11, 111);
INSERT INTO `sys_role_menu` VALUES (14, 111);
INSERT INTO `sys_role_menu` VALUES (15, 111);
INSERT INTO `sys_role_menu` VALUES (1, 112);
INSERT INTO `sys_role_menu` VALUES (11, 112);
INSERT INTO `sys_role_menu` VALUES (15, 112);
INSERT INTO `sys_role_menu` VALUES (1, 113);
INSERT INTO `sys_role_menu` VALUES (11, 113);
INSERT INTO `sys_role_menu` VALUES (15, 113);
INSERT INTO `sys_role_menu` VALUES (1, 114);
INSERT INTO `sys_role_menu` VALUES (11, 114);
INSERT INTO `sys_role_menu` VALUES (15, 114);
INSERT INTO `sys_role_menu` VALUES (1, 115);
INSERT INTO `sys_role_menu` VALUES (11, 115);
INSERT INTO `sys_role_menu` VALUES (12, 115);
INSERT INTO `sys_role_menu` VALUES (13, 115);
INSERT INTO `sys_role_menu` VALUES (14, 115);
INSERT INTO `sys_role_menu` VALUES (15, 115);
INSERT INTO `sys_role_menu` VALUES (1, 116);
INSERT INTO `sys_role_menu` VALUES (11, 116);
INSERT INTO `sys_role_menu` VALUES (12, 116);
INSERT INTO `sys_role_menu` VALUES (13, 116);
INSERT INTO `sys_role_menu` VALUES (14, 116);
INSERT INTO `sys_role_menu` VALUES (15, 116);
INSERT INTO `sys_role_menu` VALUES (1, 117);
INSERT INTO `sys_role_menu` VALUES (11, 117);
INSERT INTO `sys_role_menu` VALUES (15, 117);
INSERT INTO `sys_role_menu` VALUES (1, 118);
INSERT INTO `sys_role_menu` VALUES (11, 118);
INSERT INTO `sys_role_menu` VALUES (15, 118);
INSERT INTO `sys_role_menu` VALUES (1, 119);
INSERT INTO `sys_role_menu` VALUES (11, 119);
INSERT INTO `sys_role_menu` VALUES (15, 119);
INSERT INTO `sys_role_menu` VALUES (1, 120);
INSERT INTO `sys_role_menu` VALUES (11, 120);
INSERT INTO `sys_role_menu` VALUES (12, 120);
INSERT INTO `sys_role_menu` VALUES (13, 120);
INSERT INTO `sys_role_menu` VALUES (14, 120);
INSERT INTO `sys_role_menu` VALUES (15, 120);
INSERT INTO `sys_role_menu` VALUES (1, 121);
INSERT INTO `sys_role_menu` VALUES (11, 121);
INSERT INTO `sys_role_menu` VALUES (12, 121);
INSERT INTO `sys_role_menu` VALUES (13, 121);
INSERT INTO `sys_role_menu` VALUES (14, 121);
INSERT INTO `sys_role_menu` VALUES (15, 121);
INSERT INTO `sys_role_menu` VALUES (1, 122);
INSERT INTO `sys_role_menu` VALUES (11, 122);
INSERT INTO `sys_role_menu` VALUES (15, 122);
INSERT INTO `sys_role_menu` VALUES (1, 123);
INSERT INTO `sys_role_menu` VALUES (11, 123);
INSERT INTO `sys_role_menu` VALUES (15, 123);
INSERT INTO `sys_role_menu` VALUES (1, 124);
INSERT INTO `sys_role_menu` VALUES (11, 124);
INSERT INTO `sys_role_menu` VALUES (15, 124);
INSERT INTO `sys_role_menu` VALUES (1, 125);
INSERT INTO `sys_role_menu` VALUES (11, 125);
INSERT INTO `sys_role_menu` VALUES (14, 125);
INSERT INTO `sys_role_menu` VALUES (15, 125);
INSERT INTO `sys_role_menu` VALUES (1, 126);
INSERT INTO `sys_role_menu` VALUES (11, 126);
INSERT INTO `sys_role_menu` VALUES (14, 126);
INSERT INTO `sys_role_menu` VALUES (15, 126);
INSERT INTO `sys_role_menu` VALUES (1, 127);
INSERT INTO `sys_role_menu` VALUES (11, 127);
INSERT INTO `sys_role_menu` VALUES (14, 127);
INSERT INTO `sys_role_menu` VALUES (15, 127);
INSERT INTO `sys_role_menu` VALUES (1, 128);
INSERT INTO `sys_role_menu` VALUES (11, 128);
INSERT INTO `sys_role_menu` VALUES (14, 128);
INSERT INTO `sys_role_menu` VALUES (15, 128);
INSERT INTO `sys_role_menu` VALUES (1, 129);
INSERT INTO `sys_role_menu` VALUES (11, 129);
INSERT INTO `sys_role_menu` VALUES (14, 129);
INSERT INTO `sys_role_menu` VALUES (15, 129);
INSERT INTO `sys_role_menu` VALUES (1, 130);
INSERT INTO `sys_role_menu` VALUES (11, 130);
INSERT INTO `sys_role_menu` VALUES (14, 130);
INSERT INTO `sys_role_menu` VALUES (15, 130);
INSERT INTO `sys_role_menu` VALUES (1, 131);
INSERT INTO `sys_role_menu` VALUES (11, 131);
INSERT INTO `sys_role_menu` VALUES (14, 131);
INSERT INTO `sys_role_menu` VALUES (15, 131);
INSERT INTO `sys_role_menu` VALUES (1, 132);
INSERT INTO `sys_role_menu` VALUES (11, 132);
INSERT INTO `sys_role_menu` VALUES (14, 132);
INSERT INTO `sys_role_menu` VALUES (15, 132);
INSERT INTO `sys_role_menu` VALUES (1, 133);
INSERT INTO `sys_role_menu` VALUES (11, 133);
INSERT INTO `sys_role_menu` VALUES (14, 133);
INSERT INTO `sys_role_menu` VALUES (15, 133);
INSERT INTO `sys_role_menu` VALUES (1, 134);
INSERT INTO `sys_role_menu` VALUES (11, 134);
INSERT INTO `sys_role_menu` VALUES (14, 134);
INSERT INTO `sys_role_menu` VALUES (15, 134);
INSERT INTO `sys_role_menu` VALUES (1, 135);
INSERT INTO `sys_role_menu` VALUES (11, 135);
INSERT INTO `sys_role_menu` VALUES (14, 135);
INSERT INTO `sys_role_menu` VALUES (15, 135);
INSERT INTO `sys_role_menu` VALUES (1, 136);
INSERT INTO `sys_role_menu` VALUES (11, 136);
INSERT INTO `sys_role_menu` VALUES (14, 136);
INSERT INTO `sys_role_menu` VALUES (15, 136);
INSERT INTO `sys_role_menu` VALUES (1, 137);
INSERT INTO `sys_role_menu` VALUES (11, 137);
INSERT INTO `sys_role_menu` VALUES (14, 137);
INSERT INTO `sys_role_menu` VALUES (15, 137);
INSERT INTO `sys_role_menu` VALUES (1, 138);
INSERT INTO `sys_role_menu` VALUES (11, 138);
INSERT INTO `sys_role_menu` VALUES (14, 138);
INSERT INTO `sys_role_menu` VALUES (15, 138);
INSERT INTO `sys_role_menu` VALUES (1, 139);
INSERT INTO `sys_role_menu` VALUES (11, 139);
INSERT INTO `sys_role_menu` VALUES (14, 139);
INSERT INTO `sys_role_menu` VALUES (15, 139);
INSERT INTO `sys_role_menu` VALUES (1, 140);
INSERT INTO `sys_role_menu` VALUES (11, 140);
INSERT INTO `sys_role_menu` VALUES (14, 140);
INSERT INTO `sys_role_menu` VALUES (15, 140);
INSERT INTO `sys_role_menu` VALUES (1, 141);
INSERT INTO `sys_role_menu` VALUES (11, 141);
INSERT INTO `sys_role_menu` VALUES (14, 141);
INSERT INTO `sys_role_menu` VALUES (15, 141);
INSERT INTO `sys_role_menu` VALUES (1, 142);
INSERT INTO `sys_role_menu` VALUES (11, 142);
INSERT INTO `sys_role_menu` VALUES (14, 142);
INSERT INTO `sys_role_menu` VALUES (15, 142);
INSERT INTO `sys_role_menu` VALUES (1, 143);
INSERT INTO `sys_role_menu` VALUES (11, 143);
INSERT INTO `sys_role_menu` VALUES (14, 143);
INSERT INTO `sys_role_menu` VALUES (15, 143);
INSERT INTO `sys_role_menu` VALUES (1, 144);
INSERT INTO `sys_role_menu` VALUES (11, 144);
INSERT INTO `sys_role_menu` VALUES (14, 144);
INSERT INTO `sys_role_menu` VALUES (15, 144);
INSERT INTO `sys_role_menu` VALUES (1, 145);
INSERT INTO `sys_role_menu` VALUES (11, 145);
INSERT INTO `sys_role_menu` VALUES (14, 145);
INSERT INTO `sys_role_menu` VALUES (15, 145);
INSERT INTO `sys_role_menu` VALUES (1, 146);
INSERT INTO `sys_role_menu` VALUES (11, 146);
INSERT INTO `sys_role_menu` VALUES (12, 146);
INSERT INTO `sys_role_menu` VALUES (13, 146);
INSERT INTO `sys_role_menu` VALUES (14, 146);
INSERT INTO `sys_role_menu` VALUES (15, 146);
INSERT INTO `sys_role_menu` VALUES (1, 147);
INSERT INTO `sys_role_menu` VALUES (11, 147);
INSERT INTO `sys_role_menu` VALUES (12, 147);
INSERT INTO `sys_role_menu` VALUES (13, 147);
INSERT INTO `sys_role_menu` VALUES (14, 147);
INSERT INTO `sys_role_menu` VALUES (15, 147);
INSERT INTO `sys_role_menu` VALUES (1, 148);
INSERT INTO `sys_role_menu` VALUES (11, 148);
INSERT INTO `sys_role_menu` VALUES (13, 148);
INSERT INTO `sys_role_menu` VALUES (14, 148);
INSERT INTO `sys_role_menu` VALUES (15, 148);
INSERT INTO `sys_role_menu` VALUES (1, 149);
INSERT INTO `sys_role_menu` VALUES (11, 149);
INSERT INTO `sys_role_menu` VALUES (13, 149);
INSERT INTO `sys_role_menu` VALUES (14, 149);
INSERT INTO `sys_role_menu` VALUES (15, 149);
INSERT INTO `sys_role_menu` VALUES (1, 150);
INSERT INTO `sys_role_menu` VALUES (11, 150);
INSERT INTO `sys_role_menu` VALUES (15, 150);
INSERT INTO `sys_role_menu` VALUES (1, 151);
INSERT INTO `sys_role_menu` VALUES (11, 151);
INSERT INTO `sys_role_menu` VALUES (14, 151);
INSERT INTO `sys_role_menu` VALUES (15, 151);
INSERT INTO `sys_role_menu` VALUES (1, 152);
INSERT INTO `sys_role_menu` VALUES (11, 152);
INSERT INTO `sys_role_menu` VALUES (13, 152);
INSERT INTO `sys_role_menu` VALUES (15, 152);
INSERT INTO `sys_role_menu` VALUES (1, 153);
INSERT INTO `sys_role_menu` VALUES (11, 153);
INSERT INTO `sys_role_menu` VALUES (13, 153);
INSERT INTO `sys_role_menu` VALUES (15, 153);
INSERT INTO `sys_role_menu` VALUES (1, 154);
INSERT INTO `sys_role_menu` VALUES (11, 154);
INSERT INTO `sys_role_menu` VALUES (12, 154);
INSERT INTO `sys_role_menu` VALUES (13, 154);
INSERT INTO `sys_role_menu` VALUES (14, 154);
INSERT INTO `sys_role_menu` VALUES (15, 154);
INSERT INTO `sys_role_menu` VALUES (1, 155);
INSERT INTO `sys_role_menu` VALUES (11, 155);
INSERT INTO `sys_role_menu` VALUES (12, 155);
INSERT INTO `sys_role_menu` VALUES (13, 155);
INSERT INTO `sys_role_menu` VALUES (14, 155);
INSERT INTO `sys_role_menu` VALUES (15, 155);
INSERT INTO `sys_role_menu` VALUES (1, 156);
INSERT INTO `sys_role_menu` VALUES (11, 156);
INSERT INTO `sys_role_menu` VALUES (12, 156);
INSERT INTO `sys_role_menu` VALUES (13, 156);
INSERT INTO `sys_role_menu` VALUES (15, 156);
INSERT INTO `sys_role_menu` VALUES (1, 157);
INSERT INTO `sys_role_menu` VALUES (11, 157);
INSERT INTO `sys_role_menu` VALUES (12, 157);
INSERT INTO `sys_role_menu` VALUES (13, 157);
INSERT INTO `sys_role_menu` VALUES (1, 158);
INSERT INTO `sys_role_menu` VALUES (11, 158);
INSERT INTO `sys_role_menu` VALUES (13, 158);
INSERT INTO `sys_role_menu` VALUES (1, 159);
INSERT INTO `sys_role_menu` VALUES (11, 159);
INSERT INTO `sys_role_menu` VALUES (12, 159);
INSERT INTO `sys_role_menu` VALUES (13, 159);
INSERT INTO `sys_role_menu` VALUES (1, 160);
INSERT INTO `sys_role_menu` VALUES (11, 160);
INSERT INTO `sys_role_menu` VALUES (12, 160);
INSERT INTO `sys_role_menu` VALUES (13, 160);
INSERT INTO `sys_role_menu` VALUES (14, 160);
INSERT INTO `sys_role_menu` VALUES (15, 160);
INSERT INTO `sys_role_menu` VALUES (1, 161);
INSERT INTO `sys_role_menu` VALUES (11, 161);
INSERT INTO `sys_role_menu` VALUES (12, 161);
INSERT INTO `sys_role_menu` VALUES (13, 161);
INSERT INTO `sys_role_menu` VALUES (14, 161);
INSERT INTO `sys_role_menu` VALUES (15, 161);
INSERT INTO `sys_role_menu` VALUES (1, 162);
INSERT INTO `sys_role_menu` VALUES (11, 162);
INSERT INTO `sys_role_menu` VALUES (12, 162);
INSERT INTO `sys_role_menu` VALUES (13, 162);
INSERT INTO `sys_role_menu` VALUES (1, 163);
INSERT INTO `sys_role_menu` VALUES (11, 163);
INSERT INTO `sys_role_menu` VALUES (12, 163);
INSERT INTO `sys_role_menu` VALUES (13, 163);
INSERT INTO `sys_role_menu` VALUES (1, 164);
INSERT INTO `sys_role_menu` VALUES (11, 164);
INSERT INTO `sys_role_menu` VALUES (12, 164);
INSERT INTO `sys_role_menu` VALUES (13, 164);
INSERT INTO `sys_role_menu` VALUES (1, 165);
INSERT INTO `sys_role_menu` VALUES (11, 165);
INSERT INTO `sys_role_menu` VALUES (12, 165);
INSERT INTO `sys_role_menu` VALUES (13, 165);
INSERT INTO `sys_role_menu` VALUES (14, 165);
INSERT INTO `sys_role_menu` VALUES (15, 165);
INSERT INTO `sys_role_menu` VALUES (1, 166);
INSERT INTO `sys_role_menu` VALUES (11, 166);
INSERT INTO `sys_role_menu` VALUES (12, 166);
INSERT INTO `sys_role_menu` VALUES (13, 166);
INSERT INTO `sys_role_menu` VALUES (14, 166);
INSERT INTO `sys_role_menu` VALUES (15, 166);
INSERT INTO `sys_role_menu` VALUES (1, 167);
INSERT INTO `sys_role_menu` VALUES (11, 167);
INSERT INTO `sys_role_menu` VALUES (12, 167);
INSERT INTO `sys_role_menu` VALUES (13, 167);
INSERT INTO `sys_role_menu` VALUES (1, 168);
INSERT INTO `sys_role_menu` VALUES (11, 168);
INSERT INTO `sys_role_menu` VALUES (12, 168);
INSERT INTO `sys_role_menu` VALUES (13, 168);
INSERT INTO `sys_role_menu` VALUES (1, 169);
INSERT INTO `sys_role_menu` VALUES (11, 169);
INSERT INTO `sys_role_menu` VALUES (12, 169);
INSERT INTO `sys_role_menu` VALUES (13, 169);
INSERT INTO `sys_role_menu` VALUES (1, 170);
INSERT INTO `sys_role_menu` VALUES (11, 170);
INSERT INTO `sys_role_menu` VALUES (12, 170);
INSERT INTO `sys_role_menu` VALUES (13, 170);
INSERT INTO `sys_role_menu` VALUES (1, 171);
INSERT INTO `sys_role_menu` VALUES (11, 171);
INSERT INTO `sys_role_menu` VALUES (12, 171);
INSERT INTO `sys_role_menu` VALUES (13, 171);
INSERT INTO `sys_role_menu` VALUES (14, 171);
INSERT INTO `sys_role_menu` VALUES (15, 171);
INSERT INTO `sys_role_menu` VALUES (1, 172);
INSERT INTO `sys_role_menu` VALUES (11, 172);
INSERT INTO `sys_role_menu` VALUES (12, 172);
INSERT INTO `sys_role_menu` VALUES (13, 172);
INSERT INTO `sys_role_menu` VALUES (14, 172);
INSERT INTO `sys_role_menu` VALUES (15, 172);
INSERT INTO `sys_role_menu` VALUES (1, 173);
INSERT INTO `sys_role_menu` VALUES (11, 173);
INSERT INTO `sys_role_menu` VALUES (13, 173);
INSERT INTO `sys_role_menu` VALUES (14, 173);
INSERT INTO `sys_role_menu` VALUES (15, 173);
INSERT INTO `sys_role_menu` VALUES (1, 174);
INSERT INTO `sys_role_menu` VALUES (11, 174);
INSERT INTO `sys_role_menu` VALUES (13, 174);
INSERT INTO `sys_role_menu` VALUES (14, 174);
INSERT INTO `sys_role_menu` VALUES (15, 174);
INSERT INTO `sys_role_menu` VALUES (1, 175);
INSERT INTO `sys_role_menu` VALUES (11, 175);
INSERT INTO `sys_role_menu` VALUES (12, 175);
INSERT INTO `sys_role_menu` VALUES (13, 175);
INSERT INTO `sys_role_menu` VALUES (14, 175);
INSERT INTO `sys_role_menu` VALUES (15, 175);
INSERT INTO `sys_role_menu` VALUES (1, 176);
INSERT INTO `sys_role_menu` VALUES (11, 176);
INSERT INTO `sys_role_menu` VALUES (12, 176);
INSERT INTO `sys_role_menu` VALUES (13, 176);
INSERT INTO `sys_role_menu` VALUES (14, 176);
INSERT INTO `sys_role_menu` VALUES (15, 176);
INSERT INTO `sys_role_menu` VALUES (1, 177);
INSERT INTO `sys_role_menu` VALUES (11, 177);
INSERT INTO `sys_role_menu` VALUES (12, 177);
INSERT INTO `sys_role_menu` VALUES (13, 177);
INSERT INTO `sys_role_menu` VALUES (14, 177);
INSERT INTO `sys_role_menu` VALUES (15, 177);
INSERT INTO `sys_role_menu` VALUES (1, 178);
INSERT INTO `sys_role_menu` VALUES (11, 178);
INSERT INTO `sys_role_menu` VALUES (13, 178);
INSERT INTO `sys_role_menu` VALUES (14, 178);
INSERT INTO `sys_role_menu` VALUES (15, 178);
INSERT INTO `sys_role_menu` VALUES (1, 179);
INSERT INTO `sys_role_menu` VALUES (11, 179);
INSERT INTO `sys_role_menu` VALUES (13, 179);
INSERT INTO `sys_role_menu` VALUES (14, 179);
INSERT INTO `sys_role_menu` VALUES (15, 179);
INSERT INTO `sys_role_menu` VALUES (1, 180);
INSERT INTO `sys_role_menu` VALUES (11, 180);
INSERT INTO `sys_role_menu` VALUES (12, 180);
INSERT INTO `sys_role_menu` VALUES (13, 180);
INSERT INTO `sys_role_menu` VALUES (14, 180);
INSERT INTO `sys_role_menu` VALUES (15, 180);

-- ----------------------------
-- Table structure for sys_tenant
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '租户名称',
  `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '租户编码',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_code`(`tenant_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '租户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_tenant
-- ----------------------------
INSERT INTO `sys_tenant` VALUES (1, '平台', 'PLATFORM', 1, '2099-12-31 23:59:59', '系统管理员', '13800138000', '2026-04-18 22:38:01', '2026-04-28 12:28:36');
INSERT INTO `sys_tenant` VALUES (5, '金石小区', 'JINSHI', 1, NULL, '', '', '2026-10-30 18:48:26', '2026-10-30 18:48:26');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT 1 COMMENT '租户ID',
  `dept_id` bigint NULL DEFAULT 0 COMMENT '部门ID',
  `user_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'BCrypt加密密码',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别：0未知 1男 2女',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_code`(`user_code` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_dept_id`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 1, 1, 'admin', '$2a$10$OIwNI0FL4T2QcK2j8RoMJ.pJ/5ODsKQ/bAvoD75aui.5mCUGmK.hO', '超级管理员', 'waynemail@vip.qq.com', '13888888888', NULL, 1, 1, '2026-10-31 12:25:51', '2026-04-18 22:38:01', '2026-10-31 12:25:51', 0, NULL);
INSERT INTO `sys_user` VALUES (16, 5, 27, 'ptgly', '$2a$10$OIwNI0FL4T2QcK2j8RoMJ.pJ/5ODsKQ/bAvoD75aui.5mCUGmK.hO', '平台管理员', '', '', NULL, 0, 1, '2026-10-31 11:31:36', '2026-10-30 18:49:29', '2026-10-31 11:31:36', 1, NULL);
INSERT INTO `sys_user` VALUES (17, 5, 26, 'sale', '$2a$10$OIwNI0FL4T2QcK2j8RoMJ.pJ/5ODsKQ/bAvoD75aui.5mCUGmK.hO', '销售', '', '', NULL, 0, 1, '2026-10-31 12:51:53', '2026-10-30 18:52:08', '2026-10-31 12:51:53', 1, NULL);
INSERT INTO `sys_user` VALUES (18, 5, 26, 'sale_m', '$2a$10$OIwNI0FL4T2QcK2j8RoMJ.pJ/5ODsKQ/bAvoD75aui.5mCUGmK.hO', '销售主管', '', '', NULL, 0, 1, '2026-10-31 11:31:44', '2026-10-30 18:52:32', '2026-10-31 11:31:44', 1, NULL);
INSERT INTO `sys_user` VALUES (19, 5, 25, 'finance', '$2a$10$OIwNI0FL4T2QcK2j8RoMJ.pJ/5ODsKQ/bAvoD75aui.5mCUGmK.hO', '财务', '', '', NULL, 0, 1, '2026-10-31 11:25:00', '2026-10-30 18:52:49', '2026-10-31 11:25:00', 1, NULL);
INSERT INTO `sys_user` VALUES (20, 5, 25, 'finance_m', '$2a$10$OIwNI0FL4T2QcK2j8RoMJ.pJ/5ODsKQ/bAvoD75aui.5mCUGmK.hO', '财务主管', '', '', NULL, 0, 1, '2026-10-31 11:21:33', '2026-10-30 18:53:08', '2026-10-31 11:21:33', 1, NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (16, 11);
INSERT INTO `sys_user_role` VALUES (19, 12);
INSERT INTO `sys_user_role` VALUES (20, 13);
INSERT INTO `sys_user_role` VALUES (17, 14);
INSERT INTO `sys_user_role` VALUES (18, 15);

SET FOREIGN_KEY_CHECKS = 1;
