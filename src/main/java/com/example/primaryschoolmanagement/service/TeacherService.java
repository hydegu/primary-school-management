package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.entity.*;
import com.example.primaryschoolmanagement.common.utils.R;

public interface TeacherService extends IService<Teacher>{

    R teacherList();
    R queryByConditions(String teacherName, String teacherNo, String title);
    R getTeacherById(Integer id);
    R addTeacher(Teacher teacher, AppUser appuser, UserRole userrole);
    R deleteTeacher(Teacher teacher, AppUser appuser,UserRole userrole);
    R updateTeacher(Teacher teacher,AppUser appuser,UserRole userrole);
    R getcrouseByteacherId(Integer id);
}
