package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.dto.LoginRequest;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Role;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public interface UserService extends IService<AppUser> {

    Role selectRolesByUserId(Long userId);

    AppUser findByUserName(String name);

    int regUser(AppUser appUser);

    Optional<AppUser> findByIdentifier(String identifier);

    void updatePassword(AppUser user, String rawPassword);

    /**
     * 用户登录 - 处理登录业务逻辑并返回Token
     *
     * @param loginRequest 登录请求
     * @return JWT Token
     */
    String login(LoginRequest loginRequest);

    /**
     * 用户登出 - 将Token加入黑名单
     *
     * @param token JWT Token
     */
    void logout(String token);

}
