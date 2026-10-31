package com.platform.server.crm.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "客户联系人保存请求")
public class ContactSaveDTO {

    @Schema(description = "联系人ID（修改时必填）")
    private Long id;

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @Schema(description = "联系人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名最多50字符")
    private String contactName;

    @Schema(description = "性别：0未知 1男 2女")
    private Integer gender;

    @Schema(description = "职位")
    @Size(max = 50, message = "职位最多50字符")
    private String position;

    @Schema(description = "部门")
    @Size(max = 50, message = "部门最多50字符")
    private String department;

    @Schema(description = "是否主要联系人：0否 1是")
    private Integer isPrimary;

    @Schema(description = "电话")
    @Size(max = 20, message = "电话最多20字符")
    private String phone;

    @Schema(description = "手机")
    @Size(max = 20, message = "手机最多20字符")
    private String mobile;

    @Schema(description = "邮箱")
    @Size(max = 100, message = "邮箱最多100字符")
    private String email;

    @Schema(description = "微信")
    @Size(max = 50, message = "微信最多50字符")
    private String wechat;

    @Schema(description = "备注")
    @Size(max = 200, message = "备注最多200字符")
    private String remark;
}