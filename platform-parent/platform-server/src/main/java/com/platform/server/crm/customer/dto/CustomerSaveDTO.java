package com.platform.server.crm.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "客户保存请求")
public class CustomerSaveDTO {

    @Schema(description = "客户ID（修改时必填）")
    private Long id;

    @Schema(description = "客户类型：1企业 2个人", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "客户类型不能为空")
    private Integer customerType;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "某科技公司")
    @NotBlank(message = "客户名称不能为空")
    @Size(max = 100, message = "客户名称最多100字符")
    private String customerName;

    @Schema(description = "行业", example = "IT")
    @Size(max = 50, message = "行业最多50字符")
    private String industry;

    @Schema(description = "企业规模", example = "100-500人")
    @Size(max = 20, message = "企业规模最多20字符")
    private String companySize;

    @Schema(description = "省份", example = "广东省")
    @Size(max = 50, message = "省份最多50字符")
    private String province;

    @Schema(description = "城市", example = "深圳市")
    @Size(max = 50, message = "城市最多50字符")
    private String city;

    @Schema(description = "详细地址", example = "南山区科技园")
    @Size(max = 200, message = "详细地址最多200字符")
    private String address;

    @Schema(description = "网站", example = "https://www.example.com")
    @Size(max = 100, message = "网站最多100字符")
    private String website;

    @Schema(description = "性别：0未知 1男 2女", example = "1")
    private Integer gender;

    @Schema(description = "生日", example = "1990-01-01")
    private LocalDate birthday;

    @Schema(description = "职业", example = "工程师")
    @Size(max = 50, message = "职业最多50字符")
    private String occupation;

    @Schema(description = "电话", example = "13800138000")
    @Size(max = 20, message = "电话最多20字符")
    private String phone;

    @Schema(description = "邮箱", example = "contact@example.com")
    @Size(max = 100, message = "邮箱最多100字符")
    private String email;

    @Schema(description = "客户来源", example = "展会")
    @Size(max = 50, message = "客户来源最多50字符")
    private String source;

    @Schema(description = "客户等级：1普通 2重要 3VIP", example = "1")
    private Integer level;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注最多500字符")
    private String remark;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;
}