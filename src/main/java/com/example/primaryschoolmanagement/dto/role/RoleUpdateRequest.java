package com.example.primaryschoolmanagement.dto.role;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新角色请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {

    /**
     * 角色名称
     */
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    /**
     * 角色编码
     */
    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    private String roleCode;

    /**
     * 角色描述
     */
    @Size(max = 200, message = "角色描述长度不能超过200个字符")
    private String roleDesc;

    /**
     * 显示排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    @Min(value = 0, message = "状态必须为0或1")
    @Max(value = 1, message = "状态必须为0或1")
    private Integer status;
}
