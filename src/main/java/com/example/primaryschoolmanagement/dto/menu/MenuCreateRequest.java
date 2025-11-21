package com.example.primaryschoolmanagement.dto.menu;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建菜单请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuCreateRequest {

    /**
     * 父菜单ID，0表示顶级菜单
     */
    @NotNull(message = "父菜单ID不能为空")
    private Long parentId;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称最长50个字符")
    private String menuName;

    /**
     * 菜单编码
     */
    @Size(max = 50, message = "菜单编码最长50个字符")
    private String menuCode;

    /**
     * 菜单类型：1-目录 2-菜单 3-按钮
     */
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    /**
     * 路由地址
     */
    @Size(max = 200, message = "路由地址最长200个字符")
    private String routePath;

    /**
     * 组件路径
     */
    @Size(max = 200, message = "组件路径最长200个字符")
    private String componentPath;

    /**
     * 权限标识
     */
    @Size(max = 100, message = "权限标识最长100个字符")
    private String permission;

    /**
     * 菜单图标
     */
    @Size(max = 100, message = "菜单图标最长100个字符")
    private String icon;

    /**
     * 显示排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注最长500个字符")
    private String remark;

    /**
     * 子菜单列表
     */
    @Valid
    private List<MenuCreateRequest> children;
}
