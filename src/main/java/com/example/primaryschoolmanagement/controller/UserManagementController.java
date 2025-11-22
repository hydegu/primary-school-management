package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.common.PageResult;
import com.example.primaryschoolmanagement.dto.user.*;
import com.example.primaryschoolmanagement.service.FileStorageService;
import com.example.primaryschoolmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 创建用户
     * 权限要求：超级管理员
     * 支持文件上传（multipart/form-data）或 JSON（application/json）
     */
    @PreAuthorize("hasRole('super_admin')")
    @PostMapping(consumes = {"multipart/form-data", "application/json"})
    public R createUser(
            @RequestParam(required = false) MultipartFile avatarFile,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String realName,
            @RequestParam Integer userType,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) Integer status
    ) {
        log.info("创建用户请求：username={}", username);

        try {
            // 构建请求对象
            UserCreateRequest request = UserCreateRequest.builder()
                    .username(username)
                    .password(password)
                    .realName(realName)
                    .userType(userType)
                    .phone(phone)
                    .email(email)
                    .gender(gender)
                    .status(status)
                    .build();

            // 先创建用户（不带头像）
            UserDTO user = userService.createUser(request);

            // 如果上传了头像文件，保存文件并更新用户头像
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarUrl = fileStorageService.storeAvatar(avatarFile, user.getId());
                log.info("头像文件上传成功：userId={}, url={}", user.getId(), avatarUrl);

                // 更新用户头像
                UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                        .avatar(avatarUrl)
                        .build();
                user = userService.updateUser(user.getId(), updateRequest);
            }

            return R.ok(user);

        } catch (IllegalArgumentException e) {
            log.warn("创建用户失败：{}", e.getMessage());
            return R.er(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建用户异常", e);
            return R.er(500, "创建用户失败");
        }
    }

    /**
     * 更新用户
     * 权限要求：超级管理员
     * 支持文件上传（multipart/form-data）
     */
    @PreAuthorize("hasRole('super_admin')")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data", "application/json"})
    public R updateUser(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile avatarFile,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) Integer status
    ) {
        log.info("更新用户请求：userId={}", id);

        try {
            // 构建请求对象
            UserUpdateRequest request = UserUpdateRequest.builder()
                    .username(username)
                    .password(password)
                    .realName(realName)
                    .phone(phone)
                    .email(email)
                    .gender(gender)
                    .status(status)
                    .build();

            // 如果上传了头像文件，先保存文件
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarUrl = fileStorageService.storeAvatar(avatarFile, id);
                request.setAvatar(avatarUrl);
                log.info("头像文件上传成功：userId={}, url={}", id, avatarUrl);
            }

            // 更新用户
            UserDTO user = userService.updateUser(id, request);
            return R.ok(user);

        } catch (IllegalArgumentException e) {
            log.warn("更新用户失败：{}", e.getMessage());
            return R.er(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新用户异常", e);
            return R.er(500, "更新用户失败");
        }
    }

    /**
     * 删除用户
     * 权限要求：超级管理员
     */
    @PreAuthorize("hasRole('super_admin')")
    @DeleteMapping("/{id}")
    public R deleteUser(@PathVariable Long id) {
        log.info("删除用户请求：userId={}", id);
        userService.deleteUser(id);
        return R.ok("删除成功");
    }

    /**
     * 获取用户详情
     * 权限要求：任何认证用户
     */
    @GetMapping("/{id}")
    public R getUserById(@PathVariable Long id) {
        log.info("查询用户详情：userId={}", id);
        UserDTO user = userService.getUserById(id);
        return R.ok(user);
    }

    /**
     * 分页查询用户列表
     * 权限要求：任何认证用户
     */
    @GetMapping
    public R queryUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("分页查询用户列表：page={}, size={}", page, size);

        UserQueryRequest request = UserQueryRequest.builder()
                .username(username)
                .realName(realName)
                .userType(userType)
                .status(status)
                .phone(phone)
                .email(email)
                .page(page)
                .size(size)
                .build();

        PageResult<UserDTO> result = userService.queryUsers(request);
        return R.ok(result);
    }

    /**
     * 分配角色给用户
     * 权限要求：超级管理员
     */
    @PreAuthorize("hasRole('super_admin')")
    @PostMapping("/{id}/roles")
    public R assignRoles(@PathVariable Long id, @Valid @RequestBody UserRoleUpdateRequest request) {
        log.info("分配角色请求：userId={}, roleIds={}", id, request.getRoleIds());
        if(id == 1){
            return R.er(ResultCode.BAD_REQUEST.getCode(),"无法为超级管理员作角色更改");
        }
        userService.assignRolesToUser(id, request.getRoleIds());
        return R.ok("角色分配成功");
    }

    /**
     * 获取用户的角色列表
     * 权限要求：任何认证用户
     */
    @GetMapping("/{id}/roles")
    public R getUserRoles(@PathVariable Long id) {
        log.info("查询用户角色：userId={}", id);
        List<String> roles = userService.getUserRoles(id);
        return R.ok(roles);
    }
}
