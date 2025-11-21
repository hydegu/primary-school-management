package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.role.RoleCreateRequest;
import com.example.primaryschoolmanagement.dto.role.RoleDTO;
import com.example.primaryschoolmanagement.dto.role.RoleUpdateRequest;
import com.example.primaryschoolmanagement.dto.user.UserDTO;
import com.example.primaryschoolmanagement.service.RoleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 创建角色
     * 权限要求：超级管理员
     */
    @PreAuthorize("hasRole('super_admin')")
    @PostMapping
    public R createRole(@Valid @RequestBody RoleCreateRequest request) {
        log.info("创建角色请求：roleCode={}", request.getRoleCode());
        RoleDTO role = roleService.createRole(request);
        return R.ok(role);
    }

    /**
     * 更新角色
     * 权限要求：超级管理员
     */
    @PreAuthorize("hasRole('super_admin')")
    @PutMapping("/{id}")
    public R updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        log.info("更新角色请求：roleId={}", id);
        if(id == 1){
            return R.er(ResultCode.BAD_REQUEST.getCode(),"无法修改超级管理员角色");
        }
        RoleDTO role = roleService.updateRole(id, request);
        return R.ok(role);
    }

    /**
     * 删除角色
     * 权限要求：超级管理员
     */
    @PreAuthorize("hasRole('super_admin')")
    @DeleteMapping("/{id}")
    public R deleteRole(@PathVariable Long id) {
        log.info("删除角色请求：roleId={}", id);
        if(id == 1){
            return R.er(ResultCode.BAD_REQUEST.getCode(),"无法删除超级管理员角色");
        }
        roleService.deleteRole(id);
        return R.ok("删除成功");
    }

    /**
     * 获取角色详情
     * 权限要求：任何认证用户
     */
    @GetMapping("/{id}")
    public R getRoleById(@PathVariable Long id) {
        log.info("查询角色详情：roleId={}", id);
        RoleDTO role = roleService.getRoleById(id);
        return R.ok(role);
    }

    /**
     * 获取所有角色列表
     * 权限要求：任何认证用户
     */
    @GetMapping
    public R getAllRoles() {
        log.info("查询所有角色列表");
        List<RoleDTO> roles = roleService.getAllRoles();
        return R.ok(roles);
    }

    /**
     * 获取角色下的用户列表
     * 权限要求：任何认证用户
     */
    @GetMapping("/{id}/users")
    public R getRoleUsers(@PathVariable Long id) {
        log.info("查询角色下的用户列表：roleId={}", id);
        List<UserDTO> users = roleService.getRoleUsers(id);
        return R.ok(users);
    }
}
