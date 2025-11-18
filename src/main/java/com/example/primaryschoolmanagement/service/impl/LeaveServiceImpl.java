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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 请假业务逻辑实现类
 */
@Service
public class LeaveServiceImpl extends ServiceImpl<LeaveMapper, Leave> implements LeaveService {

    @Resource
    private LeaveMapper leaveMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitLeave(LeaveDTO leaveDTO, Long userId) {
        // 1. 数据验证
        validateLeaveDTO(leaveDTO);

        // 2. 创建请假实体
        Leave leave = new Leave();
        BeanUtils.copyProperties(leaveDTO, leave);

        // 3. 设置额外字段
        leave.setStudentId(userId);
        leave.setStudentName("学生姓名"); // 实际应从学生表查询
        leave.setLeaveNo(generateLeaveNo());
        leave.setLeaveDays(calculateLeaveDays(leaveDTO.getStartDate(), leaveDTO.getEndDate()));
        leave.setApplyTime(LocalDateTime.now());
        leave.setApprovalStatus(ApprovalStatusEnum.PENDING.getCode());

        // 4. 保存到数据库
        baseMapper.insert(leave);

        // 5. 调用审批流程
        Long approvalId = createApprovalProcess(leave);
        leave.setApprovalId(approvalId);
        baseMapper.updateById(leave);

        return leave.getId(); // 返回ID
    }

    @Override
    public LeaveVO getLeaveDetail(Long id) {
        Leave leave = leaveMapper.selectLeaveWithApproval(id);
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

    private String generateLeaveNo() {
        return "L" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private BigDecimal calculateLeaveDays(LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return BigDecimal.valueOf(days);
    }

    private Long createApprovalProcess(Leave leave) {
        // 集成工作流引擎或调用审批服务
        // 这里可以调用审批模块的API创建审批流程
        return System.currentTimeMillis(); // 模拟返回审批ID
    }

    private void cancelApprovalProcess(Long approvalId) {
        // 调用审批服务的取消接口
        // 这里可以调用审批模块的API取消审批流程
    }

    private LeaveVO convertToVO(Leave leave) {
        if (leave == null) {
            return null;
        }

        LeaveVO leaveVO = new LeaveVO();
        BeanUtils.copyProperties(leave, leaveVO);

        // 设置枚举文本
        leaveVO.setLeaveTypeText(LeaveTypeEnum.getTextByCode(leave.getLeaveType()));
        leaveVO.setApprovalStatusText(ApprovalStatusEnum.getTextByCode(leave.getApprovalStatus()));

        // 实际项目中查询班级表获取班级名称
        leaveVO.setClassName("班级名称");

        // 实际项目中查询审批节点表获取审批信息
        leaveVO.setLastApprover("班主任");
        leaveVO.setLastApprovalTime(LocalDateTime.now());

        return leaveVO;
    }
}