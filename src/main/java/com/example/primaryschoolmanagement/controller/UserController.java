package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.config.JwtProperties;
import com.example.primaryschoolmanagement.dto.LoginRequest;
import com.example.primaryschoolmanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;
    /**
     * 用户登录 - 返回Token
     *
     * @param loginRequest 登录请求
     * @return 令牌
     */
    @PostMapping("/login")
    public R login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.login(loginRequest);
            return R.ok(token);
        } catch (IllegalArgumentException e) {
            log.warn("登录失败：{}", e.getMessage());
            return R.er(ResultCode.USERCHECK);
        } catch (Exception e) {
            log.error("登录过程发生异常", e);
            return R.er(ResultCode.ERROR);
        }
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

            // 调用Service层处理登出逻辑
            userService.logout(token);
            return R.ok("登出成功");
        } catch (IllegalArgumentException e) {
            log.warn("登出失败：{}", e.getMessage());
            return R.er(ResultCode.UNAUTHORIZED);
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
