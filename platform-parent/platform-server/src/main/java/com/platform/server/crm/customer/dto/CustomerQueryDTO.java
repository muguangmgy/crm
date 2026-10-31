package com.platform.server.crm.customer.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户查询条件")
public class CustomerQueryDTO extends PageQuery {

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "客户类型：1企业 2个人")
    private Integer customerType;

    @Schema(description = "池类型：1私有池 2公海池")
    private Integer poolType;

    @Schema(description = "客户等级：1普通 2重要 3VIP")
    private Integer level;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "行业")
    private String industry;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "客户来源")
    private String source;

    @Schema(description = "负责人ID")
    private Long ownerId;

    @Schema(description = "部门ID")
    private Long deptId;
}