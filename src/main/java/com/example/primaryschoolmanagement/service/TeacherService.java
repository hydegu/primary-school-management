package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.entity.*;
import com.example.primaryschoolmanagement.common.utils.R;

public interface TeacherService extends IService<Teacher>{

    R teacherList();//查询未删除的
    R queryByConditions(String teacherName, String teacherNo, String title);
    R getTeacherById(Integer id);
    R addTeacher(Teacher teacher, AppUser appuser, UserRole userrole);
    R deleteTeacher(Integer id);
    R updateTeacher(Teacher teacher,AppUser appuser,UserRole userrole,Integer id);

    /**
     * 根据科目ID获取能教该科目的教师列表
     * @param subjectId 科目ID
     * @return 教师列表
     */
    R getTeachersBySubjectId(Long subjectId);
}
