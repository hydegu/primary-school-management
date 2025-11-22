package com.example.primaryschoolmanagement.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 教师信息响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {

    /**
     * 教师ID
     */
    private Long id;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 教师编号
     */
    private String teacherNo;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 性别：1-男 2-女
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private Date birthDate;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 职称（如：班主任/语文老师）
     */
    private String title;

    /**
     * 入职日期
     */
    private Date hireDate;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 教授的科目ID列表
     */
    private List<Long> subjectIds;

    /**
     * 班级ID列表（如果是班主任）
     */
    private List<Long> classIds;
}
