package com.platform.server.system.dicttype.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "字典类型信息响应")
public class DictTypeVO {

    @Schema(description = "字典类型ID", example = "1")
    private Long id;

    @Schema(description = "租户ID", example = "0")
    private Long tenantId;

    @Schema(description = "字典编码", example = "STATUS")
    private String dictCode;

    @Schema(description = "字典名称", example = "状态字典")
    private String dictName;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}