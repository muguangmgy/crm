package com.platform.server.crm.invoice.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "发票查询条件")
public class InvoiceQueryDTO extends PageQuery {

    @Schema(description = "发票编号")
    private String invoiceNo;

    @Schema(description = "关联合同ID")
    private Long contractId;

    @Schema(description = "发票类型：1普通发票 2增值税专用发票")
    private Integer invoiceType;

    @Schema(description = "发票状态：1已开票 2已作废")
    private Integer invoiceStatus;
}