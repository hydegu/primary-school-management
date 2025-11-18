package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.primaryschoolmanagement.entity.Leave;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 请假申请数据访问层
 */
@Mapper
public interface LeaveMapper extends BaseMapper<Leave> {

    /**
     * 分页查询请假记录
     */
    IPage<Leave> selectLeavePage(IPage<Leave> page, @Param(Constants.WRAPPER) Wrapper<Leave> queryWrapper);
    
    /**
     * 查询请假详情（关联审批信息）
     */
    Leave selectLeaveWithApproval(@Param("id") Long id);
}