package com.platform.server.system.log.oper.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "操作日志查询条件")
public class OperLogQueryDTO extends PageQuery {

    @Schema(description = "用户名")
    private String userCode;

    @Schema(description = "模块名称")
    private String module;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "响应状态：1成功 0失败")
    private Integer responseStatus;

    @Schema(description = "操作时间起始")
    private LocalDateTime operTimeStart;

    @Schema(description = "操作时间截止")
    private LocalDateTime operTimeEnd;
}