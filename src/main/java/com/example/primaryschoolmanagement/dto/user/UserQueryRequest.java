package com.example.primaryschoolmanagement.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户查询条件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryRequest {

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 真实姓名（模糊查询）
     */
    private String realName;

    /**
     * 用户类型：1-管理员 2-教师 3-学生 4-家长
     */
    private Integer userType;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 手机号（模糊查询）
     */
    private String phone;

    /**
     * 邮箱（模糊查询）
     */
    private String email;

    /**
     * 页码，默认1
     */
    private Integer page = 1;

    /**
     * 每页条数，默认10
     */
    private Integer size = 10;
}
