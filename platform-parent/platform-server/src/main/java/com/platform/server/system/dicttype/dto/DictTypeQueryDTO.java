package com.platform.server.system.dicttype.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典类型查询条件")
public class DictTypeQueryDTO extends PageQuery {

    @Schema(description = "租户ID（平台管理员可指定）")
    private Long tenantId;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}