package com.platform.server.system.dictdata.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "字典项信息响应")
public class DictDataVO {

    @Schema(description = "字典项ID", example = "1")
    private Long id;

    @Schema(description = "租户ID", example = "0")
    private Long tenantId;

    @Schema(description = "字典编码", example = "STATUS")
    private String dictCode;

    @Schema(description = "字典值", example = "1")
    private String itemValue;

    @Schema(description = "字典标签", example = "启用")
    private String itemLabel;

    @Schema(description = "排序号", example = "1")
    private Integer sort;

    @Schema(description = "状态：0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}