package com.platform.server.crm.payment.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "收款记录查询条件")
public class PaymentRecordQueryDTO extends PageQuery {

    @Schema(description = "关联合同ID")
    private Long contractId;

    @Schema(description = "关联收款计划ID")
    private Long planId;

    @Schema(description = "收款编号")
    private String paymentNo;

    @Schema(description = "状态：1已确认 2待确认")
    private Integer status;

    @Schema(description = "收款日期开始")
    private LocalDate paymentDateStart;

    @Schema(description = "收款日期结束")
    private LocalDate paymentDateEnd;

    @Schema(description = "收款方式")
    private String paymentType;
}