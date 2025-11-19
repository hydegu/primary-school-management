package com.example.primaryschoolmanagement.common.exception;

/**
 * 数据重复异常（如用户名、邮箱、角色编码重复）
 */
public class DuplicateException extends RuntimeException {

    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(String field, String value) {
        super(field + "已存在：" + value);
    }
}
