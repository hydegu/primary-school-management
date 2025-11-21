package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.enums.ApprovalStatusEnum;
import com.example.primaryschoolmanagement.common.enums.ApprovalNodeStatusEnum;
import com.example.primaryschoolmanagement.common.enums.BusinessTypeEnum;
import com.example.primaryschoolmanagement.common.utils.SecurityUtils;
import com.example.primaryschoolmanagement.dao.FlowApprovalMapper;
import com.example.primaryschoolmanagement.dao.FlowApprovalNodeMapper;
import com.example.primaryschoolmanagement.dto.ApprovalActionDTO;
import com.example.primaryschoolmanagement.entity.FlowApproval;
import com.example.primaryschoolmanagement.entity.FlowApprovalNode;
import com.example.primaryschoolmanagement.service.*;
import com.example.primaryschoolmanagement.vo.ApprovalVO;
import com.example.primaryschoolmanagement.vo.ApprovalNodeVO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ApprovalServiceImpl extends ServiceImpl<FlowApprovalMapper, FlowApproval>
        implements ApprovalService {

    @Resource
    private FlowApprovalMapper flowApprovalMapper;

    @Resource
    private FlowApprovalNodeMapper flowApprovalNodeMapper;

    // 注入各个业务Service
    @Resource
    private LeaveService leaveService;

    @Resource
    private CourseChangeService courseChangeService;

    @Resource
    private CourseSwapService courseSwapService;

    @Resource
    private ClassTransferService classTransferService;

    // 业务类型与服务的映射
    private Map<Integer, BusinessStatusUpdater> businessUpdaters;

    @PostConstruct
    public void init() {
        businessUpdaters = new HashMap<>();
        businessUpdaters.put(BusinessTypeEnum.LEAVE.getCode(), this::updateLeaveStatus);
        businessUpdaters.put(BusinessTypeEnum.COURSE_CHANGE.getCode(), this::updateCourseChangeStatus);
        businessUpdaters.put(BusinessTypeEnum.COURSE_SWAP.getCode(), this::updateCourseSwapStatus);
        businessUpdaters.put(BusinessTypeEnum.CLASS_TRANSFER.getCode(), this::updateClassTransferStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(ApprovalActionDTO approvalActionDTO, Long approverId) {
        // 1. 验证审批记录
        FlowApproval approval = flowApprovalMapper.selectById(approvalActionDTO.getApprovalId());
        if (approval == null) {
            throw new IllegalArgumentException("审批记录不存在");
        }

        // 2. 验证审批状态
        if (!approval.getApprovalStatus().equals(ApprovalStatusEnum.PENDING.getCode())) {
            throw new IllegalArgumentException("当前审批记录不可审批");
        }

        // 3. 验证审批人权限（超级管理员可以审批任何待处理节点）
        FlowApprovalNode currentNode = flowApprovalNodeMapper.selectCurrentPendingNode(approval.getId());
        if (currentNode == null) {
            throw new IllegalArgumentException("没有待审批的节点");
        }

        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        if (!isSuperAdmin && !currentNode.getApproverId().equals(approverId)) {
            throw new IllegalArgumentException("无权限审批此记录");
        }

        // 4. 更新当前节点状态（超级管理员审批时记录实际审批人）
        currentNode.setApprovalStatus(ApprovalNodeStatusEnum.APPROVED.getCode());
        currentNode.setApprovalTime(LocalDateTime.now());
        currentNode.setApprovalOpinion(approvalActionDTO.getApprovalOpinion());
        if (isSuperAdmin) {
            currentNode.setApproverName(currentNode.getApproverName() + "(超管代审)");
        }
        flowApprovalNodeMapper.updateById(currentNode);

        // 5. 检查是否还有后续节点
        FlowApprovalNode nextNode = flowApprovalNodeMapper.selectNextPendingNode(
                approval.getId(), currentNode.getNodeLevel());

        if (nextNode != null) {
            // 有后续节点，设置下一个审批人
            approval.setCurrentApproverId(nextNode.getApproverId());
        } else {
            // 没有后续节点，审批完成
            approval.setApprovalStatus(ApprovalStatusEnum.APPROVED.getCode());
            approval.setCurrentApproverId(null);

            // 更新业务数据状态
            updateBusinessStatus(approval.getBusinessType(), approval.getBusinessId(),
                    ApprovalStatusEnum.APPROVED.getCode());
        }

        flowApprovalMapper.updateById(approval);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reject(ApprovalActionDTO approvalActionDTO, Long approverId) {
        // 1. 验证审批记录
        FlowApproval approval = flowApprovalMapper.selectById(approvalActionDTO.getApprovalId());
        if (approval == null) {
            throw new IllegalArgumentException("审批记录不存在");
        }

        // 2. 验证审批状态
        if (!approval.getApprovalStatus().equals(ApprovalStatusEnum.PENDING.getCode())) {
            throw new IllegalArgumentException("当前审批记录不可审批");
        }

        // 3. 验证审批人权限（超级管理员可以审批任何待处理节点）
        FlowApprovalNode currentNode = flowApprovalNodeMapper.selectCurrentPendingNode(approval.getId());
        if (currentNode == null) {
            throw new IllegalArgumentException("没有待审批的节点");
        }

        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        if (!isSuperAdmin && !currentNode.getApproverId().equals(approverId)) {
            throw new IllegalArgumentException("无权限审批此记录");
        }

        // 4. 更新当前节点状态（超级管理员审批时记录实际审批人）
        currentNode.setApprovalStatus(ApprovalNodeStatusEnum.REJECTED.getCode());
        currentNode.setApprovalTime(LocalDateTime.now());
        currentNode.setApprovalOpinion(approvalActionDTO.getApprovalOpinion());
        if (isSuperAdmin) {
            currentNode.setApproverName(currentNode.getApproverName() + "(超管代审)");
        }
        flowApprovalNodeMapper.updateById(currentNode);

        // 5. 更新审批记录状态
        approval.setApprovalStatus(ApprovalStatusEnum.REJECTED.getCode());
        approval.setCurrentApproverId(null);
        flowApprovalMapper.updateById(approval);

        // 6. 更新业务数据状态
        updateBusinessStatus(approval.getBusinessType(), approval.getBusinessId(),
                ApprovalStatusEnum.REJECTED.getCode());

        return true;
    }

    @Override
    public IPage<ApprovalVO> getPendingApprovals(Long approverId, int page, int size) {
        Page<ApprovalVO> pageParam = new Page<>(page, size);
        return flowApprovalMapper.selectPendingApprovals(pageParam, approverId);
    }

    @Override
    public IPage<ApprovalVO> getApprovalHistory(Long applyUserId, int page, int size) {
        Page<ApprovalVO> pageParam = new Page<>(page, size);
        return flowApprovalMapper.selectApprovalHistory(pageParam, applyUserId);
    }

    @Override
    public List<ApprovalNodeVO> getApprovalNodes(Long approvalId) {
        List<ApprovalNodeVO> nodes = flowApprovalNodeMapper.selectApprovalNodes(approvalId);

        // 补充业务信息
        if (nodes != null && !nodes.isEmpty()) {
            // 获取审批记录信息
            FlowApproval approval = flowApprovalMapper.selectById(approvalId);
            if (approval != null) {
                // 为每个节点补充业务类型信息
                String businessTypeText = BusinessTypeEnum.getTextByCode(approval.getBusinessType());
                nodes.forEach(node -> {
                    node.setBusinessType(approval.getBusinessType());
                    node.setBusinessTypeText(businessTypeText);
                    node.setApplyReason(approval.getReason());
                });
            }

            // 设置节点处理时间预估
            setNodeProcessEstimation(nodes);
        }

        return nodes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createApprovalRecord(Long processId, Long applyUserId, Integer applyUserType,
                                     Integer businessType, Long businessId, String reason) {

        FlowApproval approval = new FlowApproval();
        approval.setApprovalNo(generateApprovalNo());
        approval.setProcessId(processId);
        approval.setApplyUserId(applyUserId);
        approval.setApplyUserType(applyUserType);
        approval.setApplyTime(LocalDateTime.now());
        approval.setBusinessType(businessType);
        approval.setBusinessId(businessId);
        approval.setApprovalStatus(ApprovalStatusEnum.PENDING.getCode());
        approval.setReason(reason);

        // 设置第一个审批人为当前审批人
        approval.setCurrentApproverId(1L);

        flowApprovalMapper.insert(approval);

        // 创建审批节点
        createApprovalNodes(approval.getId());

        return approval.getId();
    }

    /**
     * 业务状态同步 - 核心实现
     */
    private void updateBusinessStatus(Integer businessType, Long businessId, Integer status) {
        BusinessStatusUpdater updater = businessUpdaters.get(businessType);
        if (updater != null) {
            updater.updateStatus(businessId, status);
        } else {
            log.warn("未找到业务类型 {} 的状态更新器", businessType);
        }
    }

    // 更新请假状态
    private void updateLeaveStatus(Long leaveId, Integer status) {
        try {
            com.example.primaryschoolmanagement.entity.Leave leave = leaveService.getById(leaveId);
            if (leave != null) {
                leave.setApprovalStatus(status);
                boolean success = leaveService.updateById(leave);
                if (success) {
                    log.info("更新请假申请状态成功: leaveId={}, status={}", leaveId, status);
                } else {
                    log.error("更新请假申请状态失败: leaveId={}, status={}", leaveId, status);
                }
            } else {
                log.warn("未找到对应的请假记录: leaveId={}", leaveId);
            }
        } catch (Exception e) {
            log.error("更新请假申请状态异常: leaveId={}, status={}", leaveId, status, e);
            throw new RuntimeException("更新请假状态失败", e);
        }
    }

    // 更新调课状态
    private void updateCourseChangeStatus(Long changeId, Integer status) {
        try {
            com.example.primaryschoolmanagement.entity.CourseChange courseChange = courseChangeService.getById(changeId);
            if (courseChange != null) {
                courseChange.setApprovalStatus(status);
                boolean success = courseChangeService.updateById(courseChange);
                if (success) {
                    log.info("更新调课申请状态成功: changeId={}, status={}", changeId, status);
                } else {
                    log.error("更新调课申请状态失败: changeId={}, status={}", changeId, status);
                }
            } else {
                log.warn("未找到对应的调课记录: changeId={}", changeId);
            }
        } catch (Exception e) {
            log.error("更新调课申请状态异常: changeId={}, status={}", changeId, status, e);
            throw new RuntimeException("更新调课状态失败", e);
        }
    }

    // 更新换课状态
    private void updateCourseSwapStatus(Long swapId, Integer status) {
        try {
            com.example.primaryschoolmanagement.entity.CourseSwap courseSwap = courseSwapService.getById(swapId);
            if (courseSwap != null) {
                courseSwap.setApprovalStatus(status);
                boolean success = courseSwapService.updateById(courseSwap);
                if (success) {
                    log.info("更新换课申请状态成功: swapId={}, status={}", swapId, status);
                } else {
                    log.error("更新换课申请状态失败: swapId={}, status={}", swapId, status);
                }
            } else {
                log.warn("未找到对应的换课记录: swapId={}", swapId);
            }
        } catch (Exception e) {
            log.error("更新换课申请状态异常: swapId={}, status={}", swapId, status, e);
            throw new RuntimeException("更新换课状态失败", e);
        }
    }

    // 更新调班状态
    private void updateClassTransferStatus(Long transferId, Integer status) {
        try {
            com.example.primaryschoolmanagement.entity.ClassTransfer classTransfer = classTransferService.getById(transferId);
            if (classTransfer != null) {
                classTransfer.setApprovalStatus(status);
                boolean success = classTransferService.updateById(classTransfer);
                if (success) {
                    log.info("更新调班申请状态成功: transferId={}, status={}", transferId, status);
                } else {
                    log.error("更新调班申请状态失败: transferId={}, status={}", transferId, status);
                }
            } else {
                log.warn("未找到对应的调班记录: transferId={}", transferId);
            }
        } catch (Exception e) {
            log.error("更新调班申请状态异常: transferId={}, status={}", transferId, status, e);
            throw new RuntimeException("更新调班状态失败", e);
        }
    }

    /**
     * 设置节点处理时间预估
     */
    private void setNodeProcessEstimation(List<ApprovalNodeVO> nodes) {
        LocalDateTime now = LocalDateTime.now();
        for (ApprovalNodeVO node : nodes) {
            if (node.getApprovalStatus() == 1) {
                if (node.getNodeLevel() == 1) {
                    node.setEstimateProcessTime(now.plusHours(24));
                } else if (node.getNodeLevel() == 2) {
                    node.setEstimateProcessTime(now.plusHours(48));
                }
            }
        }
    }

    private void createApprovalNodes(Long approvalId) {
        FlowApprovalNode node1 = new FlowApprovalNode();
        node1.setApprovalId(approvalId);
        node1.setNodeLevel(1);
        node1.setApproverId(1L);
        node1.setApproverName("班主任");
        node1.setApprovalStatus(ApprovalNodeStatusEnum.PENDING.getCode());
        flowApprovalNodeMapper.insert(node1);

        FlowApprovalNode node2 = new FlowApprovalNode();
        node2.setApprovalId(approvalId);
        node2.setNodeLevel(2);
        node2.setApproverId(2L);
        node2.setApproverName("教务主任");
        node2.setApprovalStatus(ApprovalNodeStatusEnum.PENDING.getCode());
        flowApprovalNodeMapper.insert(node2);
    }

    private String generateApprovalNo() {
        return "APP" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    /**
     * 函数式接口，用于业务状态更新
     */
    @FunctionalInterface
    private interface BusinessStatusUpdater {
        void updateStatus(Long businessId, Integer status);
    }
}