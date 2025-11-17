package com.example.primaryschoolmanagement.config;

import com.example.primaryschoolmanagement.common.utils.JwtUtils;
import com.example.primaryschoolmanagement.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String tokenHeader = resolveTokenHeader(request);
        if (log.isDebugEnabled()) {
            log.debug("解析请求头中的JWT token: {}", tokenHeader != null ? "[present]" : "null");
        }
        String token = resolveToken(tokenHeader);
        if (token == null) {
            token = resolveTokenFromCookie(request);
        }
        if (log.isDebugEnabled()) {
            log.debug("已解析 JWT token: {}", token != null ? maskToken(token) : "null");
        }
        if (token != null) {
            try {
                // 检查token是否在黑名单中
                String blacklistKey = "jwt:blacklist:" + token;
                Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);

                if (Boolean.TRUE.equals(isBlacklisted)) {
                    log.warn("JWT token已被加入黑名单（用户已登出）");
                    SecurityContextHolder.clearContext();
                } else {
                    Claims claims = JwtUtils.parseJWT(jwtProperties.getUserSecretKey(), token);
                    setupAuthenticationIfNecessary(claims, request);
                }
            } catch (JwtException ex) {
                log.warn("JWT token验证失败: {}", ex.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveTokenHeader(HttpServletRequest request) {
        String headerSource = HttpHeaders.AUTHORIZATION;
        String header = request.getHeader(headerSource);
        if (header == null) {
            String configuredHeader = jwtProperties.getUserTokenName();
            if (configuredHeader != null && !configuredHeader.equalsIgnoreCase(HttpHeaders.AUTHORIZATION)) {
                headerSource = configuredHeader;
                header = request.getHeader(configuredHeader);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("从请求头 '{}' 中解析JWT token: {}", headerSource, header != null ? "[present]" : "null");
        }
        return header;
    }

    private String resolveToken(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }
        if (headerValue.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return headerValue.substring(7);
        }
        return headerValue;
    }

    private String resolveTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        String cookieName = jwtProperties.getUserTokenName();
        if (cookieName == null || cookieName.isBlank()) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(cookie -> {
                    if (log.isDebugEnabled()) {
                        log.debug("从Cookie '{}' 中解析JWT token: {}", cookieName, cookie.getValue() != null ? "[present]" : "null");
                    }
                    return cookie.getValue();
                })
                .findFirst()
                .orElse(null);
    }

    private String maskToken(String token) {
        if (token.length() <= 10) {
            return token;
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }

    private void setupAuthenticationIfNecessary(Claims claims, HttpServletRequest request) {
        String username = claims.get("username", String.class);
        if (username == null) {
            log.debug("JWT token中不包含用户名声明，跳过认证设置");
            return;
        }
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
