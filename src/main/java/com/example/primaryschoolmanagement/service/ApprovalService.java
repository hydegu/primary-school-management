package com.example.primaryschoolmanagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.primaryschoolmanagement.dto.ApprovalActionDTO;
import com.example.primaryschoolmanagement.entity.FlowApproval;
import com.example.primaryschoolmanagement.vo.ApprovalVO;
import com.example.primaryschoolmanagement.vo.ApprovalNodeVO;

import java.util.List;

/**
 * 审批服务接口
 */
public interface ApprovalService extends IService<FlowApproval> {

    /**
     * 审批通过
     */
    boolean approve(ApprovalActionDTO approvalActionDTO, Long approverId);

    /**
     * 审批拒绝
     */
    boolean reject(ApprovalActionDTO approvalActionDTO, Long approverId);

    /**
     * 查询待我审批列表
     */
    IPage<ApprovalVO> getPendingApprovals(Long approverId, int page, int size);

    /**
     * 查询审批历史
     */
    IPage<ApprovalVO> getApprovalHistory(Long applyUserId, int page, int size);

    /**
     * 查询审批流程节点
     */
    List<ApprovalNodeVO> getApprovalNodes(Long approvalId);

    /**
     * 创建审批记录
     */
    Long createApprovalRecord(Long processId, Long applyUserId, Integer applyUserType,
                              Integer businessType, Long businessId, String reason);
}