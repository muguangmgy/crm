package com.platform.server.crm.contract.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "合同查询条件")
public class ContractQueryDTO extends PageQuery {

    @Schema(description = "合同名称")
    private String contractName;

    @Schema(description = "合同编号")
    private String contractNo;

    @Schema(description = "关联客户ID")
    private Long customerId;

    @Schema(description = "关联商机ID")
    private Long opportunityId;

    @Schema(description = "合同类型：1销售合同 2采购合同")
    private Integer contractType;

    @Schema(description = "状态：1草稿 2审批中 3生效 4到期 5终止")
    private Integer status;

    @Schema(description = "负责人ID")
    private Long ownerId;

    @Schema(description = "签订日期开始")
    private String signDateStart;

    @Schema(description = "签订日期结束")
    private String signDateEnd;
}