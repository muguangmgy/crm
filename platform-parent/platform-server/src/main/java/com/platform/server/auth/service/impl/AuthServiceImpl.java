package com.platform.server.auth.service.impl;

import com.platform.common.constants.PlatformConstants;
import com.platform.common.constants.RedisConstants;
import com.platform.common.context.UserContext;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.enums.StatusEnum;
import com.platform.common.exception.BusinessException;
import com.platform.common.service.RedisLockService;
import com.platform.common.utils.JsonUtil;
import com.platform.server.auth.dto.LoginDTO;
import com.platform.server.auth.dto.PasswordChangeDTO;
import com.platform.server.auth.dto.ProfileUpdateDTO;
import com.platform.server.auth.security.JwtTokenProvider;
import com.platform.server.auth.security.UserPermissionService;
import com.platform.server.auth.service.AuthService;
import com.platform.server.auth.vo.CaptchaVO;
import com.platform.server.auth.vo.RoleInfoVO;
import com.platform.server.auth.vo.TokenVO;
import com.platform.server.auth.vo.UserInfoVO;
import com.platform.server.common.config.InterceptorConfig;
import com.platform.server.common.constants.SecurityConstants;
import com.platform.server.common.converter.SystemConverter;
import com.platform.server.system.log.login.service.SysLoginLogService;
import com.platform.server.system.role.po.SysRole;
import com.platform.server.system.tenant.mapper.SysTenantMapper;
import com.platform.server.system.tenant.po.SysTenant;
import com.platform.server.system.user.mapper.SysUserMapper;
import com.platform.server.system.user.po.SysUser;
import com.platform.server.system.user.service.SysUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserService sysUserService;
    private final SysUserMapper sysUserMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UserPermissionService userPermissionService;
    private final SystemConverter systemConverter;
    private final SysLoginLogService sysLoginLogService;
    private final RedisLockService redisLockService;
    private final InterceptorConfig interceptorConfig;
    private final SysTenantMapper sysTenantMapper;

    @Override
    public CaptchaVO generateCaptcha(String clientId) {
        // 限制单个客户端的验证码生成次数
        String clientKey = RedisConstants.REDIS_CAPTCHA_COUNT_KEY + clientId;
        String countStr = redisTemplate.opsForValue().get(clientKey);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;
        if (count >= SecurityConstants.CAPTCHA_MAX_COUNT_PER_CLIENT) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "验证码获取次数过多，请稍后再试");
        }

        String captchaCode = generateRandomCode(4);

        // 增加客户端验证码计数，设置与验证码相同的过期时间
        redisTemplate.opsForValue().set(clientKey, String.valueOf(count + 1),
                SecurityConstants.CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 使用 clientId 作为 key，一个客户端只保留一个有效验证码
        String redisKey = RedisConstants.REDIS_CAPTCHA_KEY + clientId;
        redisTemplate.opsForValue().set(redisKey, captchaCode,
                SecurityConstants.CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES);

        String captchaImage = "data:image/svg+xml;base64," +
                encodeSvgCaptcha(captchaCode);
        return new CaptchaVO(captchaImage);
    }

    @Override
    public TokenVO login(HttpServletRequest request, LoginDTO loginDTO, String clientId) {
        Long tenantId = SecurityConstants.DEFAULT_TENANT_ID;
        try {
            // 检查客户端登录失败次数
            String clientFailKey = RedisConstants.REDIS_LOGIN_FAIL_KEY + clientId;
            String userFailKey = RedisConstants.REDIS_LOGIN_FAIL_KEY + "user:" + loginDTO.getUserCode();
            loginCheck(clientFailKey, userFailKey);

            // 使用 clientId 获取验证码
            String captchaRedisKey = RedisConstants.REDIS_CAPTCHA_KEY + clientId;
            String clientKey = RedisConstants.REDIS_CAPTCHA_COUNT_KEY + clientId;
            String storedCaptcha = redisTemplate.opsForValue().get(captchaRedisKey);

            if (storedCaptcha == null || !storedCaptcha.equalsIgnoreCase(loginDTO.getCaptcha())) {
                // 验证码错误，增加失败计数
                incrementLoginFailCount(clientId);
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "验证码错误或已过期");
            }
            // 验证成功后删除验证码
            redisTemplate.delete(captchaRedisKey);

            // 根据用户名查询用户（不限租户）
            SysUser user = sysUserService.getByUserCode(loginDTO.getUserCode());
            if (user == null) {
                incrementLoginFailCount(clientId);
                throw new BusinessException(ResultCode.LOGIN_FAILED.getCode(), "用户名或密码错误");
            }

            // 使用用户实际的租户ID生成Token
            SysTenant tenant = sysTenantMapper.selectById(user.getTenantId());
            if (tenant == null) {
                throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
            }
            tenantId = tenant.getId();
            // 判断租户是否禁用
            if (tenant.getStatus() != StatusEnum.ENABLED.getCode()) {
                throw new BusinessException(ResultCode.TENANT_DISABLED);
            }

            //判断租户是否到期
            if (tenant.getExpireTime() != null && tenant.getExpireTime().isBefore(LocalDateTime.now())) {
                throw new BusinessException(ResultCode.TENANT_EXPIRED);
            }

            if (user.getStatus() != StatusEnum.ENABLED.getCode()) {
                throw new BusinessException(ResultCode.USER_DISABLED);
            }

            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                // 密码错误，增加用户和客户端失败计数
                incrementLoginFailCount(clientId);
                incrementLoginFailCount("user:" + user.getUserCode());
                throw new BusinessException(ResultCode.LOGIN_FAILED.getCode(), "用户名或密码错误");
            }

            // 登录成功，清除失败计数
            redisTemplate.delete(clientFailKey);
            redisTemplate.delete(userFailKey);
            redisTemplate.delete(clientKey);


            // 预加载权限缓存
            userPermissionService.getUserCurrentRole(user.getId());
            userPermissionService.getUserPermissions(user.getId());
            userPermissionService.getDeptIds(user.getDeptId());


            // 根据记住密码设置不同的有效期
            long accessExpireMinutes = loginDTO.getRememberMe() != null && loginDTO.getRememberMe()
                    ? interceptorConfig.getRefreshTokenExpireTime() * 24 * 60L  // 记住密码：与RefreshToken一致（天转分钟）
                    : interceptorConfig.getAccessTokenExpireTime();             // 不记住：正常有效期（分钟）

            String accessToken = jwtTokenProvider.generateAccessToken(
                    user.getId(), user.getUserCode(), tenantId, tenant.getTenantCode(), user.getDeptId()
            );
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

            String accessRedisKey = RedisConstants.REDIS_ACCESS_TOKEN_KEY + user.getId();
            String refreshRedisKey = RedisConstants.REDIS_REFRESH_TOKEN_KEY + user.getId();

            redisTemplate.opsForValue().set(accessRedisKey, accessToken, accessExpireMinutes, TimeUnit.MINUTES);
            redisTemplate.opsForValue().set(refreshRedisKey, refreshToken, interceptorConfig.getRefreshTokenExpireTime(), TimeUnit.DAYS);

            sysUserService.updateLastLoginTime(user.getId());

            // 登录成功，记录日志（异步）
            sysLoginLogService.saveLoginLog(loginDTO.getUserCode(), tenantId, PlatformConstants.LOGIN_TYPE_LOGIN, PlatformConstants.STATUS_SUCCESS,
                    "登录成功", request);

            return new TokenVO(accessToken, System.currentTimeMillis() + accessExpireMinutes * 60 * 1000, user.getTenantId());
        } catch (BusinessException e) {
            // 登录失败，记录日志（异步）
            sysLoginLogService.saveLoginLog(loginDTO.getUserCode(), tenantId, PlatformConstants.LOGIN_TYPE_LOGIN, PlatformConstants.STATUS_FAIL,
                    e.getMessage(), request);
            throw e;
        }
    }

    /**
     * 登录检查
     *
     * @param clientFailKey
     * @param userFailKey
     */
    private void loginCheck(String clientFailKey, String userFailKey) {
        String clientFailCount = redisTemplate.opsForValue().get(clientFailKey);
        if (clientFailCount != null && Integer.parseInt(clientFailCount) >= SecurityConstants.LOGIN_FAIL_MAX_COUNT) {
            throw new BusinessException(ResultCode.LOGIN_FAILED.getCode(),
                    "登录失败次数过多，请" + SecurityConstants.LOGIN_FAIL_LOCK_MINUTES + "分钟后再试");
        }

        // 检查用户是否被登录锁定
        String userFailCount = redisTemplate.opsForValue().get(userFailKey);
        if (userFailCount != null && Integer.parseInt(userFailCount) >= SecurityConstants.LOGIN_FAIL_MAX_COUNT) {
            throw new BusinessException(ResultCode.LOGIN_FAILED.getCode(),
                    "账号已被锁定，请" + SecurityConstants.LOGIN_FAIL_LOCK_MINUTES + "分钟后再试");
        }
    }

    private void incrementLoginFailCount(String key) {
        String failKey = RedisConstants.REDIS_LOGIN_FAIL_KEY + key;
        String countStr = redisTemplate.opsForValue().get(failKey);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;
        redisTemplate.opsForValue().set(failKey, String.valueOf(count + 1),
                SecurityConstants.LOGIN_FAIL_LOCK_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public TokenVO refreshToken(String oldAccessToken) {
        // 解析Token，忽略过期检查（accessToken可能已过期）
        Claims claims;
        try {
            claims = jwtTokenProvider.parseTokenIgnoringExpiration(oldAccessToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "Token无效");
        }

        Long userId = claims.get("userId", Long.class);
        String userCode = claims.get("userCode", String.class);
        Long tenantId = claims.get("tenantId", Long.class);
        String tenantCode = claims.get("tenantCode", String.class);
        Long deptId = claims.get("deptId", Long.class);


        String refreshRedisKey = RedisConstants.REDIS_REFRESH_TOKEN_KEY + userId;
        String refreshToken = redisTemplate.opsForValue().get(refreshRedisKey);

        if (refreshToken == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "登录已过期，请重新登录");
        }

        // 使用分布式锁保证并发刷新的原子性
        String lockKey = RedisConstants.REDIS_TOKEN_REFRESH_LOCK_KEY + userId;
        TokenVO result = redisLockService.executeWithLock(lockKey, 3, 5, () -> {
            String accessRedisKey = RedisConstants.REDIS_ACCESS_TOKEN_KEY + userId;
            String currentToken = redisTemplate.opsForValue().get(accessRedisKey);

            // 幂等检查：如果当前token已经不是oldAccessToken，说明已被其他客户端刷新，直接返回现有token
            if (currentToken != null && !currentToken.equals(oldAccessToken)) {
                long ttl = redisTemplate.getExpire(accessRedisKey, TimeUnit.SECONDS);
                return new TokenVO(currentToken, System.currentTimeMillis() + ttl * 1000, tenantId);
            }

            String newAccessToken = jwtTokenProvider.generateAccessToken(userId, userCode, tenantId, tenantCode, deptId);

            long accessExpire = interceptorConfig.getAccessTokenExpireTime();
            redisTemplate.opsForValue().set(accessRedisKey, newAccessToken, accessExpire, TimeUnit.MINUTES);

            // 刷新用户当前角色缓存时间
            SysRole role = userPermissionService.getUserCurrentRole(userId);
            redisTemplate.opsForValue().set(RedisConstants.REDIS_USER_ROLE_KEY + userId, JsonUtil.toJson(role),
                    accessExpire, TimeUnit.MINUTES);
            return new TokenVO(newAccessToken, System.currentTimeMillis() + accessExpire * 60 * 1000, tenantId);
        });

        if (result == null) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR.getCode(), "刷新Token繁忙，请稍后重试");
        }
        return result;
    }

    @Override
    public void logout() {
        Long userId = UserContextHolder.getUserId();
        if (userId != null) {
            String accessRedisKey = RedisConstants.REDIS_ACCESS_TOKEN_KEY + userId;
            String refreshRedisKey = RedisConstants.REDIS_REFRESH_TOKEN_KEY + userId;
            redisTemplate.delete(accessRedisKey);
            redisTemplate.delete(refreshRedisKey);
            // 清除权限缓存
            userPermissionService.clearUserPermissionCache(userId);
        }
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        UserContext userContext = UserContextHolder.getContext();
        Long userId = userContext.getUserId();
        Long roleId = userContext.getRoleId();
        String roleCode = userContext.getRoleCode();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录");
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户不存在");
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(userId);
        vo.setUserCode(user.getUserCode());
        vo.setUserName(user.getUserName());
        vo.setAvatar(user.getAvatar());
        vo.setTenantId(user.getTenantId());
        vo.setTenantCode(userContext.getTenantCode());
        vo.setDeptId(user.getDeptId());
        vo.setCurrentRoleId(userContext.getRoleId());

        // 加载用户角色权限标识
        Set<String> permissions = userPermissionService.getRolePermissions(roleId);
        vo.setPermissions(new ArrayList<>(permissions));

        // 加载用户角色列表（完整信息，按dataScope升序排序）
        List<SysRole> roles = userPermissionService.getUserRoles(userId);
        List<RoleInfoVO> roleInfoList = systemConverter.toRoleInfoVOList(roles);
        vo.setRoles(roleInfoList);

        // 判断是否平台角色
        vo.setIsPlatformRole(roleCode.toUpperCase().startsWith(PlatformConstants.PLATFORM_ROLE_CODE_PREFIX));
        return vo;
    }

    @Override
    public void updateProfile(ProfileUpdateDTO dto) {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录");
        }

        SysUser user = new SysUser();
        user.setId(userId);
        if (dto.getUserName() != null) {
            user.setUserName(dto.getUserName());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        sysUserMapper.updateById(user);
    }

    @Override
    public void changePassword(PasswordChangeDTO dto) {
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录");
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "旧密码错误");
        }

        // 更新新密码
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        sysUserMapper.updateById(updateUser);
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    private String encodeSvgCaptcha(String code) {
        // 随机背景颜色
        String bgColor = getRandomColor(200, 240);
        // 随机文字颜色
        String textColor = getRandomColor(30, 120);
        // 随机干扰线颜色
        String lineColor1 = getRandomColor(100, 180);
        String lineColor2 = getRandomColor(100, 180);

        // 随机干扰线坐标
        int line1X1 = (int) (Math.random() * 40);
        int line1Y1 = (int) (Math.random() * 40);
        int line1X2 = (int) (Math.random() * 80 + 40);
        int line1Y2 = (int) (Math.random() * 40);

        int line2X1 = (int) (Math.random() * 20);
        int line2Y1 = (int) (Math.random() * 40);
        int line2X2 = (int) (Math.random() * 100 + 20);
        int line2Y2 = (int) (Math.random() * 40);

        String svg = "<svg xmlns='http://www.w3.org/2000/svg' width='120' height='40'>" +
                "<rect fill='" + bgColor + "' width='120' height='40'/>" +
                "<line x1='" + line1X1 + "' y1='" + line1Y1 + "' x2='" + line1X2 + "' y2='" + line1Y2 + "' stroke='" + lineColor1 + "' stroke-width='1'/>" +
                "<line x1='" + line2X1 + "' y1='" + line2Y1 + "' x2='" + line2X2 + "' y2='" + line2Y2 + "' stroke='" + lineColor2 + "' stroke-width='1'/>" +
                "<text x='60' y='26' font-size='18' fill='" + textColor + "' font-family='Arial' text-anchor='middle'>" + code + "</text>" +
                "</svg>";
        return java.util.Base64.getEncoder().encodeToString(svg.getBytes());
    }

    private String getRandomColor(int min, int max) {
        int r = min + (int) (Math.random() * (max - min));
        int g = min + (int) (Math.random() * (max - min));
        int b = min + (int) (Math.random() * (max - min));
        return "rgb(" + r + "," + g + "," + b + ")";
    }
}