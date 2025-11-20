package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单数据访问层
 */
@Mapper
public interface MenuDao extends BaseMapper<Menu> {

    /**
     * 查询所有未删除的菜单（用于构建树形结构）
     * @return 菜单列表
     */
    @Select("""
        SELECT *
        FROM sys_menu
        WHERE is_deleted = false
        ORDER BY sort_order ASC, id ASC
    """)
    List<Menu> selectAllMenus();

    /**
     * 根据父菜单ID查询子菜单
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    @Select("""
        SELECT *
        FROM sys_menu
        WHERE parent_id = #{parentId}
          AND is_deleted = false
        ORDER BY sort_order ASC, id ASC
    """)
    List<Menu> selectByParentId(Long parentId);

    /**
     * 查询菜单编码是否存在
     * @param menuCode 菜单编码
     * @return 菜单数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM sys_menu
        WHERE menu_code = #{menuCode}
          AND is_deleted = false
    """)
    int countByMenuCode(String menuCode);

    /**
     * 查询菜单编码是否存在（排除指定ID）
     * @param menuCode 菜单编码
     * @param excludeId 排除的菜单ID
     * @return 菜单数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM sys_menu
        WHERE menu_code = #{menuCode}
          AND id != #{excludeId}
          AND is_deleted = false
    """)
    int countByMenuCodeExcludeId(String menuCode, Long excludeId);
}
