package com.platform.server.auth.security;

import com.platform.server.common.config.InterceptorConfig;
import com.platform.server.common.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    @Resource
    private InterceptorConfig interceptorConfig;

    public JwtTokenProvider() {
        this.secretKey = Keys.hmacShaKeyFor(
                SecurityConstants.JWT_SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * 生成AccessToken，支持自定义过期时间（毫秒）
     */
    public String generateAccessToken(Long userId, String userCode, Long tenantId, String tenantCode, Long deptId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userCode", userCode);
        claims.put("tenantId", tenantId);
        claims.put("tenantCode", tenantCode);
        claims.put("deptId", deptId);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + interceptorConfig.getAccessTokenExpireTime() * 60 * 1000L);

        return Jwts.builder()
                .claims(claims)
                .subject(userCode)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + interceptorConfig.getRefreshTokenExpireTime() * 24 * 60 * 60 * 1000);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 解析Token，忽略过期检查（用于刷新Token场景）
     * 过期时仍返回claims，签名无效等其他错误则抛异常
     */
    public Claims parseTokenIgnoringExpiration(String token) {
        try {
            return parseToken(token);
        } catch (ExpiredJwtException e) {
            // Token过期但claims仍可信，返回claims用于刷新
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public String getUserCodeFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userCode", String.class);
    }

    public Long getTenantIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("tenantId", Long.class);
    }

    public String getTenantCodeFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("tenantCode", String.class);
    }

    public Long getDeptIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("deptId", Long.class);
    }
}