package com.platform.server.system.log.login.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.log.login.dto.LoginLogQueryDTO;
import com.platform.server.system.log.login.service.SysLoginLogService;
import com.platform.server.system.log.login.vo.LoginLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录日志管理", description = "登录日志查询、清理接口")
@RestController
@RequestMapping("/api/system/log/login")
@RequiredArgsConstructor
public class LoginLogController {

    private final SysLoginLogService sysLoginLogService;

    @Operation(summary = "登录日志列表", description = "分页查询登录日志，支持用户ID、登录类型、状态筛选")
    @ApiResponse(responseCode = "200", description = "成功返回登录日志分页列表")
    @PreAuthorize("hasAuthority('system:log:login:list')")
    @GetMapping("/list")
    public Result<PageVO<LoginLogVO>> list(@Parameter(description = "查询条件") LoginLogQueryDTO query) {
        return Result.success(sysLoginLogService.getLoginLogPage(query));
    }

    @Operation(summary = "清理日志", description = "清理指定天数之前的登录日志")
    @ApiResponse(responseCode = "200", description = "成功清理日志")
    @PreAuthorize("hasAuthority('system:log:login:clean')")
    @OperLog(module = "登录日志管理", operation = "清理")
    @DeleteMapping("/clean")
    public Result<Void> cleanLogs(
            @Parameter(description = "保留天数", required = true, example = "30")
            @RequestParam Integer keepDays) {
        sysLoginLogService.cleanLogs(keepDays);
        return Result.success();
    }
}