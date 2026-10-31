package com.platform.server.crm.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "合同保存请求")
public class ContractSaveDTO {

    @Schema(description = "合同ID（修改时必填）")
    private Long id;

    @Schema(description = "关联客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @Schema(description = "关联商机ID", example = "1")
    private Long opportunityId;

    @Schema(description = "关联报价单ID", example = "1")
    private Long quoteId;

    @Schema(description = "合同名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "销售合同001")
    @NotBlank(message = "合同名称不能为空")
    @Size(max = 100, message = "合同名称最多100字符")
    private String contractName;

    @Schema(description = "合同类型：1销售合同 2采购合同", example = "1")
    private Integer contractType;

    @Schema(description = "合同金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "100000.00")
    @NotNull(message = "合同金额不能为空")
    private BigDecimal contractAmount;

    @Schema(description = "签订日期", example = "2026-04-30")
    private LocalDate signDate;

    @Schema(description = "合同开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-05-01")
    @NotNull(message = "合同开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "合同结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2027-04-30")
    @NotNull(message = "合同结束日期不能为空")
    private LocalDate endDate;

    @Schema(description = "客户方签约人ID", example = "1")
    private Long signUserId;

    @Schema(description = "我方签约人ID", example = "1")
    private Long ourSignUserId;

    @Schema(description = "负责人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "负责人不能为空")
    private Long ownerId;

    @Schema(description = "合同文件URL")
    @Size(max = 500, message = "合同文件URL最多500字符")
    private String fileUrl;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注最多500字符")
    private String remark;
}