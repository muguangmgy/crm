package com.platform.server.system.log.login.dto;

import com.platform.server.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "登录日志查询条件")
public class LoginLogQueryDTO extends PageQuery {

    @Schema(description = "用户名")
    private String userCode;

    @Schema(description = "登录类型：1登录 2注销 3刷新")
    private Integer loginType;

    @Schema(description = "状态：1成功 0失败")
    private Integer status;

    @Schema(description = "登录时间起始")
    private LocalDateTime loginTimeStart;

    @Schema(description = "登录时间截止")
    private LocalDateTime loginTimeEnd;
}