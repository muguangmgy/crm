package com.platform.server.auth.security;

import com.platform.common.constants.RedisConstants;
import com.platform.common.context.UserContext;
import com.platform.common.context.UserContextHolder;
import com.platform.common.enums.ResultCode;
import com.platform.common.enums.StatusEnum;
import com.platform.common.utils.JsonUtil;
import com.platform.common.vo.Result;
import com.platform.server.common.config.InterceptorConfig;
import com.platform.server.common.constants.SecurityConstants;
import com.platform.server.system.role.po.SysRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final UserPermissionService userPermissionService;
    private final InterceptorConfig interceptorConfig;

    /**
     * refresh接口跳过Filter验证，由Controller处理
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/auth/refresh");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);

            // 无token，直接放行（由Spring Security的authenticationEntryPoint处理）
            if (!StringUtils.hasText(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // token格式无效，直接返回401
            if (!jwtTokenProvider.validateToken(token)) {
                writeResponse(response, ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage());
                return;
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            String redisKey = RedisConstants.REDIS_ACCESS_TOKEN_KEY + userId;
            String storedToken = redisTemplate.opsForValue().get(redisKey);

            // 单点登录控制：token不匹配
            if (interceptorConfig.getSingleLogin() && storedToken != null && !token.equals(storedToken)) {
                writeResponse(response, ResultCode.USER_ONLY_ONE_LOGIN.getCode(), ResultCode.USER_ONLY_ONE_LOGIN.getMessage());
                return;
            }

            // Redis白名单验证失败（token过期或被删除）
            if (storedToken == null || !storedToken.equals(token)) {
                writeResponse(response, ResultCode.UNAUTHORIZED.getCode(), "登录已过期");
                return;
            }

            // 验证通过，设置用户上下文
            String userCode = jwtTokenProvider.getUserCodeFromToken(token);
            Long tenantId = jwtTokenProvider.getTenantIdFromToken(token);
            String tenantCode = jwtTokenProvider.getTenantCodeFromToken(token);
            Long deptId = jwtTokenProvider.getDeptIdFromToken(token);

            // 从redis缓存取当前用户角色
            SysRole role = userPermissionService.getUserCurrentRole(userId);
            if (role == null) {
                writeResponse(response, ResultCode.BAD_REQUEST.getCode(), "用户角色不存在");
                return;
            }

            // 从缓存加载部门及子部门ID
            String deptIds = userPermissionService.getDeptIds(deptId);

            // 设置用户上下文
            UserContext userContext = UserContext.builder()
                    .userId(userId)
                    .userCode(userCode)
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .deptId(deptId)
                    .roleId(role.getId())
                    .roleCode(role.getRoleCode())
                    .dataScope(role.getDataScope())
                    .deptIds(deptIds)
                    .build();

            // 从缓存加载用户所有角色权限
            Set<String> permissions = userPermissionService.getUserPermissions(userId);
            // 创建权限
            List<SimpleGrantedAuthority> authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // 创建认证
            UsernamePasswordAuthenticationToken authentication = getUserNamePasswordAuthenticationToken(userContext, authorities);

            // 设置认证
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserContextHolder.setContext(userContext);

            filterChain.doFilter(request, response);
        } finally {
            // 清理ThreadLocal，防止内存泄露
            UserContextHolder.clear();
            SecurityContextHolder.clearContext();
        }
    }


    /**
     * 创建认证
     *
     * @param userContext
     * @param authorities
     * @return
     */
    private static @NonNull UsernamePasswordAuthenticationToken getUserNamePasswordAuthenticationToken(UserContext userContext, List<SimpleGrantedAuthority> authorities) {
        UserPrincipal principal = new UserPrincipal(
                userContext.getUserId(),
                userContext.getUserCode(),
                null,
                userContext.getTenantId(),
                userContext.getDeptId(),
                StatusEnum.ENABLED.getCode(),
                authorities);

        // 创建认证
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        return authentication;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(SecurityConstants.TOKEN_PREFIX.length());
        }
        return null;
    }


    /**
     * 响应结果
     *
     * @param response
     * @param code
     * @param message
     * @throws IOException
     */
    public void writeResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(JsonUtil.toJson(Result.error(code, message)));
    }
}