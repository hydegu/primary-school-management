package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.entity.Course;
import com.example.primaryschoolmanagement.entity.Student;

import java.util.List;

public interface CourseService extends IService<Student> {

    int createCourse(Course course);

    int updateCourse(Course course);

    int deleteCourse(Integer id);

    List<Course> list(Course course);

}
