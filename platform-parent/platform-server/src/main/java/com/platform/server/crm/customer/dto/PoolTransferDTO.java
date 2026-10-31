package com.platform.server.crm.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "客户转移请求")
public class PoolTransferDTO {

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @Schema(description = "新负责人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "新负责人不能为空")
    private Long newOwnerId;
}