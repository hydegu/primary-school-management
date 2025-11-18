package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.exception.ApiException;
import com.example.primaryschoolmanagement.common.utils.JwtUtils;
import com.example.primaryschoolmanagement.config.JwtProperties;
import com.example.primaryschoolmanagement.dao.UserDao;
import com.example.primaryschoolmanagement.dto.LoginRequest;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Role;
import com.example.primaryschoolmanagement.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserDao, AppUser> implements UserService {

    @Resource
    private UserDao userRepo;

    @Resource
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Resource
    private CacheManager cacheManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private JwtProperties jwtProperties;

    @Override
    @Cacheable(cacheNames = "users:profile", key = "#name", unless = "#result == null")
    public AppUser findByUserName(String name) {
        return userRepo.selectOne(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getUsername, name));
    }

    @Override
    public int regUser(AppUser appUser) {
        return userRepo.insert(appUser);
    }

    @Override
    public Optional<AppUser> findByIdentifier(String identifier) {
        if (!StringUtils.hasText(identifier)) {
            log.warn("未找到用户");
            return Optional.empty();
        }
        String value = identifier.trim();
        LambdaQueryWrapper<AppUser> query = new LambdaQueryWrapper<>();
        query.eq(AppUser::getUsername, value)
                .or()
                .eq(AppUser::getEmail, value);
        AppUser user = userRepo.selectOne(query);
        return Optional.ofNullable(user);
    }

    @Override
    @CacheEvict(cacheNames = "users:profile", key = "#user.username", condition = "#user != null")
    public void updatePassword(AppUser user, String rawPassword) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户信息不完整");
        }
        String encoded = passwordEncoder.encode(rawPassword);
        user.setPassword(encoded);
        LocalDateTime now = LocalDateTime.now();
        user.setUpdatedAt(now);
        int affected = userRepo.updateById(user);
        if (affected <= 0) {
            log.error("加载密码失败, userId={}", user.getId());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "加载密码失败");
        }
        log.info("密码更新成功, userId={}", user.getId());
    }

    @Override
    @CacheEvict(cacheNames = "users:roles", key = "#userId")
    public Role selectRolesByUserId(Long userId) {
        return userRepo.selectRolesByUserId(userId);
    }

    @Override
    public String login(LoginRequest loginRequest) {
        log.info("用户登录请求：identifier={}", loginRequest.getIdentifier());

        // 查找用户
        AppUser user = findByIdentifier(loginRequest.getIdentifier())
                .orElseThrow(() -> {
                    log.warn("登录失败：用户不存在，identifier={}", loginRequest.getIdentifier());
                    return new IllegalArgumentException("用户名或密码错误");
                });

        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("登录失败：密码错误，userId={}", user.getId());
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            log.warn("登录失败：用户已被禁用，userId={}", user.getId());
            throw new IllegalArgumentException("用户已被禁用");
        }

        // 获取用户角色
        Role role = selectRolesByUserId(user.getId());
        String roleCode = role != null ? role.getRoleCode() : "student";

        // 生成令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("userId", user.getId());
        claims.put("roles", roleCode);

        String token = JwtUtils.createJwt(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );

        log.info("用户登录成功：userId={}, username={}, role={}", user.getId(), user.getUsername(), roleCode);
        return token;
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            log.warn("登出失败：token为空");
            throw new IllegalArgumentException("Token不能为空");
        }

        try {
            // 验证token并获取剩余有效期
            long expiration = JwtUtils.getTokenExpiration(jwtProperties.getUserSecretKey(), token);

            if (expiration <= 0) {
                log.warn("登出失败：token已过期或无效");
                throw new IllegalArgumentException("Token已过期或无效");
            }

            // 将token加入黑名单，使用Redis存储，过期时间与token剩余有效期一致
            String blacklistKey = "jwt:blacklist:" + token;
            redisTemplate.opsForValue().set(blacklistKey, "logout", expiration, TimeUnit.SECONDS);

            log.info("用户成功登出，token已加入黑名单");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("登出过程发生异常", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "登出失败");
        }
    }

    // 辅助方法：清除用户缓存
    private void evictUserCache(String userName) {
        if (userName == null) {
            return;
        }
        try {
            Cache cache = cacheManager.getCache("users:profile");
            if (cache != null) {
                cache.evict(userName);
                log.debug("已清除用户缓存：userName={}", userName);
            }
        } catch (Exception e) {
            log.warn("清除用户缓存失败：userName={}, error={}", userName, e.getMessage());
        }
    }

}
