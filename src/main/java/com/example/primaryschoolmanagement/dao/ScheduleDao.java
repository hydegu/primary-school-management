package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Schedule;
import com.example.primaryschoolmanagement.vo.ScheduleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 排课时间表Dao
 */
@Mapper
public interface ScheduleDao extends BaseMapper<Schedule> {

    /**
     * 根据班级ID查询排课列表（返回该班级的所有课表）
     */
    @Select("""
    SELECT s.*,
           sub.subject_name,
           c.class_name,
           t.teacher_name,
           co.course_name
    FROM edu_schedule s
    LEFT JOIN edu_subject sub ON s.subject_id = sub.id
    LEFT JOIN edu_class c ON s.class_id = c.id
    LEFT JOIN edu_teacher t ON s.teacher_id = t.id
    LEFT JOIN edu_course co ON s.course_id = co.id
    WHERE s.is_deleted = 0 AND s.class_id = #{classId}
    ORDER BY s.week_day ASC, s.period ASC
    """)
    List<ScheduleVO> scheduleListByClassId(@Param("classId") Integer classId);

    /**
     * 根据ID查询排课详情
     */
    @Select("""
    SELECT s.*,
           sub.subject_name,
           c.class_name,
           t.teacher_name,
           co.course_name
    FROM edu_schedule s
    LEFT JOIN edu_subject sub ON s.subject_id = sub.id
    LEFT JOIN edu_class c ON s.class_id = c.id
    LEFT JOIN edu_teacher t ON s.teacher_id = t.id
    LEFT JOIN edu_course co ON s.course_id = co.id
    WHERE s.id = #{id} AND s.is_deleted = 0
    """)
    ScheduleVO getScheduleById(@Param("id") Long id);


}
