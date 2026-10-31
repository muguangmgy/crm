package com.platform.server.system.log.login.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "登录日志响应")
public class LoginLogVO {

    @Schema(description = "日志ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "admin")
    private String userCode;

    @Schema(description = "姓名", example = "1")
    private String userName;

    @Schema(description = "租户ID", example = "1")
    private Long tenantId;

    @Schema(description = "登录类型：1登录 2注销 3刷新", example = "1")
    private Integer loginType;

    @Schema(description = "IP地址", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "设备信息")
    private String deviceInfo;

    @Schema(description = "浏览器", example = "Chrome")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    private String os;

    @Schema(description = "登录时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

    @Schema(description = "状态：1成功 0失败", example = "1")
    private Integer status;

    @Schema(description = "提示消息")
    private String message;
}