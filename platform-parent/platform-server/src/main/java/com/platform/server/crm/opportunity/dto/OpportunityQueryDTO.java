package com.platform.server.crm.opportunity.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商机查询条件")
public class OpportunityQueryDTO extends PageQuery {

    @Schema(description = "商机名称")
    private String oppName;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "阶段ID")
    private Long stageId;

    @Schema(description = "状态：1进行中 2已成交 3已失败")
    private Integer status;

    @Schema(description = "负责人ID")
    private Long ownerId;

    @Schema(description = "商机来源")
    private String source;
}