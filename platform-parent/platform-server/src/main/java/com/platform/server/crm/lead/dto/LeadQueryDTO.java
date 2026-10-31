package com.platform.server.crm.lead.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "线索查询条件")
public class LeadQueryDTO extends PageQuery {

    @Schema(description = "线索名称")
    private String leadName;

    @Schema(description = "线索来源")
    private String source;

    @Schema(description = "状态：1新建 2跟进中 3已转商机 4已失效")
    private Integer status;

    @Schema(description = "负责人ID")
    private Long ownerId;

    @Schema(description = "公司名称")
    private String company;

    @Schema(description = "电话")
    private String phone;
}