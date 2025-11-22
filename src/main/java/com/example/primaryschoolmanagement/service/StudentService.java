package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.common.utils.R;
import com.example.primaryschoolmanagement.dto.StudentDto;
import com.example.primaryschoolmanagement.dto.common.PageResult;
import com.example.primaryschoolmanagement.entity.Student;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface StudentService extends IService<Student> {


    Student findByStudentNo(String studentNo);

    int createStudent(StudentDto dto);

    boolean updateStudent(Student dto,Long id);

    int delete(Integer id);

    PageResult<Student> list(Map<String,Object> map);

}
