package com.platform.server.crm.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "跟进记录保存请求")
public class FollowSaveDTO {

    @Schema(description = "跟进记录ID（修改时必填）")
    private Long id;

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @Schema(description = "跟进方式", example = "电话")
    @Size(max = 50, message = "跟进方式最多50字符")
    private String followType;

    @Schema(description = "跟进内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "跟进内容不能为空")
    @Size(max = 500, message = "跟进内容最多500字符")
    private String followContent;

    @Schema(description = "下次跟进时间", example = "2024-02-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowTime;
}