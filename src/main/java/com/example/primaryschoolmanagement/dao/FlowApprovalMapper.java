package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.primaryschoolmanagement.entity.FlowApproval;
import com.example.primaryschoolmanagement.vo.ApprovalVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FlowApprovalMapper extends BaseMapper<FlowApproval> {
    
    /**
     * 查询审批详情
     */
    ApprovalVO selectApprovalDetail(@Param("id") Long id);
    
    /**
     * 查询待我审批列表
     */
    IPage<ApprovalVO> selectPendingApprovals(Page<ApprovalVO> page, 
                                           @Param("approverId") Long approverId);
    
    /**
     * 查询审批历史
     */
    IPage<ApprovalVO> selectApprovalHistory(Page<ApprovalVO> page, 
                                          @Param("applyUserId") Long applyUserId);
}