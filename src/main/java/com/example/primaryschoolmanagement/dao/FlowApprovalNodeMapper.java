package com.example.primaryschoolmanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.primaryschoolmanagement.entity.FlowApprovalNode;
import com.example.primaryschoolmanagement.vo.ApprovalNodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FlowApprovalNodeMapper extends BaseMapper<FlowApprovalNode> {
    
    /**
     * 查询审批节点列表
     */
    List<ApprovalNodeVO> selectApprovalNodes(@Param("approvalId") Long approvalId);
    
    /**
     * 查询当前待审批节点
     */
    FlowApprovalNode selectCurrentPendingNode(@Param("approvalId") Long approvalId);
    
    /**
     * 查询下一个待审批节点
     */
    FlowApprovalNode selectNextPendingNode(@Param("approvalId") Long approvalId, 
                                         @Param("currentLevel") Integer currentLevel);
}