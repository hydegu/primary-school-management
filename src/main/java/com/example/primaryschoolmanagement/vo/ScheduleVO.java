package com.example.primaryschoolmanagement.vo;

import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

/**
 * 排课信息VO（包含关联的科目、班级、教师信息）
 */
@Data
public class ScheduleVO {

    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 科目ID
     */
    private Long subjectId;

    /**
     * 星期几：1-7（1=周一，7=周日）
     */
    private Integer weekDay;

    /**
     * 第几节课：1-8
     */
    private Integer period;

    /**
     * 教室
     */
    private String classroom;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 学期
     */
    private String semester;

    /**
     * 状态：0-停用 1-启用
     */
    private Integer status;

    /**
     * 科目名称
     */
    private String subjectName;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 课程名称
     */
    private String courseName;

    private Date createdAt;
    private Date updatedAt;
}
