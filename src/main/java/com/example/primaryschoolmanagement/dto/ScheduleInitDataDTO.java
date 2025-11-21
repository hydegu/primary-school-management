package com.example.primaryschoolmanagement.dto;

import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.Subject;
import com.example.primaryschoolmanagement.entity.Teacher;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 排课初始化数据DTO
 * 一次性返回所有排课所需的基础数据
 */
@Data
public class ScheduleInitDataDTO {
    /**
     * 所有科目列表
     */
    private List<Subject> subjects;

    /**
     * 所有教师列表
     */
    private List<Teacher> teachers;

    /**
     * 所有班级列表
     */
    private List<Classes> classes;

    /**
     * 科目-教师映射关系
     * key: 科目ID, value: 该科目对应的教师ID列表
     * 例如: {"1": [1, 2, 3], "2": [2, 4, 5]}
     * 表示科目1可以由教师1、2、3教授，科目2可以由教师2、4、5教授
     */
    private Map<Long, List<Long>> subjectTeacherMap;
}
