package com.platform.server.auth.controller;

import com.platform.common.annotation.Idempotent;
import com.platform.common.annotation.RepeatLimit;
import com.platform.common.constants.PlatformConstants;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.exception.BusinessException;
import com.platform.common.vo.Result;
import com.platform.server.auth.dto.LoginDTO;
import com.platform.server.auth.dto.PasswordChangeDTO;
import com.platform.server.auth.dto.ProfileUpdateDTO;
import com.platform.server.auth.service.AuthService;
import com.platform.server.auth.vo.CaptchaVO;
import com.platform.server.auth.vo.TokenVO;
import com.platform.server.auth.vo.UserInfoVO;
import com.platform.server.system.log.login.service.SysLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理", description = "登录、注销、Token刷新等接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SysLoginLogService sysLoginLogService;


    @Operation(summary = "获取验证码", description = "生成图形验证码，返回验证码Key和Base64图片")
    @ApiResponse(responseCode = "200", description = "成功返回验证码")
    @GetMapping("/captcha")
    @RepeatLimit(keyExpression = "#clientId", intervalSeconds = 3, message = "刷新验证码频繁")
    public Result<CaptchaVO> getCaptcha(
            @Parameter(description = "客户端标识", required = true, example = "browser-uuid")
            @RequestParam String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "客户端标识不能为空");
        }
        return Result.success(authService.generateCaptcha(clientId));
    }

    @Operation(summary = "用户登录", description = "用户名密码登录，验证码校验，返回JWT Token")
    @ApiResponse(responseCode = "200", description = "成功返回Token")
    @PostMapping("/login")
    @Idempotent(keyExpression = "#loginDTO.userCode", expireSeconds = 5, message = "登录频繁")
    public Result<TokenVO> login(
            @Parameter(description = "登录信息", required = true)
            @Valid @RequestBody LoginDTO loginDTO,
            @Parameter(description = "客户端标识", required = true, example = "browser-uuid")
            @RequestParam String clientId,
            HttpServletRequest request) {
        if (clientId == null || clientId.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "客户端标识不能为空");
        }
        TokenVO tokenVO = authService.login(request, loginDTO, clientId);
        return Result.success(tokenVO);
    }

    @Operation(summary = "刷新Token", description = "使用当前Token刷新获取新Token")
    @ApiResponse(responseCode = "200", description = "成功返回新Token")
    @PostMapping("/refresh")
    @Idempotent(keyExpression = "#authorization", expireSeconds = 10, message = "token刷新频繁")
    public Result<TokenVO> refreshToken(
            @Parameter(description = "Authorization Header", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIs...")
            @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        return Result.success(authService.refreshToken(token));
    }

    @Operation(summary = "用户注销", description = "清除用户Token，退出登录")
    @ApiResponse(responseCode = "200", description = "成功注销")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String userCode = UserContextHolder.getUserCode();
        Long tenantId = UserContextHolder.getTenantId();

        authService.logout();
        // 记录注销日志（异步）
        sysLoginLogService.saveLoginLog(userCode, tenantId, PlatformConstants.LOGIN_TYPE_LOGOUT, PlatformConstants.STATUS_SUCCESS,
                "注销成功", request);
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的基本信息")
    @ApiResponse(responseCode = "200", description = "成功返回用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getCurrentUserInfo() {
        return Result.success(authService.getCurrentUserInfo());
    }

    @Operation(summary = "修改个人信息", description = "修改当前用户的姓名和头像")
    @ApiResponse(responseCode = "200", description = "成功修改个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(
            @Parameter(description = "个人信息", required = true)
            @Valid @RequestBody ProfileUpdateDTO dto) {
        authService.updateProfile(dto);
        return Result.success();
    }

    @Operation(summary = "修改密码", description = "修改当前用户密码，需验证旧密码")
    @ApiResponse(responseCode = "200", description = "成功修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(
            @Parameter(description = "密码信息", required = true)
            @Valid @RequestBody PasswordChangeDTO dto) {
        authService.changePassword(dto);
        return Result.success();
    }
}