package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.exception.BusinessException;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dao.MenuDao;
import com.example.primaryschoolmanagement.dto.common.PageResult;
import com.example.primaryschoolmanagement.dto.menu.MenuCreateRequest;
import com.example.primaryschoolmanagement.dto.menu.MenuDTO;
import com.example.primaryschoolmanagement.dto.menu.MenuUpdateRequest;
import com.example.primaryschoolmanagement.entity.Menu;
import com.example.primaryschoolmanagement.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Slf4j
@Service
public class MenuServiceImpl extends ServiceImpl<MenuDao, Menu> implements MenuService {

    @Autowired
    private MenuDao menuDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuDTO createMenu(MenuCreateRequest request) {
        log.info("创建菜单：menuName={}, menuCode={}", request.getMenuName(), request.getMenuCode());

        // 1. 检查菜单编码唯一性（如果提供了菜单编码）
        if (StringUtils.hasText(request.getMenuCode())) {
            int count = menuDao.countByMenuCode(request.getMenuCode());
            if (count > 0) {
                throw new BusinessException("菜单编码已存在：" + request.getMenuCode());
            }
        }

        // 2. 如果父菜单不为0，检查父菜单是否存在
        if (request.getParentId() != 0) {
            Menu parentMenu = menuDao.selectById(request.getParentId());
            if (parentMenu == null || parentMenu.getIsDeleted()) {
                throw new BusinessException("父菜单不存在：" + request.getParentId());
            }
        }

        // 3. 构建菜单实体
        Menu menu = new Menu();
        BeanUtils.copyProperties(request, menu);

        // 4. 设置默认值
        if (menu.getSortOrder() == null) {
            menu.setSortOrder(0);
        }
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());
        menu.setIsDeleted(false);

        // 5. 保存菜单
        int result = menuDao.insert(menu);
        if (result <= 0) {
            throw new BusinessException("创建菜单失败");
        }

        log.info("菜单创建成功：menuId={}, menuCode={}", menu.getId(), menu.getMenuCode());

        // 6. 创建子菜单
        MenuDTO menuDTO = convertToDTO(menu);
        if (request.getChildren() != null && !request.getChildren().isEmpty()) {
            List<MenuDTO> childrenDTOs = new ArrayList<>();
            for (MenuCreateRequest childRequest : request.getChildren()) {
                // 设置父菜单ID
                childRequest.setParentId(menu.getId());
                // 递归创建子菜单
                MenuDTO childDTO = createMenu(childRequest);
                childrenDTOs.add(childDTO);
            }
            menuDTO.setChildren(childrenDTOs);
        }

        return menuDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuDTO updateMenu(Long id, MenuUpdateRequest request) {
        log.info("更新菜单：menuId={}", id);

        // 1. 查询菜单是否存在
        Menu menu = menuDao.selectById(id);
        if (menu == null || menu.getIsDeleted()) {
            throw new BusinessException("菜单不存在：" + id);
        }

        // 2. 如果修改了父菜单，需要检查
        if (request.getParentId() != null) {
            // 不能将菜单的父菜单设置为自己
            if (request.getParentId().equals(id)) {
                throw new BusinessException("不能将菜单的父菜单设置为自己");
            }

            // 如果父菜单不为0，检查父菜单是否存在
            if (request.getParentId() != 0) {
                Menu parentMenu = menuDao.selectById(request.getParentId());
                if (parentMenu == null || parentMenu.getIsDeleted()) {
                    throw new BusinessException("父菜单不存在：" + request.getParentId());
                }

                // 不能将菜单的父菜单设置为自己的子菜单
                if (isChildMenu(id, request.getParentId())) {
                    throw new BusinessException("不能将菜单的父菜单设置为自己的子菜单");
                }
            }
        }

        // 3. 更新菜单信息
        if (request.getParentId() != null) {
            menu.setParentId(request.getParentId());
        }
        if (StringUtils.hasText(request.getMenuName())) {
            menu.setMenuName(request.getMenuName());
        }
        if (request.getMenuType() != null) {
            menu.setMenuType(request.getMenuType());
        }
        if (request.getRoutePath() != null) {
            menu.setRoutePath(request.getRoutePath());
        }
        if (request.getComponentPath() != null) {
            menu.setComponentPath(request.getComponentPath());
        }
        if (request.getPermission() != null) {
            menu.setPermission(request.getPermission());
        }
        if (request.getIcon() != null) {
            menu.setIcon(request.getIcon());
        }
        if (request.getSortOrder() != null) {
            menu.setSortOrder(request.getSortOrder());
        }
        if (request.getRemark() != null) {
            menu.setRemark(request.getRemark());
        }
        menu.setUpdatedAt(LocalDateTime.now());

        // 4. 保存更新
        int result = menuDao.updateById(menu);
        if (result <= 0) {
            throw new BusinessException("更新菜单失败");
        }

        log.info("菜单更新成功：menuId={}", id);

        // 5. 处理子菜单的批量更新
        MenuDTO menuDTO = convertToDTO(menu);
        if (request.getChildren() != null) {
            // 获取现有的子菜单
            List<Menu> existingChildren = menuDao.selectByParentId(id);
            List<Long> existingChildIds = existingChildren.stream()
                    .map(Menu::getId)
                    .collect(Collectors.toList());

            List<MenuDTO> childrenDTOs = new ArrayList<>();
            List<Long> updatedChildIds = new ArrayList<>();

            // 处理请求中的子菜单
            for (MenuUpdateRequest childRequest : request.getChildren()) {
                if (childRequest.getId() != null) {
                    // 更新现有子菜单
                    MenuDTO childDTO = updateMenu(childRequest.getId(), childRequest);
                    childrenDTOs.add(childDTO);
                    updatedChildIds.add(childRequest.getId());
                } else {
                    // 新增子菜单
                    MenuCreateRequest createRequest = new MenuCreateRequest();
                    BeanUtils.copyProperties(childRequest, createRequest);
                    createRequest.setParentId(id);
                    MenuDTO childDTO = createMenu(createRequest);
                    childrenDTOs.add(childDTO);
                }
            }

            // 删除未在请求中的子菜单
            for (Long existingChildId : existingChildIds) {
                if (!updatedChildIds.contains(existingChildId)) {
                    deleteMenu(existingChildId);
                }
            }

            menuDTO.setChildren(childrenDTOs);
        }

        return menuDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteMenu(Long id) {
        log.info("删除菜单：menuId={}", id);

        // 1. 查询菜单是否存在
        Menu menu = menuDao.selectById(id);
        if (menu == null || menu.getIsDeleted()) {
            throw new BusinessException("菜单不存在：" + id);
        }

        // 2. 递归删除所有子菜单
        List<Menu> children = menuDao.selectByParentId(id);
        if (!children.isEmpty()) {
            log.info("菜单{}有{}个子菜单，将执行级联删除", id, children.size());
            for (Menu child : children) {
                deleteMenu(child.getId());
            }
        }

        // 3. 删除当前菜单
        int i = menuDao.deleteById(id);
        if (i > 0) {
            log.info("菜单删除成功：menuId={}", id);
            return true;
        } else {
            log.error("菜单删除失败：menuId={}", id);
            return false;
        }
    }

    @Override
    public MenuDTO getMenuById(Long id)  {
        log.info("查询菜单详情：menuId={}", id);

        Menu menu = menuDao.selectById(id);
        if (menu == null || menu.getIsDeleted()) {
            throw new BusinessException("菜单不存在：" + id);
        }

        MenuDTO menuDTO = convertToDTO(menu);

        // 加载子菜单
        List<Menu> children = menuDao.selectByParentId(id);
        if (!children.isEmpty()) {
            List<MenuDTO> childrenDTOs = children.stream()
                    .map(this::convertToDTOWithChildren)
                    .collect(Collectors.toList());
            menuDTO.setChildren(childrenDTOs);
        }

        return menuDTO;
    }

    @Override
    public List<MenuDTO> getMenuTree() {
        log.info("查询菜单树");

        // 1. 查询所有菜单
        List<Menu> allMenus = menuDao.selectAllMenus();

        // 2. 转换为DTO
        List<MenuDTO> allMenuDTOs = allMenus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 3. 构建树形结构
        return buildMenuTree(allMenuDTOs, 0L);
    }

    @Override
    public List<MenuDTO> getAllMenus() {
        log.info("查询所有菜单列表");

        List<Menu> allMenus = menuDao.selectAllMenus();
        return allMenus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public R getAllMenusWithPagination(int page, int size) {
        log.info("分页查询所有菜单列表，page={}, size={}", page, size);

        // 1. 查询所有未删除的菜单
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getIsDeleted, false)
                .orderByAsc(Menu::getSortOrder, Menu::getId);
        List<Menu> allMenus = menuDao.selectList(queryWrapper);

        // 2. 转换为DTO并构建树形结构
        List<MenuDTO> allMenuDTOs = allMenus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        List<MenuDTO> menuTree = buildMenuTree(allMenuDTOs, 0L);

        // 3. 对树形结构进行分页
        int total = menuTree.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);

        List<MenuDTO> pagedMenuTree = new ArrayList<>();
        if (fromIndex < total) {
            pagedMenuTree = menuTree.subList(fromIndex, toIndex);
        }

        // 4. 构建分页结果
        PageResult<MenuDTO> pageResult = PageResult.of(
                (long) total,
                pagedMenuTree,
                page,
                size
        );

        return R.ok(pageResult);
    }

    /**
     * 构建菜单树形结构
     * @param allMenus 所有菜单列表
     * @param parentId 父菜单ID
     * @return 菜单树
     */
    private List<MenuDTO> buildMenuTree(List<MenuDTO> allMenus, Long parentId) {
        List<MenuDTO> tree = new ArrayList<>();

        for (MenuDTO menu : allMenus) {
            if (menu.getParentId().equals(parentId)) {
                // 递归查找子菜单
                List<MenuDTO> children = buildMenuTree(allMenus, menu.getId());
                menu.setChildren(children.isEmpty() ? null : children);
                tree.add(menu);
            }
        }

        return tree;
    }

    /**
     * 检查目标菜单是否是当前菜单的子菜单（递归检查）
     * @param currentMenuId 当前菜单ID
     * @param targetMenuId 目标菜单ID
     * @return true-是子菜单，false-不是子菜单
     */
    private boolean isChildMenu(Long currentMenuId, Long targetMenuId) {
        Menu targetMenu = menuDao.selectById(targetMenuId);
        if (targetMenu == null || targetMenu.getIsDeleted()) {
            return false;
        }

        if (targetMenu.getParentId().equals(currentMenuId)) {
            return true;
        }

        if (targetMenu.getParentId() == 0) {
            return false;
        }

        // 递归检查
        return isChildMenu(currentMenuId, targetMenu.getParentId());
    }

    /**
     * 将Menu实体转换为DTO
     */
    private MenuDTO convertToDTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(menu, dto);
        return dto;
    }

    /**
     * 将Menu实体转换为DTO，并递归加载子菜单
     */
    private MenuDTO convertToDTOWithChildren(Menu menu) {
        MenuDTO dto = convertToDTO(menu);

        // 递归加载子菜单
        List<Menu> children = menuDao.selectByParentId(menu.getId());
        if (!children.isEmpty()) {
            List<MenuDTO> childrenDTOs = children.stream()
                    .map(this::convertToDTOWithChildren)
                    .collect(Collectors.toList());
            dto.setChildren(childrenDTOs);
        }

        return dto;
    }
}
