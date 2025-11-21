package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.CourseChange;
import org.apache.ibatis.annotations.Mapper;

/**
 * 调课申请数据访问层（仅使用 MyBatis-Plus 默认方法）
 */
@Mapper
public interface CourseChangeMapper extends BaseMapper<CourseChange> {

}