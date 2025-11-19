package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.exception.*;
import com.example.primaryschoolmanagement.dao.RoleDao;
import com.example.primaryschoolmanagement.dao.UserDao;
import com.example.primaryschoolmanagement.dto.role.RoleCreateRequest;
import com.example.primaryschoolmanagement.dto.role.RoleDTO;
import com.example.primaryschoolmanagement.dto.role.RoleUpdateRequest;
import com.example.primaryschoolmanagement.dto.user.UserDTO;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Role;
import com.example.primaryschoolmanagement.service.RoleService;
import com.example.primaryschoolmanagement.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
@Slf4j
public class RoleServiceImpl extends ServiceImpl<RoleDao, Role> implements RoleService {

    @Resource
    private RoleDao roleDao;

    @Resource
    private UserDao userDao;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleDTO createRole(RoleCreateRequest request) {
        log.info("创建角色：roleCode={}", request.getRoleCode());

        // 1. 检查角色编码是否已存在
        LambdaQueryWrapper<Role> codeQuery = new LambdaQueryWrapper<>();
        codeQuery.eq(Role::getRoleCode, request.getRoleCode());
        if (roleDao.selectCount(codeQuery) > 0) {
            throw new DuplicateException("角色编码", request.getRoleCode());
        }

        // 2. 检查角色名称是否已存在
        LambdaQueryWrapper<Role> nameQuery = new LambdaQueryWrapper<>();
        nameQuery.eq(Role::getRoleName, request.getRoleName());
        if (roleDao.selectCount(nameQuery) > 0) {
            throw new DuplicateException("角色名称", request.getRoleName());
        }

        // 3. 创建角色实体
        Role role = new Role();
        BeanUtils.copyProperties(request, role);

        // 4. 设置默认值
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        if (role.getSortOrder() == null) {
            role.setSortOrder(0);
        }
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        role.setIsDeleted(false);

        // 5. 保存角色
        int result = roleDao.insert(role);
        if (result <= 0) {
            throw new BusinessException("创建角色失败");
        }

        log.info("角色创建成功：roleId={}, roleCode={}", role.getId(), role.getRoleCode());
        return convertToDTO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleDTO updateRole(Long id, RoleUpdateRequest request) {
        log.info("更新角色：roleId={}", id);

        // 1. 查询角色是否存在
        Role role = roleDao.selectById(id);
        if (role == null || role.getIsDeleted()) {
            throw new RoleNotFoundException(id);
        }

        // 2. 不允许修改super_admin角色
        if ("super_admin".equals(role.getRoleCode())) {
            throw new BusinessException("不允许修改超级管理员角色");
        }

        // 3. 检查角色名称唯一性（如果修改）
        if (StringUtils.hasText(request.getRoleName()) && !request.getRoleName().equals(role.getRoleName())) {
            LambdaQueryWrapper<Role> nameQuery = new LambdaQueryWrapper<>();
            nameQuery.eq(Role::getRoleName, request.getRoleName())
                    .ne(Role::getId, id);
            if (roleDao.selectCount(nameQuery) > 0) {
                throw new DuplicateException("角色名称", request.getRoleName());
            }
        }

        // 4. 更新角色信息
        if (StringUtils.hasText(request.getRoleName())) {
            role.setRoleName(request.getRoleName());
        }
        if (StringUtils.hasText(request.getRoleDesc())) {
            role.setRoleDesc(request.getRoleDesc());
        }
        if (request.getSortOrder() != null) {
            role.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        role.setUpdatedAt(LocalDateTime.now());

        // 5. 保存更新
        int result = roleDao.updateById(role);
        if (result <= 0) {
            throw new BusinessException("更新角色失败");
        }

        log.info("角色更新成功：roleId={}", id);
        return convertToDTO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        log.info("删除角色：roleId={}", id);

        // 1. 查询角色是否存在
        Role role = roleDao.selectById(id);
        if (role == null || role.getIsDeleted()) {
            throw new RoleNotFoundException(id);
        }

        // 2. 不允许删除super_admin角色
        if ("super_admin".equals(role.getRoleCode())) {
            throw new BusinessException("不允许删除超级管理员角色");
        }

        // 3. 检查是否有用户关联此角色
        List<Long> userIds = roleDao.selectUserIdsByRoleId(id);
        if (!userIds.isEmpty()) {
            throw new BusinessException("该角色下还有" + userIds.size() + "个用户，无法删除");
        }

        // 4. 软删除角色
        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Role::getId, id)
                .set(Role::getIsDeleted, 1)
                .set(Role::getUpdatedAt, LocalDateTime.now());

        int result = roleDao.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("删除角色失败");
        }

        log.info("角色删除成功：roleId={}", id);
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        log.info("查询角色详情：roleId={}", id);

        Role role = roleDao.selectById(id);
        if (role == null || role.getIsDeleted()) {
            throw new RoleNotFoundException(id);
        }

        return convertToDTO(role);
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        log.info("查询所有角色列表");

        // 查询所有未删除的角色
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getIsDeleted, 0)
                .orderByAsc(Role::getSortOrder)
                .orderByDesc(Role::getCreatedAt);

        List<Role> roles = roleDao.selectList(queryWrapper);

        return roles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getRoleUsers(Long roleId) {
        log.info("查询角色下的用户列表：roleId={}", roleId);

        // 1. 检查角色是否存在
        Role role = roleDao.selectById(roleId);
        if (role == null || role.getIsDeleted()) {
            throw new RoleNotFoundException(roleId);
        }

        // 2. 查询角色下的所有用户ID
        List<Long> userIds = roleDao.selectUserIdsByRoleId(roleId);
        if (userIds.isEmpty()) {
            return List.of();
        }

        // 3. 查询用户信息
        List<AppUser> users = userDao.selectBatchIds(userIds);

        // 4. 过滤未删除的用户并转换为DTO
        return users.stream()
                .filter(user -> !user.getIsDeleted())
                .map(user -> userService.getUserById(user.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 将Role实体转换为DTO
     */
    private RoleDTO convertToDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId().longValue())
                .roleName(role.getRoleName())
                .roleCode(role.getRoleCode())
                .roleDesc(role.getRoleDesc())
                .sortOrder(role.getSortOrder())
                .status(role.getStatus())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
