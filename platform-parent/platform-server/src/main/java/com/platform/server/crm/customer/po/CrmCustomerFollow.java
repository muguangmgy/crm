package com.platform.server.crm.customer.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户跟进记录实体
 */
@Data
@TableName("crm_customer_follow")
public class CrmCustomerFollow {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 客户ID */
    private Long customerId;

    /** 跟进方式（电话、邮件、拜访等） */
    private String followType;

    /** 跟进内容 */
    private String followContent;

    /** 下次跟进时间 */
    private LocalDateTime nextFollowTime;

    /** 跟进人ID */
    private Long ownerId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}