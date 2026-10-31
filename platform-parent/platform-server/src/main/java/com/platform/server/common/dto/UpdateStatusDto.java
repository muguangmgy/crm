package com.platform.server.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName: UpdateStatusDto
 * @Description: 更新状态
 * @Author: waynevip@foxmail.com
 * @Date: 2026/04/22 19:51
 */
@Data
public class UpdateStatusDto {

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
