package com.platform.server.crm.invoice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "发票信息响应")
public class InvoiceVO {

    @Schema(description = "发票ID")
    private Long id;

    @Schema(description = "发票编号")
    private String invoiceNo;

    @Schema(description = "关联合同ID")
    private Long contractId;

    @Schema(description = "合同名称")
    private String contractName;

    @Schema(description = "合同编号")
    private String contractNo;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "负责人ID")
    private Long ownerId;

    @Schema(description = "负责人名称")
    private String ownerName;

    @Schema(description = "发票类型：1普通发票 2增值税专用发票")
    private Integer invoiceType;

    @Schema(description = "发票类型名称")
    private String invoiceTypeName;

    @Schema(description = "发票金额")
    private BigDecimal invoiceAmount;

    @Schema(description = "开票日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;

    @Schema(description = "发票状态：1已开票 2已作废")
    private Integer invoiceStatus;

    @Schema(description = "发票状态名称")
    private String invoiceStatusName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "发票文件URL")
    private String fileUrl;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
