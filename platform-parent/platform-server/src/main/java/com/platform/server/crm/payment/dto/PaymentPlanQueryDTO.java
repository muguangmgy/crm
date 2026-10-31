package com.platform.server.crm.payment.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "收款计划查询条件")
public class PaymentPlanQueryDTO extends PageQuery {

    @Schema(description = "关联合同ID")
    private Long contractId;

    @Schema(description = "计划编号")
    private String planNo;

    @Schema(description = "收款状态：0未收 1部分 2已收")
    private Integer receivedStatus;

    @Schema(description = "计划日期开始")
    private LocalDate planDateStart;

    @Schema(description = "计划日期结束")
    private LocalDate planDateEnd;
}