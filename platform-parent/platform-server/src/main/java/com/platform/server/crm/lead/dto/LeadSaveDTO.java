package com.platform.server.crm.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "线索保存请求")
public class LeadSaveDTO {

    @Schema(description = "线索ID（修改时必填）")
    private Long id;

    @Schema(description = "关联客户ID")
    private Long customerId;

    @Schema(description = "关联联系人ID")
    private Long contactId;

    @Schema(description = "线索名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "某公司采购意向")
    @NotBlank(message = "线索名称不能为空")
    @Size(max = 100, message = "线索名称最多100字符")
    private String leadName;

    @Schema(description = "线索来源", example = "展会")
    @Size(max = 50, message = "线索来源最多50字符")
    private String source;

    @Schema(description = "状态：1新建 2跟进中 3已转商机 4已失效", example = "1")
    private Integer status;

    @Schema(description = "电话", example = "13800138000")
    @Size(max = 20, message = "电话最多20字符")
    private String phone;

    @Schema(description = "邮箱", example = "contact@example.com")
    @Size(max = 100, message = "邮箱最多100字符")
    private String email;

    @Schema(description = "公司名称", example = "某科技公司")
    @Size(max = 100, message = "公司名称最多100字符")
    private String company;

    @Schema(description = "线索描述")
    @Size(max = 500, message = "线索描述最多500字符")
    private String description;

    @Schema(description = "负责人ID（新增时可不传，默认当前登录用户）", example = "1")
    private Long ownerId;
}