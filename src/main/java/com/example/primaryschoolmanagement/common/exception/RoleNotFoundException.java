package com.example.primaryschoolmanagement.common.exception;

/**
 * 角色未找到异常
 */
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(Long roleId) {
        super("角色不存在：ID = " + roleId);
    }

    public RoleNotFoundException(String field, String value) {
        super("角色不存在：" + field + " = " + value);
    }
}
