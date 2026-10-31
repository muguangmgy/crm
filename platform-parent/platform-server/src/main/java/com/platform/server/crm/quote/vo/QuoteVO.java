package com.platform.server.crm.quote.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "报价单信息响应")
public class QuoteVO {

    @Schema(description = "报价单ID")
    private Long id;

    @Schema(description = "报价单编号")
    private String quoteNo;

    @Schema(description = "报价单名称")
    private String quoteName;

    @Schema(description = "关联商机ID")
    private Long opportunityId;

    @Schema(description = "商机名称")
    private String opportunityName;

    @Schema(description = "关联客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "报价总金额")
    private BigDecimal totalAmount;

    @Schema(description = "整体折扣率（%）")
    private BigDecimal discountRate;

    @Schema(description = "最终金额")
    private BigDecimal finalAmount;

    @Schema(description = "状态：1草稿 2已发送 3已接受 4已拒绝")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "报价有效期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @Schema(description = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    @Schema(description = "接受时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acceptTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "负责人ID")
    private Long ownerId;

    @Schema(description = "负责人名称")
    private String ownerName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "产品明细列表")
    private List<QuoteProductVO> products;
}