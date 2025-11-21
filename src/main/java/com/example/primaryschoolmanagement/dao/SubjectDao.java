package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Subject;
import org.apache.ibatis.annotations.Mapper;

/**
 * 科目数据访问层
 */
@Mapper
public interface SubjectDao extends BaseMapper<Subject> {
}
