package com.platform.server.crm.payment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "收款计划信息响应")
public class PaymentPlanVO {

    @Schema(description = "计划ID", example = "1")
    private Long id;

    @Schema(description = "关联合同ID", example = "1")
    private Long contractId;

    @Schema(description = "合同编号", example = "CT2026001")
    private String contractNo;

    @Schema(description = "计划编号", example = "PP2026001")
    private String planNo;

    @Schema(description = "计划收款金额", example = "10000.00")
    private BigDecimal planAmount;

    @Schema(description = "计划收款日期", example = "2026-05-30")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate planDate;

    @Schema(description = "已收款金额", example = "5000.00")
    private BigDecimal receivedAmount;

    @Schema(description = "收款状态：0未收 1部分 2已收", example = "1")
    private Integer receivedStatus;

    @Schema(description = "收款状态名称", example = "部分收款")
    private String receivedStatusName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间", example = "2026-04-30 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2026-04-30 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}