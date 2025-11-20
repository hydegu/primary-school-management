package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.primaryschoolmanagement.entity.CourseChange;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 调课申请数据访问层
 */
@Mapper
public interface CourseChangeMapper extends BaseMapper<CourseChange> {

    /**
     * 分页查询调课记录
     */
    IPage<CourseChange> selectCourseChangePage(IPage<CourseChange> page, @Param(Constants.WRAPPER) Wrapper<CourseChange> queryWrapper);
    
    /**
     * 查询调课详情（关联审批信息）
     */
    CourseChange selectCourseChangeWithApproval(@Param("id") Long id);
}