package com.example.primaryschoolmanagement.common.utils;

import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

    /**
     * 判断当前用户是否拥有指定角色
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals(role) || auth.equals("ROLE_" + role));
    }

    /**
     * 判断当前用户是否是超级管理员
     */
    public static boolean isSuperAdmin() {
        return hasRole("super_admin");
    }
}
