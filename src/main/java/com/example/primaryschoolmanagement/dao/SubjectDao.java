package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Subject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 科目数据访问层
 */
@Mapper
public interface SubjectDao extends BaseMapper<Subject> {
    @Select("""
        select * from edu_subject where is_deleted = 0;
""")
    List<Subject> subjectList();
    @Delete("""
        delete from edu_subject_teacher 
               where  subject_id = #{id}
""")
    int deleteTeacher(Integer id);
}
