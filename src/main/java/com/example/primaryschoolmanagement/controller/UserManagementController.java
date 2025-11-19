package com.example.primaryschoolmanagement.controller;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     */
    @PreAuthorize("hasRole('super_admin')")
    @PostMapping
    public R createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("创建用户请求：username={}", request.getUsername());
        UserDTO user = userService.createUser(request);
        return R.ok(user);
    }

    /**
     * 更新用户
     * 权限要求：超级管理员
     */
    @PreAuthorize("hasRole('super_admin')")
    @PutMapping("/{id}")
    public R updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        log.info("更新用户请求：userId={}", id);
        UserDTO user = userService.updateUser(id, request);
        return R.ok(user);
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

    /**
     * 上传用户头像
     * 权限要求：任何认证用户（只能上传自己的头像或管理员可上传任意用户）
     */
    @PostMapping("/{id}/avatar")
    public R uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        log.info("上传用户头像：userId={}, filename={}", id, file.getOriginalFilename());

        try {
            // 1. 验证用户是否存在
            UserDTO user = userService.getUserById(id);

            // 2. 存储新头像文件
            String fileUrl = fileStorageService.storeAvatar(file, id);

            // 3. 更新用户头像URL
            UserUpdateRequest updateRequest = new UserUpdateRequest();
            updateRequest.setAvatar(fileUrl);
            UserDTO updatedUser = userService.updateUser(id, updateRequest);

            // 4. 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("user", updatedUser);

            log.info("用户头像上传成功：userId={}, url={}", id, fileUrl);
            return R.ok(result);

        } catch (IllegalArgumentException e) {
            log.warn("头像上传失败：{}", e.getMessage());
            return R.er(400, e.getMessage());
        } catch (Exception e) {
            log.error("头像上传异常", e);
            return R.er(500, "头像上传失败");
        }
    }
}
