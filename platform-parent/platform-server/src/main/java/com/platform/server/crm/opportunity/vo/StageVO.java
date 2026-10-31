package com.platform.server.crm.opportunity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "商机阶段信息响应")
public class StageVO {

    @Schema(description = "阶段ID")
    private Long id;

    @Schema(description = "阶段名称")
    private String stageName;

    @Schema(description = "阶段编码")
    private String stageCode;

    @Schema(description = "成交概率（0-100）")
    private Integer probability;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否成交阶段：0否 1是")
    private Integer isWin;

    @Schema(description = "是否失败阶段：0否 1是")
    private Integer isLost;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}