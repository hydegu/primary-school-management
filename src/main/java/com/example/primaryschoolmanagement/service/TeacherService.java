package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.common.utils.R;

public interface TeacherService extends IService<Teacher> {

    R teacherList();
    R queryByConditions(String teacherName, String teacherNo, String title);
    R teacherNo(String teacher_no);
    R teacherName(String teacherName);
    R title(String title);
    R addTeacher(Teacher teacher);
    R deleteTeacher(Integer id);
    R updateTeacher(Teacher teacher);
}
