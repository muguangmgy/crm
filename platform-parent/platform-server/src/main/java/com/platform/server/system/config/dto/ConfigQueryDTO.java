package com.platform.server.system.config.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "业务参数查询条件")
public class ConfigQueryDTO extends PageQuery {

    @Schema(description = "租户ID（平台管理员可指定）")
    private Long tenantId;

    @Schema(description = "分组编码")
    private String groupCode;

    @Schema(description = "参数键")
    private String configKey;

    @Schema(description = "参数名称")
    private String configName;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}