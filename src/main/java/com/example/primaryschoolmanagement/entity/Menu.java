package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.primaryschoolmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 菜单实体类
 */
@Data
@TableName("sys_menu")
@Accessors(chain = true)
public class Menu extends BaseEntity {

    /**
     * 菜单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父菜单ID，0表示顶级菜单
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 菜单类型：1-目录 2-菜单 3-按钮
     */
    private Integer menuType;

    /**
     * 路由地址
     */
    private String routePath;

    /**
     * 组件路径
     */
    private String componentPath;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 显示排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}
