package com.example.primaryschoolmanagement.common.exception;

/**
 * 业务异常（如删除有关联数据的角色）
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
