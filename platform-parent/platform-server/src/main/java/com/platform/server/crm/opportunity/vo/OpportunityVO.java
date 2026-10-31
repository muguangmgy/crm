package com.platform.server.crm.opportunity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "商机信息响应")
public class OpportunityVO {

    @Schema(description = "商机ID")
    private Long id;

    @Schema(description = "商机编号")
    private String oppNo;

    @Schema(description = "商机名称")
    private String oppName;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "联系人ID")
    private Long contactId;

    @Schema(description = "联系人名称")
    private String contactName;

    @Schema(description = "阶段ID")
    private Long stageId;

    @Schema(description = "阶段名称")
    private String stageName;

    @Schema(description = "成交概率（0-100）")
    private Integer probability;

    @Schema(description = "预期金额")
    private BigDecimal expectedAmount;

    @Schema(description = "实际成交金额")
    private BigDecimal actualAmount;

    @Schema(description = "预期成交日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedDate;

    @Schema(description = "实际成交日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate closeDate;

    @Schema(description = "商机来源")
    private String source;

    @Schema(description = "状态：1进行中 2已成交 3已失败")
    private Integer status;

    @Schema(description = "负责人ID")
    private Long ownerId;

    @Schema(description = "负责人名称")
    private String ownerName;

    @Schema(description = "中标报价单ID")
    private Long winnerQuoteId;

    @Schema(description = "中标报价单编号")
    private String winnerQuoteNo;

    @Schema(description = "中标报价单名称")
    private String winnerQuoteName;

    @Schema(description = "商机描述")
    private String description;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}