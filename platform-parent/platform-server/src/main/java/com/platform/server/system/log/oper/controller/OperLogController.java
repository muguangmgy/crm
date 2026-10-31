package com.platform.server.system.log.oper.controller;

import com.platform.common.annotation.OperLog;
import com.platform.common.vo.Result;
import com.platform.server.common.vo.PageVO;
import com.platform.server.system.log.oper.dto.OperLogQueryDTO;
import com.platform.server.system.log.oper.service.SysOperLogService;
import com.platform.server.system.log.oper.vo.OperLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "操作日志管理", description = "操作日志查询、清理接口")
@RestController
@RequestMapping("/api/system/log/oper")
@RequiredArgsConstructor
public class OperLogController {

    private final SysOperLogService sysOperLogService;

    @Operation(summary = "操作日志列表", description = "分页查询操作日志，支持用户ID、模块、操作类型、状态筛选")
    @ApiResponse(responseCode = "200", description = "成功返回操作日志分页列表")
    @PreAuthorize("hasAuthority('system:log:oper:list')")
    @GetMapping("/list")
    public Result<PageVO<OperLogVO>> list(@Parameter(description = "查询条件") OperLogQueryDTO query) {
        return Result.success(sysOperLogService.getOperLogPage(query));
    }

    @Operation(summary = "清理日志", description = "清理指定天数之前的操作日志")
    @ApiResponse(responseCode = "200", description = "成功清理日志")
    @PreAuthorize("hasAuthority('system:log:oper:clean')")
    @OperLog(module = "操作日志管理", operation = "清理")
    @DeleteMapping("/clean")
    public Result<Void> cleanLogs(
            @Parameter(description = "保留天数", required = true, example = "30")
            @RequestParam Integer keepDays) {
        sysOperLogService.cleanLogs(keepDays);
        return Result.success();
    }
}