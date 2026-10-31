package com.platform.server.crm.customer.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户实体
 */
@Data
@TableName("crm_customer")
public class CrmCustomer {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 部门ID */
    private Long deptId;

    /** 客户类型：1企业 2个人 */
    private Integer customerType;

    /** 客户名称（企业名/个人姓名） */
    private String customerName;

    /** 客户编号（自动生成） */
    private String customerNo;

    /** 行业 */
    private String industry;

    /** 企业规模 */
    private String companySize;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 详细地址 */
    private String address;

    /** 网站 */
    private String website;

    /** 性别：0未知 1男 2女 */
    private Integer gender;

    /** 生日 */
    private LocalDate birthday;

    /** 职业 */
    private String occupation;

    /** 电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 客户来源 */
    private String source;

    /** 客户等级：1普通 2重要 3VIP */
    private Integer level;

    /** 状态：0禁用 1启用 */
    private Integer status;

    /** 池类型：1私有池 2公海池 */
    private Integer poolType;

    /** 负责人ID（私有池时必填） */
    private Long ownerId;

    /** 锁定时间（公海领取后） */
    private LocalDateTime lockTime;

    /** 下次跟进时间 */
    private LocalDateTime nextFollowTime;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 更新人 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
}