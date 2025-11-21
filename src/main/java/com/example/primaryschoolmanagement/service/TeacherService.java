package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.dto.TeacherDTO;
import com.example.primaryschoolmanagement.dto.TeacherQueryDTO;
import com.example.primaryschoolmanagement.dto.subjectteacherDTO;
import com.example.primaryschoolmanagement.entity.*;
import com.example.primaryschoolmanagement.common.utils.R;

public interface TeacherService extends IService<Teacher>{

    R teacherList();//查询未删除的
    R queryByConditions(TeacherQueryDTO teacherQueryDTO);
    R getTeacherById(Integer id);
    int addTeacher(TeacherDTO teacherDTO);
    R deleteTeacher(Integer id);
    R updateTeacher(TeacherDTO teacherDTO);

    /**
     * 根据科目ID获取能教该科目的教师列表
     * @param subjectId 科目ID
     * @return 教师列表
     */
    R getTeachersBySubjectId(Long subjectId);
}
