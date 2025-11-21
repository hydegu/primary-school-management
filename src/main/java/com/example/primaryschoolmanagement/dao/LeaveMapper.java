package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.Leave;
import org.apache.ibatis.annotations.Mapper;

/**
 * 请假申请数据访问层
 */
@Mapper
public interface LeaveMapper extends BaseMapper<Leave> {

}