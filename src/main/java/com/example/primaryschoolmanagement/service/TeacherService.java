package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.entity.Course;
import com.example.primaryschoolmanagement.entity.Role;
import com.example.primaryschoolmanagement.entity.Teacher;
import com.example.primaryschoolmanagement.common.utils.R;

public interface TeacherService extends IService<Teacher>{

    R teacherList();
    R queryByConditions(String teacherName, String teacherNo, String title);
    R getTeacherById(Integer id);
    R addTeacher(Teacher teacher, AppUser appuser, Role role);
    R deleteTeacher(Teacher teacher, AppUser appuser);
    R updateTeacher(Teacher teacher,AppUser appuser);
    R getcrouseByteacherId(Integer id);
}
