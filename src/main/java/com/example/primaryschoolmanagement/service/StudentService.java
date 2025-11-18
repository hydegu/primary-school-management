package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.entity.Student;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface StudentService extends IService<Student> {


    Student findByStudentNo(String studentNo);

    int createStudent(Student dto);

    int updateStudent(Student dto);

    int delete(Integer id);

    List<Student> list(Map<String,Object> map);

}
