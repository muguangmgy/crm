package com.platform.server.crm.customer.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "客户联系人响应")
public class ContactVO {

    @Schema(description = "联系人ID")
    private Long id;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "性别：0未知 1男 2女")
    private Integer gender;

    @Schema(description = "职位")
    private String position;

    @Schema(description = "部门")
    private String department;

    @Schema(description = "是否主要联系人：0否 1是")
    private Integer isPrimary;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "微信")
    private String wechat;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}