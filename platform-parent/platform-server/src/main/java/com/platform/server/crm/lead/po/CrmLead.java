package com.platform.server.crm.lead.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 销售线索实体
 */
@Data
@TableName("crm_lead")
public class CrmLead {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 关联客户（可空） */
    private Long customerId;

    /** 关联联系人 */
    private Long contactId;

    /** 线索名称 */
    private String leadName;

    /** 线索来源 */
    private String source;

    /** 状态：1新建 2跟进中 3已转商机 4已失效 */
    private Integer status;

    /** 电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 公司名称 */
    private String company;

    /** 线索描述 */
    private String description;

    /** 负责人 */
    private Long ownerId;

    /** 所属部门ID */
    private Long deptId;

    /** 转商机时间 */
    private LocalDateTime convertedTime;

    /** 转换后的商机ID */
    private Long convertedOppId;

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