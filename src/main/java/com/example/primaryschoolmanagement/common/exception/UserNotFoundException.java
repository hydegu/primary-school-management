package com.example.primaryschoolmanagement.common.exception;

/**
 * 用户未找到异常
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        super("用户不存在：ID = " + userId);
    }

    public UserNotFoundException(String field, String value) {
        super("用户不存在：" + field + " = " + value);
    }
}
