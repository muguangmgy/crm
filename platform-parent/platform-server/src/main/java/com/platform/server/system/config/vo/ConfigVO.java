package com.platform.server.system.config.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "业务参数信息响应")
public class ConfigVO {

    @Schema(description = "参数ID", example = "1")
    private Long id;

    @Schema(description = "租户ID", example = "0")
    private Long tenantId;

    @Schema(description = "分组编码", example = "basic")
    private String groupCode;

    @Schema(description = "参数键", example = "site_name")
    private String configKey;

    @Schema(description = "参数名称", example = "站点名称")
    private String configName;

    @Schema(description = "参数值", example = "管理平台")
    private String configValue;

    @Schema(description = "值类型：1-string 2-number 3-json", example = "1")
    private Integer valueType;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}