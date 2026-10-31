package com.platform.server.crm.contract.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "合同信息响应")
public class ContractVO {

    @Schema(description = "合同ID")
    private Long id;

    @Schema(description = "合同编号")
    private String contractNo;

    @Schema(description = "合同名称")
    private String contractName;

    @Schema(description = "合同类型：1销售合同 2采购合同")
    private Integer contractType;

    @Schema(description = "合同类型名称")
    private String contractTypeName;

    @Schema(description = "关联客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "关联商机ID")
    private Long opportunityId;

    @Schema(description = "商机名称")
    private String opportunityName;

    @Schema(description = "关联报价单ID")
    private Long quoteId;

    @Schema(description = "报价单名称")
    private String quoteName;

    @Schema(description = "合同金额")
    private BigDecimal contractAmount;

    @Schema(description = "已收款金额")
    private BigDecimal receivedAmount;

    @Schema(description = "已开票金额")
    private BigDecimal invoicedAmount;

    @Schema(description = "收款进度（%）")
    private BigDecimal receivedProgress;

    @Schema(description = "开票进度（%）")
    private BigDecimal invoicedProgress;

    @Schema(description = "状态：1草稿 2审批中 3生效 4到期 5终止")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "签订日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate signDate;

    @Schema(description = "合同开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "合同结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "客户方签约人ID")
    private Long signUserId;

    @Schema(description = "客户方签约人名称")
    private String signUserName;

    @Schema(description = "我方签约人ID")
    private Long ourSignUserId;

    @Schema(description = "我方签约人名称")
    private String ourSignUserName;

    @Schema(description = "负责人ID")
    private Long ownerId;

    @Schema(description = "负责人名称")
    private String ownerName;

    @Schema(description = "合同文件URL")
    private String fileUrl;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}