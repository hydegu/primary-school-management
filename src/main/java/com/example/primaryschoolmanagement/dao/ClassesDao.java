package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.Student;
import com.example.primaryschoolmanagement.vo.CourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClassesDao extends BaseMapper<Classes>  {
    @Select("""
    select edu_student.*
    from edu_student
    inner join edu_class
    on edu_student.class_id=edu_class.id
    where edu_class.id = #{id}
    """)
    List<Student> classStudent(Integer id);

    @Select("""
    SELECT c.*, s.subject_name, t.teacher_name, cl.class_name
    FROM edu_course c
    LEFT JOIN edu_subject s ON c.subject_id = s.id
    LEFT JOIN edu_teacher t ON c.teacher_id = t.id
    LEFT JOIN edu_class cl ON c.class_id = cl.id
    WHERE c.class_id = #{classId}
    """)
    List<CourseVO> classCourses(Integer classId);
}
