package com.example.primaryschoolmanagement.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建用户请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    /**
     * 登录账号，3-50个字符，唯一
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码，6-20个字符
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    /**
     * 真实姓名，1-50个字符
     */
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    /**
     * 用户类型：1-管理员 2-教师 3-学生 4-家长
     */
    @NotNull(message = "用户类型不能为空")
    @Min(value = 1, message = "用户类型必须在1-4之间")
    @Max(value = 4, message = "用户类型必须在1-4之间")
    private Integer userType;

    /**
     * 头像URL
     */
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;

    /**
     * 联系电话，11位手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱地址
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 性别：1-男 2-女
     */
    @Min(value = 1, message = "性别必须为1或2")
    @Max(value = 2, message = "性别必须为1或2")
    private Integer gender;

    /**
     * 状态：0-禁用 1-启用，默认1
     */
    private Integer status = 1;
}
