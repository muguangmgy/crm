package com.platform.server.crm.customer.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户联系人实体
 */
@Data
@TableName("crm_customer_contact")
public class CrmCustomerContact {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 客户ID */
    private Long customerId;

    /** 联系人姓名 */
    private String contactName;

    /** 性别：0未知 1男 2女 */
    private Integer gender;

    /** 职位 */
    private String position;

    /** 部门 */
    private String department;

    /** 是否主要联系人：0否 1是 */
    private Integer isPrimary;

    /** 电话 */
    private String phone;

    /** 手机 */
    private String mobile;

    /** 邮箱 */
    private String email;

    /** 微信 */
    private String wechat;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}