package com.example.primaryschoolmanagement.dao;

import com.example.primaryschoolmanagement.entity.Teacher;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public interface TeacherDao {
    List<Teacher>  TeacherList();
}
