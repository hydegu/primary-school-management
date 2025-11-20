package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.SubjectTeacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 科目教师关联数据访问层
 */
@Mapper
public interface SubjectTeacherDao extends BaseMapper<SubjectTeacher> {
}
