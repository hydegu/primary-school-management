package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.dto.role.RoleCreateRequest;
import com.example.primaryschoolmanagement.dto.role.RoleDTO;
import com.example.primaryschoolmanagement.dto.role.RoleUpdateRequest;
import com.example.primaryschoolmanagement.dto.user.UserDTO;
import com.example.primaryschoolmanagement.entity.Role;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务接口
 */
@Service
public interface RoleService extends IService<Role> {

    /**
     * 创建角色
     * @param request 创建角色请求
     * @return 角色信息DTO
     */
    RoleDTO createRole(RoleCreateRequest request);

    /**
     * 更新角色
     * @param id 角色ID
     * @param request 更新角色请求
     * @return 角色信息DTO
     */
    RoleDTO updateRole(Long id, RoleUpdateRequest request);

    /**
     * 删除角色（需检查是否有用户关联）
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 根据ID获取角色详情
     * @param id 角色ID
     * @return 角色信息DTO
     */
    RoleDTO getRoleById(Long id);

    /**
     * 获取所有角色列表
     * @return 角色列表
     */
    List<RoleDTO> getAllRoles();

    /**
     * 获取角色下的用户列表
     * @param roleId 角色ID
     * @return 用户列表
     */
    List<UserDTO> getRoleUsers(Long roleId);
}
