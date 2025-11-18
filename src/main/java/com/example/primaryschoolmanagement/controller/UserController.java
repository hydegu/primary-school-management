package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.JwtUtils;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.config.JwtProperties;
import com.example.primaryschoolmanagement.dto.LoginRequest;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Role;
import com.example.primaryschoolmanagement.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtProperties jwtProperties;
    /**
     * 用户登录 - 返回Token
     *
     * @param loginRequest 登录请求
     * @return 令牌
     */
    @PostMapping("/login")
    public R login(@Valid @RequestBody LoginRequest loginRequest) {
        // 查找用户
        AppUser user = userService.findByIdentifier(loginRequest.getIdentifier())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new IllegalArgumentException("用户已被禁用");
        }
        // 获取用户角色
        Role role = userService.selectRolesByUserId(user.getId());
        String roleCode = role != null ? role.getRoleCode() : "student";
        // 生成令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("userId", user.getId());
        claims.put("roles", roleCode);
        String token = JwtUtils.createJwt(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        return R.ok(token);
    }

    /**
     * 用户登出 - 撤销Token
     * @param request HTTP请求对象
     * @return 登出结果
     */
    @PostMapping("/logout")
    public R logout(HttpServletRequest request) {
        try {
            // 从请求头中获取token
            String token = resolveToken(request);

            if (token == null || token.isBlank()) {
                log.warn("登出失败：未找到有效的token");
                return R.er(ResultCode.UNAUTHORIZED);
            }

            // 验证token并获取剩余有效期
            long expiration = JwtUtils.getTokenExpiration(jwtProperties.getUserSecretKey(), token);

            if (expiration <= 0) {
                log.warn("登出失败：token已过期或无效");
                return R.er(ResultCode.UNAUTHORIZED);
            }

            // 将token加入黑名单，使用Redis存储，过期时间与token剩余有效期一致
            String blacklistKey = "jwt:blacklist:" + token;
            redisTemplate.opsForValue().set(blacklistKey, "logout", expiration, TimeUnit.SECONDS);

            log.info("用户成功登出，token已加入黑名单");
            return R.ok("登出成功");
        } catch (Exception e) {
            log.error("登出过程发生异常", e);
            return R.er(ResultCode.ERROR);
        }
    }

    /**
     * 从请求中解析token
     * @param request HTTP请求对象
     * @return token字符串，如果不存在则返回null
     */
    private String resolveToken(HttpServletRequest request) {
        // 优先从Authorization请求头获取
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 从自定义请求头获取
        String tokenName = jwtProperties.getUserTokenName();
        if (tokenName != null && !tokenName.isBlank()) {
            String token = request.getHeader(tokenName);
            if (token != null && !token.isBlank()) {
                return token;
            }
        }

        return null;
    }
}
