package com.example.primaryschoolmanagement.dto.teacher;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 修改教师请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherUpdateRequest {

    /**
     * 教师编号
     */
    @Size(max = 50, message = "教师编号长度不能超过50个字符")
    private String teacherNo;

    /**
     * 教师姓名
     */
    @Size(max = 50, message = "教师姓名长度不能超过50个字符")
    private String teacherName;

    /**
     * 性别：1-男 2-女
     */
    @Min(value = 1, message = "性别必须为1或2")
    @Max(value = 2, message = "性别必须为1或2")
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 身份证号
     */
    @Size(max = 18, message = "身份证号长度不能超过18个字符")
    private String idCard;

    /**
     * 联系电话
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 职称（如：班主任/语文老师）
     */
    @Size(max = 50, message = "职称长度不能超过50个字符")
    private String title;

    /**
     * 入职日期
     */
    private LocalDate hireDate;

    /**
     * 教授的科目ID列表
     */
    private List<Long> subjectIds;

    /**
     * 班级ID列表（如果是班主任）
     */
    private List<Long> classIds;
}
