package com.example.primaryschoolmanagement.common.utils;

import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全工具类 - 获取当前登录用户信息
 */
@Component
public class SecurityUtils {

    private static UserService userService;

    public SecurityUtils(UserService userService) {
        SecurityUtils.userService = userService;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        AppUser user = userService.findByUserName(username);
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前登录用户
     */
    public static AppUser getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        return userService.findByUserName(username);
    }
}
