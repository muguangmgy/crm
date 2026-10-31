package com.platform.server.crm.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "客户分布分析")
public class CustomerDistributionVO {

    @Schema(description = "分布维度（行业/地区/等级）")
    private String dimension;

    @Schema(description = "维度值（如行业：IT、地区：北京）")
    private String value;

    @Schema(description = "客户数量")
    private Integer customerCount;

    @Schema(description = "占比")
    private BigDecimal percentage;
}