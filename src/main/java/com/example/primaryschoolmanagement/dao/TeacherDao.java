package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeacherDao extends BaseMapper<Teacher> {

    /**
     * 根据科目ID查询能教该科目的教师列表
     * @param subjectId 科目ID
     * @return 教师列表
     */
    @Select("""
        SELECT t.*
        FROM edu_teacher t
        INNER JOIN edu_subject_teacher st ON t.id = st.teacher_id
        WHERE st.subject_id = #{subjectId}
          AND t.is_deleted = 0
    """)
    List<Teacher> findTeachersBySubjectId(Long subjectId);

}
