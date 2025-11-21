package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Classes;
import com.example.primaryschoolmanagement.entity.Student;
import com.example.primaryschoolmanagement.vo.ClassesVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
    LIMIT 0, 5
    """)
    List<Student> classStudent(Integer id);

    /**
     * 模糊搜索班级列表（关联班主任姓名）
     */
    @Select("""
    <script>
    SELECT c.*, t.teacher_name as head_teacher_name
    FROM edu_class c
    LEFT JOIN edu_teacher t ON c.head_teacher_id = t.id
    WHERE c.is_deleted = 0
    <if test="classNo != null and classNo != ''">
        AND c.class_no LIKE CONCAT('%', #{classNo}, '%')
    </if>
    <if test="className != null and className != ''">
        AND c.class_name LIKE CONCAT('%', #{className}, '%')
    </if>
    <if test="headTeacherName != null and headTeacherName != ''">
        AND t.teacher_name LIKE CONCAT('%', #{headTeacherName}, '%')
    </if>
    ORDER BY c.id DESC
    </script>
    """)
    List<ClassesVO> searchClasses(@Param("classNo") String classNo,
                                   @Param("className") String className,
                                   @Param("headTeacherName") String headTeacherName);
}
