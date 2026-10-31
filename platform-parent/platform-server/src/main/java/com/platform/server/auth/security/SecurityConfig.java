package com.platform.server.auth.security;

import com.platform.common.enums.ResultCode;
import com.platform.server.common.config.InterceptorConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final InterceptorConfig interceptorConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 禁用session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 异常处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            jwtAuthenticationFilter.writeResponse(response, ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage());
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            jwtAuthenticationFilter.writeResponse(response, ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage());
                        })
                )

                // 权限控制
                .authorizeHttpRequests(auth -> {
                            // 排除路径（登录、Swagger等）
                            if (interceptorConfig.getExcludePatterns() != null) {
                                auth.requestMatchers(interceptorConfig.getExcludePatterns().toArray(new String[0]))
                                        .permitAll();
                            }

                            //拦截路径
                            if (interceptorConfig.getPathPatterns() != null) {
                                auth.requestMatchers(interceptorConfig.getPathPatterns().toArray(new String[0])).authenticated();
                                // 其他路径允许访问
                                auth.anyRequest().permitAll();
                            } else {
                                auth.anyRequest().authenticated();
                            }
                        }
                )

                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}