package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.primaryschoolmanagement.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalTime;

/**
 * 排课时间表实体类
 */
@Data
@TableName("edu_schedule")
public class Schedule extends BaseEntity {

    @TableId(type = IdType.AUTO)
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
     * 学期（如：2023-2024-1）
     */
    private String semester;

    /**
     * 状态：0-停用 1-启用
     */
    private Integer status;
}
