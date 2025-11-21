package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.dto.SubjectTeacherRelationDTO;
import com.example.primaryschoolmanagement.entity.Course;
import com.example.primaryschoolmanagement.vo.CourseVO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
public interface CourseDao extends BaseMapper<Course> {

    @Select("""
        select a.*,b.subject_name,c.class_name,d.teacher_name 
        from edu_course a,edu_subject b,edu_class c,edu_teacher d
        where a.id = #{id} 
          and b.id = a.subject_id 
          and c.id = a.class_id
        and d.id = a.teacher_id
""")
    CourseVO getCourse(Integer id);
    @Select("""
     select a.*,b.subject_name,c.class_name,d.teacher_name
        from edu_course a
        left join edu_subject b on a.subject_id = b.id
        left join edu_class c on a.class_id = c.id
        left join edu_teacher d on a.teacher_id = d.id
        where  a.subject_id = #{subjectId}
""")
    List<CourseVO> courseList(Integer subjectId);

    @Select("""
     select a.*,b.subject_name,c.class_name,d.teacher_name
        from edu_course a
        left join edu_subject b on a.subject_id = b.id
        left join edu_class c on a.class_id = c.id
        left join edu_teacher d on a.teacher_id = d.id
""")
    List<CourseVO> courseListAll();

    /**
     * 带筛选条件查询课程列表（subjectId和classId可选）
     */
    @Select("""
    <script>
    SELECT a.*, b.subject_name, c.class_name, d.teacher_name
    FROM edu_course a
    LEFT JOIN edu_subject b ON a.subject_id = b.id
    LEFT JOIN edu_class c ON a.class_id = c.id
    LEFT JOIN edu_teacher d ON a.teacher_id = d.id
    WHERE 1=1
    <if test="subjectId != null">
        AND a.subject_id = #{subjectId}
    </if>
    <if test="classId != null">
        AND a.class_id = #{classId}
    </if>
    ORDER BY a.id DESC
    </script>
    """)
    List<CourseVO> courseListWithFilters(@Param("subjectId") Integer subjectId, @Param("classId") Integer classId);

    @Insert("""
        insert into edu_subject_teacher(subject_id,teacher_id)
        values(#{dto.subjectId},#{dto.teacherId})
""")
    int addCourse(@Param("dto")SubjectTeacherRelationDTO dto);

}
