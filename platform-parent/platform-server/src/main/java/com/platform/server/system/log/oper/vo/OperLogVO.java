package com.platform.server.system.log.oper.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "操作日志响应")
public class OperLogVO {

    @Schema(description = "日志ID", example = "1")
    private Long id;

    @Schema(description = "租户ID", example = "1")
    private Long tenantId;

    @Schema(description = "姓名", example = "1")
    private String userName;

    @Schema(description = "用户名", example = "admin")
    private String userCode;

    @Schema(description = "模块名称", example = "用户管理")
    private String module;

    @Schema(description = "操作类型", example = "新增")
    private String operation;

    @Schema(description = "操作对象类型", example = "用户")
    private String targetType;

    @Schema(description = "操作对象ID", example = "1")
    private Long targetId;

    @Schema(description = "请求参数（JSON）")
    private String requestData;

    @Schema(description = "响应状态：1成功 0失败", example = "1")
    private Integer responseStatus;

    @Schema(description = "IP地址", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "操作时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operTime;

    @Schema(description = "执行耗时(ms)", example = "100")
    private Integer duration;
}