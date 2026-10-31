package com.platform.server.crm.contract.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同实体
 */
@Data
@TableName("crm_contract")
public class CrmContract {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 关联客户 */
    private Long customerId;

    /** 关联商机 */
    private Long opportunityId;

    /** 关联报价单 */
    private Long quoteId;

    /** 合同编号（自动生成） */
    private String contractNo;

    /** 合同名称 */
    private String contractName;

    /** 合同类型：1销售合同 2采购合同 */
    private Integer contractType;

    /** 合同金额 */
    private BigDecimal contractAmount;

    /** 已收款金额 */
    private BigDecimal receivedAmount;

    /** 已开票金额 */
    private BigDecimal invoicedAmount;

    /** 签订日期 */
    private LocalDate signDate;

    /** 合同开始日期 */
    private LocalDate startDate;

    /** 合同结束日期 */
    private LocalDate endDate;

    /** 客户方签约人 */
    private Long signUserId;

    /** 我方签约人 */
    private Long ourSignUserId;

    /** 状态：1草稿 2审批中 3生效 4到期 5终止 */
    private Integer status;

    /** 负责人 */
    private Long ownerId;

    /** 所属部门ID */
    private Long deptId;

    /** 合同文件URL */
    private String fileUrl;

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