package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.Student;
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
}
