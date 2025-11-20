package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.dto.menu.MenuCreateRequest;
import com.example.primaryschoolmanagement.dto.menu.MenuDTO;
import com.example.primaryschoolmanagement.dto.menu.MenuUpdateRequest;
import com.example.primaryschoolmanagement.entity.Menu;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface MenuService extends IService<Menu> {

    /**
     * 创建菜单
     * @param request 创建菜单请求
     * @return 菜单信息DTO
     */
    MenuDTO createMenu(MenuCreateRequest request);

    /**
     * 更新菜单
     * @param id 菜单ID
     * @param request 更新菜单请求
     * @return 菜单信息DTO
     */
    MenuDTO updateMenu(Long id, MenuUpdateRequest request);

    /**
     * 删除菜单（软删除）
     * @param id 菜单ID
     */
    void deleteMenu(Long id);

    /**
     * 根据ID获取菜单详情
     * @param id 菜单ID
     * @return 菜单信息DTO
     */
    MenuDTO getMenuById(Long id);

    /**
     * 获取菜单树形结构
     * @return 菜单树
     */
    List<MenuDTO> getMenuTree();

    /**
     * 获取所有菜单列表（扁平结构）
     * @return 菜单列表
     */
    List<MenuDTO> getAllMenus();
}
