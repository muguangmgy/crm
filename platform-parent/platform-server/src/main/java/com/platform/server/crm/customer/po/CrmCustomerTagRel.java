package com.platform.server.crm.customer.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 客户标签关联实体
 */
@Data
@TableName("crm_customer_tag_rel")
public class CrmCustomerTagRel {

    /** 客户ID */
    private Long customerId;

    /** 标签ID */
    private Long tagId;
}