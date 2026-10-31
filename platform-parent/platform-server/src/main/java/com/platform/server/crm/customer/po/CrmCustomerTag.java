package com.platform.server.crm.customer.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户标签实体
 */
@Data
@TableName("crm_customer_tag")
public class CrmCustomerTag {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 标签名称 */
    private String tagName;

    /** 标签颜色 */
    private String tagColor;

    /** 排序 */
    private Integer sort;

    /** 状态：0禁用 1启用 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}