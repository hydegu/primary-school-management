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
import com.example.primaryschoolmanagement.entity.Student;
import com.example.primaryschoolmanagement.entity.AppUser;
import com.example.primaryschoolmanagement.service.LeaveService;
import com.example.primaryschoolmanagement.service.StudentService;
import com.example.primaryschoolmanagement.service.UserService;
import com.example.primaryschoolmanagement.vo.LeaveVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private static final Logger log = LoggerFactory.getLogger(LeaveServiceImpl.class);

    @Resource
    private LeaveMapper leaveMapper;

    @Resource
    private StudentService studentService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitLeave(LeaveDTO leaveDTO) {
        // 1. 数据验证
        validateLeaveDTO(leaveDTO);

        // 2. 获取当前学生ID
        Long studentId = getCurrentStudentId();
        String studentName = getStudentName(studentId);

        // 3. 创建请假实体
        Leave leave = new Leave();
        BeanUtils.copyProperties(leaveDTO, leave);

        // 4. 设置额外字段
        leave.setStudentId(studentId);
        leave.setStudentName(studentName);
        leave.setLeaveNo(generateLeaveNo());
        leave.setLeaveDays(calculateLeaveDays(leaveDTO.getStartDate(), leaveDTO.getEndDate()));
        leave.setApplyTime(LocalDateTime.now());
        leave.setApprovalStatus(ApprovalStatusEnum.PENDING.getCode());

        // 5. 保存到数据库
        baseMapper.insert(leave);

        // 6. 调用审批流程（简化版）
        Long approvalId = createApprovalProcess(leave);
        if (approvalId != null) {
            leave.setApprovalId(approvalId);
            baseMapper.updateById(leave);
        }

        return leave.getId();
    }

    @Override
    public LeaveVO getLeaveDetail(Long id) {
        Leave leave = baseMapper.selectById(id);
        if (leave == null) {
            return null;
        }
        return convertToVO(leave);
    }

    @Override
    public IPage<LeaveVO> getMyLeaves(int page, int size) {
        // 获取当前学生ID
        Long studentId = getCurrentStudentId();

        Page<Leave> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Leave> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Leave::getStudentId, studentId)
                .orderByDesc(Leave::getApplyTime);

        IPage<Leave> leavePage = leaveMapper.selectPage(pageParam, queryWrapper);
        return leavePage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelLeave(Long id) {
        // 获取当前学生ID
        Long studentId = getCurrentStudentId();

        Leave leave = baseMapper.selectById(id);
        if (leave == null) {
            return false;
        }

        // 权限验证：只能撤回自己的请假申请
        if (!leave.getStudentId().equals(studentId)) {
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
     * 安全获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("用户未认证");
        }

        String username = authentication.getName();
        log.info("当前登录用户名: {}", username);

        // 方案1：通过用户名查询用户信息获取真实ID
        try {
            AppUser user = userService.findByUserName(username);
            if (user != null) {
                log.info("通过用户名查询到用户ID: {}", user.getId());
                return user.getId();
            }
        } catch (Exception e) {
            log.warn("通过用户名查询用户信息失败: {}", username, e);
        }

        // 方案2：临时测试方案 - 根据常见用户名返回测试ID
        Long testUserId = getTestUserId(username);
        if (testUserId != null) {
            log.info("使用测试用户ID: {} for username: {}", testUserId, username);
            return testUserId;
        }

        throw new IllegalArgumentException("无法获取有效的用户ID，用户名: " + username);
    }

    /**
     * 获取测试用户ID（用于开发和测试环境）
     */
    private Long getTestUserId(String username) {
        // 这里可以根据实际测试数据调整
        switch (username.toLowerCase()) {
            case "admin":
                return 1L; // 管理员用户ID
            case "student1":
            case "student":
                return 2L; // 学生用户ID
            case "teacher1":
            case "teacher":
                return 3L; // 教师用户ID
            case "parent1":
            case "parent":
                return 4L; // 家长用户ID
            default:
                // 尝试解析为数字（兼容原有逻辑）
                try {
                    return Long.parseLong(username);
                } catch (NumberFormatException e) {
                    return null;
                }
        }
    }

    /**
     * 获取当前学生ID（通过用户ID查询学生表）
     */
    private Long getCurrentStudentId() {
        Long userId = getCurrentUserId();
        log.info("获取到的用户ID: {}", userId);

        // 通过用户ID查询学生信息
        try {
            LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Student::getUserId, userId);
            Student student = studentService.getOne(queryWrapper);

            if (student != null) {
                Long studentId = student.getId().longValue();
                log.info("找到对应的学生信息，学生ID: {}", studentId);
                return studentId;
            } else {
                // 如果不是学生用户，可以返回一个测试学生ID或抛出异常
                log.warn("用户ID {} 未找到对应的学生信息，使用测试学生ID", userId);
                return getTestStudentId(userId);
            }
        } catch (Exception e) {
            log.error("查询学生信息失败，用户ID: {}", userId, e);
            throw new IllegalArgumentException("查询学生信息失败，请检查用户类型");
        }
    }

    /**
     * 获取测试学生ID（用于开发和测试环境）
     */
    private Long getTestStudentId(Long userId) {
        // 这里可以根据实际测试数据调整
        // 假设用户ID 2 对应学生ID 1
        if (userId == 2L) {
            return 1L;
        }
        // 默认返回用户ID作为学生ID（仅用于测试）
        return userId;
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
     * 获取学生姓名
     */
    private String getStudentName(Long studentId) {
        try {
            // 将 Long 类型转换为 Integer 类型进行查询
            Student student = studentService.getById(studentId.intValue());
            return student != null ? student.getStudentName() : "未知学生";
        } catch (Exception e) {
            log.warn("获取学生姓名失败，学生ID: {}", studentId, e);
            return "未知学生";
        }
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