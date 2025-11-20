package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.Student;
import com.example.primaryschoolmanagement.entity.Teacher;

import java.util.List;

public interface ClassesService extends IService<Classes> {
    R classesList();
    R addclasses(Classes classes);
    R deleteclasses(Integer id);
    R updateclasses(Classes classes);
    //班级详情
    R getclassById(Integer id);
    //班级学生列表   根据class_id 查询
    List<Student> classStudent(Integer id);
    //分配班主任
    Teacher classheadteacher(Integer id);
}
