package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.enums.ApprovalStatusEnum;
import com.example.primaryschoolmanagement.common.enums.LeaveTypeEnum;
import com.example.primaryschoolmanagement.dao.LeaveMapper;
import com.example.primaryschoolmanagement.dto.LeaveDTO;
import com.example.primaryschoolmanagement.entity.Leave;
import com.example.primaryschoolmanagement.service.LeaveService;
import com.example.primaryschoolmanagement.vo.LeaveVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 请假业务逻辑实现类
 */
@Service
public class LeaveServiceImpl extends ServiceImpl<LeaveMapper, Leave> implements LeaveService {

    private static final Logger log = LoggerFactory.getLogger(LeaveServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private LeaveMapper leaveMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitLeave(LeaveDTO leaveDTO, Long userId) {
        // 1. 数据验证
        validateLeaveDTO(leaveDTO);

        // 2. 创建请假实体
        Leave leave = new Leave();
        BeanUtils.copyProperties(leaveDTO, leave, "proofFiles");

        // 转换proofFiles列表为JSON字符串
        if (leaveDTO.getProofFiles() != null && !leaveDTO.getProofFiles().isEmpty()) {
            try {
                leave.setProofFiles(objectMapper.writeValueAsString(leaveDTO.getProofFiles()));
            } catch (JsonProcessingException e) {
                log.warn("证明材料序列化失败: {}", e.getMessage());
                leave.setProofFiles("[]");
            }
        }

        // 3. 设置额外字段 - 使用当前登录用户ID
        leave.setStudentId(userId);
        leave.setStudentName(getStudentName(userId));
        leave.setLeaveNo(generateLeaveNo());
        // 如果DTO中提供了leaveDays则使用，否则自动计算
        if (leaveDTO.getLeaveDays() != null) {
            leave.setLeaveDays(leaveDTO.getLeaveDays());
        } else {
            leave.setLeaveDays(calculateLeaveDays(leaveDTO.getStartDate(), leaveDTO.getEndDate()));
        }
        leave.setApplyTime(LocalDateTime.now());
        leave.setApprovalStatus(ApprovalStatusEnum.PENDING.getCode());

        // 4. 保存到数据库
        baseMapper.insert(leave);

        // 5. 调用审批流程（简化版）
        Long approvalId = createApprovalProcess(leave);
        if (approvalId != null) {
            leave.setApprovalId(approvalId);
            baseMapper.updateById(leave);
        }

        return leave.getId();
    }

    @Override
    public LeaveVO getLeaveDetail(Long id) {
        // 使用 MyBatis Plus 的默认方法查询
        Leave leave = baseMapper.selectById(id);
        if (leave == null) {
            return null;
        }
        return convertToVO(leave);
    }

    @Override
    public IPage<LeaveVO> getMyLeaves(Long studentId, int page, int size) {
        Page<Leave> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Leave> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Leave::getStudentId, studentId)
                .orderByDesc(Leave::getApplyTime);

        IPage<Leave> leavePage = leaveMapper.selectPage(pageParam, queryWrapper);
        return leavePage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelLeave(Long id, Long userId) {
        Leave leave = baseMapper.selectById(id);
        if (leave == null) {
            return false;
        }

        // 权限验证：只能撤回自己的请假申请
        if (!leave.getStudentId().equals(userId)) {
            return false;
        }

        // 状态验证：只能撤回待审批的申请
        if (!ApprovalStatusEnum.PENDING.getCode().equals(leave.getApprovalStatus())) {
            return false;
        }

        // 更新状态为已撤回
        leave.setApprovalStatus(ApprovalStatusEnum.CANCELLED.getCode());
        int updateCount = baseMapper.updateById(leave);

        // 同步取消审批流程
        cancelApprovalProcess(leave.getApprovalId());

        return updateCount > 0;
    }

    @Override
    public IPage<LeaveVO> getPendingLeaves(Long classId, int page, int size) {
        Page<Leave> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Leave> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Leave::getClassId, classId)
                .eq(Leave::getApprovalStatus, ApprovalStatusEnum.PENDING.getCode())
                .orderByAsc(Leave::getApplyTime);

        IPage<Leave> leavePage = leaveMapper.selectPage(pageParam, queryWrapper);
        return leavePage.convert(this::convertToVO);
    }

    /**
     * 验证请假数据
     */
    private void validateLeaveDTO(LeaveDTO leaveDTO) {
        if (leaveDTO.getStartDate() == null || leaveDTO.getEndDate() == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }

        if (leaveDTO.getEndDate().isBefore(leaveDTO.getStartDate())) {
            throw new IllegalArgumentException("结束日期不能早于开始日期");
        }

        if (leaveDTO.getLeaveType() == null) {
            throw new IllegalArgumentException("请假类型不能为空");
        }

        if (leaveDTO.getReason() == null || leaveDTO.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("请假原因不能为空");
        }
    }

    /**
     * 生成请假单号
     */
    private String generateLeaveNo() {
        return "L" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    /**
     * 计算请假天数
     */
    private BigDecimal calculateLeaveDays(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return BigDecimal.ZERO;
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return BigDecimal.valueOf(days);
    }

    /**
     * 获取学生姓名（简化版）
     */
    private String getStudentName(Long studentId) {
        // 这里可以调用 StudentService 查询真实姓名
        // 暂时返回模拟数据
        return "学生" + studentId;
    }

    /**
     * 创建审批流程
     */
    private Long createApprovalProcess(Leave leave) {
        try {
            // 实际项目中这里应该调用审批服务创建审批流程
            // 返回模拟的审批ID
            return System.currentTimeMillis();
        } catch (Exception e) {
            // 审批流程创建失败不影响请假申请提交
            log.warn("创建审批流程失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 取消审批流程（简化版）
     */
    private void cancelApprovalProcess(Long approvalId) {
        try {
            // 实际项目中这里应该调用审批服务取消审批流程
            if (approvalId != null) {
                // 调用审批服务的取消接口
                log.info("取消审批流程: {}", approvalId);
            }
        } catch (Exception e) {
            // 审批流程取消失败不影响请假撤回
            log.warn("取消审批流程失败: {}", e.getMessage());
        }
    }

    /**
     * 转换为VO对象
     */
    private LeaveVO convertToVO(Leave leave) {
        if (leave == null) {
            return null;
        }

        LeaveVO leaveVO = new LeaveVO();
        BeanUtils.copyProperties(leave, leaveVO);

        // 设置枚举文本
        leaveVO.setLeaveTypeText(LeaveTypeEnum.getTextByCode(leave.getLeaveType()));
        leaveVO.setApprovalStatusText(ApprovalStatusEnum.getTextByCode(leave.getApprovalStatus()));

        // 设置班级名称（实际项目中应该查询班级表）
        leaveVO.setClassName(getClassName(leave.getClassId()));

        // 设置审批信息（实际项目中应该查询审批节点表）
        leaveVO.setLastApprover(getLastApprover(leave.getApprovalId(), leave.getApprovalStatus()));
        leaveVO.setLastApprovalTime(getLastApprovalTime(leave.getApprovalId(), leave.getApprovalStatus()));

        return leaveVO;
    }

    /**
     * 获取班级名称（简化版）
     */
    private String getClassName(Long classId) {
        // 实际项目中应该查询班级表
        if (classId == null) {
            return "未知班级";
        }
        return "班级" + classId;
    }

    /**
     * 获取最后审批人（简化版）
     */
    private String getLastApprover(Long approvalId, Integer approvalStatus) {
        if (approvalId == null || ApprovalStatusEnum.PENDING.getCode().equals(approvalStatus)) {
            return "待审批";
        }
        // 实际项目中应该查询审批节点表
        return "班主任";
    }

    /**
     * 获取最后审批时间（简化版）
     */
    private LocalDateTime getLastApprovalTime(Long approvalId, Integer approvalStatus) {
        if (approvalId == null || ApprovalStatusEnum.PENDING.getCode().equals(approvalStatus)) {
            return null;
        }
        // 实际项目中应该查询审批节点表
        return LocalDateTime.now().minusHours(1); // 模拟审批时间
    }
}