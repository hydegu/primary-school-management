package com.example.primaryschoolmanagement.common.exception;

/**
 * 菜单未找到异常
 */
public class MenuNotFoundException extends RuntimeException {

    public MenuNotFoundException(String message) {
        super(message);
    }

    public MenuNotFoundException(Long menuId) {
        super("菜单不存在：ID = " + menuId);
    }
}
