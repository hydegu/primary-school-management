package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.CourseSwap;
import org.apache.ibatis.annotations.Mapper;

/**
 * 换课申请数据访问层
 */
@Mapper
public interface CourseSwapMapper extends BaseMapper<CourseSwap> {
    // 使用MyBatis Plus默认方法，无需额外定义
}