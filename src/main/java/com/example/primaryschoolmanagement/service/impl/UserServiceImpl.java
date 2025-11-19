package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.exception.*;
import com.example.primaryschoolmanagement.common.utils.JwtUtils;
import com.example.primaryschoolmanagement.config.JwtProperties;
import com.example.primaryschoolmanagement.dao.RoleDao;
import com.example.primaryschoolmanagement.dao.UserDao;
import com.example.primaryschoolmanagement.dao.UserRoleDao;
import com.example.primaryschoolmanagement.dto.LoginRequest;
import com.example.primaryschoolmanagement.dto.common.PageResult;
import com.example.primaryschoolmanagement.dto.user.*;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Role;
import com.example.primaryschoolmanagement.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Resource
    private UserRoleDao userRoleDao;

    @Resource
    private RoleDao roleDao;

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
            log.error("更新密码失败, userId={}", user.getId());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "更新密码失败");
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

    // ========== 新增用户管理方法实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO createUser(UserCreateRequest request) {
        log.info("创建用户：username={}", request.getUsername());

        // 1. 检查用户名是否已存在
        LambdaQueryWrapper<AppUser> usernameQuery = new LambdaQueryWrapper<>();
        usernameQuery.eq(AppUser::getUsername, request.getUsername());
        if (userRepo.selectCount(usernameQuery) > 0) {
            throw new DuplicateException("用户名", request.getUsername());
        }

        // 2. 检查手机号是否已存在（如果提供）
        if (StringUtils.hasText(request.getPhone())) {
            LambdaQueryWrapper<AppUser> phoneQuery = new LambdaQueryWrapper<>();
            phoneQuery.eq(AppUser::getPhone, request.getPhone());
            if (userRepo.selectCount(phoneQuery) > 0) {
                throw new DuplicateException("手机号", request.getPhone());
            }
        }

        // 3. 检查邮箱是否已存在（如果提供）
        if (StringUtils.hasText(request.getEmail())) {
            LambdaQueryWrapper<AppUser> emailQuery = new LambdaQueryWrapper<>();
            emailQuery.eq(AppUser::getEmail, request.getEmail());
            if (userRepo.selectCount(emailQuery) > 0) {
                throw new DuplicateException("邮箱", request.getEmail());
            }
        }

        // 4. 创建用户实体
        AppUser user = new AppUser();
        BeanUtils.copyProperties(request, user);

        // 5. 密码加密
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        // 6. 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsDeleted(false);

        // 7. 保存用户
        int result = userRepo.insert(user);
        if (result <= 0) {
            throw new BusinessException("创建用户失败");
        }

        // 8. 根据用户类型分配默认角色
        assignDefaultRole(user.getId(), user.getUserType());

        log.info("用户创建成功：userId={}, username={}", user.getId(), user.getUsername());
        return convertToDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateUser(Long id, UserUpdateRequest request) {
        log.info("更新用户：userId={}", id);

        // 1. 查询用户是否存在
        AppUser user = userRepo.selectById(id);
        if (user == null || user.getIsDeleted()) {
            throw new UserNotFoundException(id);
        }

        // 2. 检查手机号唯一性（如果修改）
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            LambdaQueryWrapper<AppUser> phoneQuery = new LambdaQueryWrapper<>();
            phoneQuery.eq(AppUser::getPhone, request.getPhone())
                    .ne(AppUser::getId, id);
            if (userRepo.selectCount(phoneQuery) > 0) {
                throw new DuplicateException("手机号", request.getPhone());
            }
        }

        // 3. 检查邮箱唯一性（如果修改）
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            LambdaQueryWrapper<AppUser> emailQuery = new LambdaQueryWrapper<>();
            emailQuery.eq(AppUser::getEmail, request.getEmail())
                    .ne(AppUser::getId, id);
            if (userRepo.selectCount(emailQuery) > 0) {
                throw new DuplicateException("邮箱", request.getEmail());
            }
        }

        // 4. 更新用户信息
        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        user.setUpdatedAt(LocalDateTime.now());

        // 5. 保存更新
        int result = userRepo.updateById(user);
        if (result <= 0) {
            throw new BusinessException("更新用户失败");
        }

        // 6. 清除缓存
        evictUserCache(user.getUsername());

        log.info("用户更新成功：userId={}", id);
        return convertToDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        log.info("删除用户：userId={}", id);

        // 1. 查询用户是否存在
        AppUser user = userRepo.selectById(id);
        if (user == null || user.getIsDeleted()) {
            throw new UserNotFoundException(id);
        }

        // 2. 不允许删除超级管理员（user_type=1且id=1）
        if (user.getUserType() == 1 && user.getId() == 1L) {
            throw new BusinessException("不允许删除超级管理员");
        }

        // 3. 软删除用户
        LambdaUpdateWrapper<AppUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AppUser::getId, id)
                .set(AppUser::getIsDeleted, 1)
                .set(AppUser::getUpdatedAt, LocalDateTime.now());

        int result = userRepo.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("删除用户失败");
        }

        // 4. 删除用户角色关联
        userRoleDao.deleteByUserId(id);

        // 5. 清除缓存
        evictUserCache(user.getUsername());

        log.info("用户删除成功：userId={}", id);
    }

    @Override
    public UserDTO getUserById(Long id) {
        log.info("查询用户详情：userId={}", id);

        AppUser user = userRepo.selectById(id);
        if (user == null || user.getIsDeleted()) {
            throw new UserNotFoundException(id);
        }

        return convertToDTO(user);
    }

    @Override
    public PageResult<UserDTO> queryUsers(UserQueryRequest request) {
        log.info("分页查询用户：request={}", request);

        // 1. 构建查询条件
        LambdaQueryWrapper<AppUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AppUser::getIsDeleted, 0);

        if (StringUtils.hasText(request.getUsername())) {
            queryWrapper.like(AppUser::getUsername, request.getUsername());
        }
        if (StringUtils.hasText(request.getRealName())) {
            queryWrapper.like(AppUser::getRealName, request.getRealName());
        }
        if (request.getUserType() != null) {
            queryWrapper.eq(AppUser::getUserType, request.getUserType());
        }
        if (request.getStatus() != null) {
            queryWrapper.eq(AppUser::getStatus, request.getStatus());
        }
        if (StringUtils.hasText(request.getPhone())) {
            queryWrapper.like(AppUser::getPhone, request.getPhone());
        }
        if (StringUtils.hasText(request.getEmail())) {
            queryWrapper.like(AppUser::getEmail, request.getEmail());
        }

        // 2. 按创建时间降序排序
        queryWrapper.orderByDesc(AppUser::getCreatedAt);

        // 3. 执行分页查询
        Page<AppUser> page = new Page<>(request.getPage(), request.getSize());
        Page<AppUser> result = userRepo.selectPage(page, queryWrapper);

        // 4. 转换为DTO
        List<UserDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), dtoList, request.getPage(), request.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        log.info("分配角色给用户：userId={}, roleIds={}", userId, roleIds);

        // 1. 检查用户是否存在
        AppUser user = userRepo.selectById(userId);
        if (user == null || user.getIsDeleted()) {
            throw new UserNotFoundException(userId);
        }

        // 2. 检查角色是否都存在
        for (Long roleId : roleIds) {
            Role role = roleDao.selectById(roleId);
            if (role == null || role.getIsDeleted()) {
                throw new RoleNotFoundException(roleId);
            }
        }

        // 3. 删除用户原有的角色
        userRoleDao.deleteByUserId(userId);

        // 4. 插入新的角色关联
        for (Long roleId : roleIds) {
            userRoleDao.insertUserRole(userId, roleId);
        }

        log.info("角色分配成功：userId={}", userId);
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        log.info("查询用户角色：userId={}", userId);

        // 1. 查询用户的所有角色ID
        List<Long> roleIds = userRoleDao.selectRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 查询角色信息
        List<Role> roles = roleDao.selectBatchIds(roleIds);

        // 3. 返回角色编码列表
        return roles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户类型分配默认角色
     */
    private void assignDefaultRole(Long userId, Integer userType) {
        String defaultRoleCode;
        switch (userType) {
            case 1 -> defaultRoleCode = "super_admin";  // 管理员
            case 2 -> defaultRoleCode = "teacher";      // 教师
            case 3 -> defaultRoleCode = "student";      // 学生
            case 4 -> defaultRoleCode = "student";      // 家长（暂时默认为student）
            default -> {
                log.warn("未知的用户类型：{}", userType);
                return;
            }
        }

        // 查询角色
        LambdaQueryWrapper<Role> roleQuery = new LambdaQueryWrapper<>();
        roleQuery.eq(Role::getRoleCode, defaultRoleCode);
        Role role = roleDao.selectOne(roleQuery);

        if (role != null) {
            userRoleDao.insertUserRole(userId, role.getId().longValue());
            log.info("已分配默认角色：userId={}, roleCode={}", userId, defaultRoleCode);
        } else {
            log.warn("默认角色不存在：roleCode={}", defaultRoleCode);
        }
    }

    /**
     * 将User实体转换为DTO
     */
    private UserDTO convertToDTO(AppUser user) {
        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .userType(user.getUserType())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .email(user.getEmail())
                .gender(user.getGender())
                .status(user.getStatus())
                .lastLoginTime(user.getLastLoginTime())
                .lastLoginIp(user.getLastLoginIp())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        // 查询用户角色
        List<String> roles = getUserRoles(user.getId());
        dto.setRoles(roles);

        return dto;
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
