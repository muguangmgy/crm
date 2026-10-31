package com.platform.server.crm.lead.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "线索信息响应")
public class LeadVO {

    @Schema(description = "线索ID")
    private Long id;

    @Schema(description = "关联客户ID")
    private Long customerId;

    @Schema(description = "关联客户名称")
    private String customerName;

    @Schema(description = "关联联系人ID")
    private Long contactId;

    @Schema(description = "关联联系人姓名")
    private String contactName;

    @Schema(description = "线索名称")
    private String leadName;

    @Schema(description = "线索来源")
    private String source;

    @Schema(description = "状态：1新建 2跟进中 3已转商机 4已失效")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "公司名称")
    private String company;

    @Schema(description = "线索描述")
    private String description;

    @Schema(description = "负责人ID")
    private Long ownerId;

    @Schema(description = "负责人姓名")
    private String ownerName;

    @Schema(description = "转商机时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime convertedTime;

    @Schema(description = "转换后的商机ID")
    private Long convertedOppId;

    @Schema(description = "转换后的商机名称")
    private String convertedOppName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}