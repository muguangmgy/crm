package com.platform.server.crm.quote.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "报价单查询条件")
public class QuoteQueryDTO extends PageQuery {

    @Schema(description = "报价单名称")
    private String quoteName;

    @Schema(description = "报价单编号")
    private String quoteNo;

    @Schema(description = "关联商机ID")
    private Long opportunityId;

    @Schema(description = "关联客户ID")
    private Long customerId;

    @Schema(description = "状态：1草稿 2已发送 3已接受 4已拒绝")
    private Integer status;

    @Schema(description = "负责人ID")
    private Long ownerId;
}