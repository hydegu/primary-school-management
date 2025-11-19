package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.dto.LoginRequest;
import com.example.primaryschoolmanagement.dto.common.PageResult;
import com.example.primaryschoolmanagement.dto.user.*;
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

    // ========== 新增用户管理方法 ==========

    /**
     * 创建用户（带密码加密）
     * @param request 创建用户请求
     * @return 用户信息DTO
     */
    UserDTO createUser(UserCreateRequest request);

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param request 更新用户请求
     * @return 用户信息DTO
     */
    UserDTO updateUser(Long id, UserUpdateRequest request);

    /**
     * 删除用户（软删除）
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 根据ID获取用户详情
     * @param id 用户ID
     * @return 用户信息DTO
     */
    UserDTO getUserById(Long id);

    /**
     * 分页查询用户列表
     * @param request 查询条件
     * @return 分页结果
     */
    PageResult<UserDTO> queryUsers(UserQueryRequest request);

    /**
     * 分配角色给用户
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 获取用户的角色列表
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getUserRoles(Long userId);

}
