package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Grade;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GradeDao extends BaseMapper<Grade> {
}
