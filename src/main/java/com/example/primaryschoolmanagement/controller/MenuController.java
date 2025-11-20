package com.example.primaryschoolmanagement.controller;

import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.menu.MenuCreateRequest;
import com.example.primaryschoolmanagement.dto.menu.MenuDTO;
import com.example.primaryschoolmanagement.dto.menu.MenuUpdateRequest;
import com.example.primaryschoolmanagement.service.MenuService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取菜单树
     * 权限要求：任何认证用户
     */
    @GetMapping("/tree")
    public R getMenuTree() {
        log.info("查询菜单树");
        List<MenuDTO> menuTree = menuService.getMenuTree();
        return R.ok(menuTree);
    }

    /**
     * 获取所有菜单列表（扁平结构）
     * 权限要求：任何认证用户
     */
    @GetMapping("/list")
    public R getAllMenus(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("查询所有菜单列表，page={}, size={}", page, size);
        return menuService.getAllMenusWithPagination(page, size);
    }

    /**
     * 获取菜单详情
     * 权限要求：任何认证用户
     */
    @GetMapping("/{id}")
    public R getMenuById(@PathVariable Long id) {
        log.info("查询菜单详情：menuId={}", id);
        MenuDTO menu = menuService.getMenuById(id);
        return R.ok(menu);
    }

    /**
     * 创建菜单
     * 权限要求：超级管理员
     */
    @PreAuthorize("hasRole('super_admin')")
    @PostMapping
    public R createMenu(@Valid @RequestBody MenuCreateRequest request) {
        log.info("创建菜单请求：menuName={}", request.getMenuName());
        MenuDTO menu = menuService.createMenu(request);
        return R.ok(menu);
    }

    /**
     * 更新菜单
     * 权限要求：超级管理员
     */
    @PreAuthorize("hasRole('super_admin')")
    @PutMapping("/{id}")
    public R updateMenu(@PathVariable Long id, @Valid @RequestBody MenuUpdateRequest request) {
        log.info("更新菜单请求：menuId={}", id);
        MenuDTO menu = menuService.updateMenu(id, request);
        return R.ok(menu);
    }

    /**
     * 删除菜单
     * 权限要求：超级管理员
     */
    @PreAuthorize("hasRole('super_admin')")
    @DeleteMapping("/{id}")
    public R deleteMenu(@PathVariable Long id) {
        log.info("删除菜单请求：menuId={}", id);
        menuService.deleteMenu(id);
        return R.ok("删除成功");
    }
}
