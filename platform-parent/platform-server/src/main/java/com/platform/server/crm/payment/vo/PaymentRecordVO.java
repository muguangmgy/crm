package com.platform.server.crm.payment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "收款记录信息响应")
public class PaymentRecordVO {

    @Schema(description = "记录ID", example = "1")
    private Long id;

    @Schema(description = "关联合同ID", example = "1")
    private Long contractId;

    @Schema(description = "合同编号", example = "CT2026001")
    private String contractNo;

    @Schema(description = "关联收款计划ID", example = "1")
    private Long planId;

    @Schema(description = "计划编号", example = "PP2026001")
    private String planNo;

    @Schema(description = "收款编号", example = "PR2026001")
    private String paymentNo;

    @Schema(description = "实际收款金额", example = "5000.00")
    private BigDecimal paymentAmount;

    @Schema(description = "实际收款日期", example = "2026-05-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;

    @Schema(description = "收款方式", example = "bank_transfer")
    private String paymentType;

    @Schema(description = "收款方式名称", example = "银行转账")
    private String paymentTypeName;

    @Schema(description = "付款人名称", example = "张三")
    private String payerName;

    @Schema(description = "付款账户", example = "6222****1234")
    private String payerAccount;

    @Schema(description = "收款账户", example = "公司主账户")
    private String ourAccount;

    @Schema(description = "状态：1已确认 2待确认", example = "2")
    private Integer status;

    @Schema(description = "状态名称", example = "待确认")
    private String statusName;

    @Schema(description = "凭证附件URL")
    private String voucherUrl;

    @Schema(description = "确认时间", example = "2026-05-01 14:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmTime;

    @Schema(description = "确认人ID", example = "1")
    private Long confirmBy;

    @Schema(description = "确认人名称", example = "admin")
    private String confirmByName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间", example = "2026-04-30 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2026-04-30 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}