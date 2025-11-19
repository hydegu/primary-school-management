package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.ClassTransfer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 调班申请数据访问层
 */
@Mapper
public interface ClassTransferMapper extends BaseMapper<ClassTransfer> {

    /**
     * 查询调班详情（关联班级信息）
     */
    ClassTransfer selectClassTransferWithDetails(@Param("id") Long id);
}