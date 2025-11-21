package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.enums.ApprovalStatusEnum;
import com.example.primaryschoolmanagement.common.enums.BusinessTypeEnum;
import com.example.primaryschoolmanagement.common.enums.TargetConfirmEnum;
import com.example.primaryschoolmanagement.dao.CourseSwapMapper;
import com.example.primaryschoolmanagement.dto.CourseSwapDTO;
import com.example.primaryschoolmanagement.entity.CourseSwap;
import com.example.primaryschoolmanagement.service.ApprovalService;
import com.example.primaryschoolmanagement.service.CourseSwapService;
import com.example.primaryschoolmanagement.service.UserService;
import com.example.primaryschoolmanagement.vo.CourseSwapVO;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 换课业务逻辑实现类
 */
@Service
public class CourseSwapServiceImpl extends ServiceImpl<CourseSwapMapper, CourseSwap> implements CourseSwapService {

    private static final Logger log = LoggerFactory.getLogger(CourseSwapServiceImpl.class);

    @Resource
    private CourseSwapMapper courseSwapMapper;

    @Lazy
    @Resource
    private ApprovalService approvalService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitCourseSwap(CourseSwapDTO courseSwapDTO, Long teacherId) {
        // 1. 数据验证
        validateCourseSwapDTO(courseSwapDTO, teacherId);

        // 2. 创建换课实体
        CourseSwap courseSwap = new CourseSwap();
        BeanUtils.copyProperties(courseSwapDTO, courseSwap);

        // 3. 设置额外字段
        courseSwap.setApplyScheduleId(courseSwapDTO.getMyScheduleId());
        courseSwap.setApplyTeacherId(teacherId);
        courseSwap.setApplyTeacherName("申请教师姓名"); // 实际应从教师表查询
        courseSwap.setTargetTeacherName("目标教师姓名"); // 实际应从教师表查询
        courseSwap.setSwapNo(generateSwapNo());
        courseSwap.setApplyTime(LocalDateTime.now());
        courseSwap.setTargetConfirm(TargetConfirmEnum.PENDING.getCode());
        courseSwap.setApprovalStatus(ApprovalStatusEnum.PENDING.getCode());

        // 4. 保存到数据库
        baseMapper.insert(courseSwap);

        return courseSwap.getId();
    }

    @Override
    public IPage<CourseSwapVO> getMyCourseSwaps(Long teacherId, int page, int size) {
        Page<CourseSwap> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CourseSwap> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
                .eq(CourseSwap::getApplyTeacherId, teacherId)
                .or()
                .eq(CourseSwap::getTargetTeacherId, teacherId)
        ).orderByDesc(CourseSwap::getApplyTime);

        IPage<CourseSwap> courseSwapPage = courseSwapMapper.selectPage(pageParam, queryWrapper);
        return courseSwapPage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmCourseSwap(Long id, Long teacherId, boolean confirm) {
        CourseSwap courseSwap = baseMapper.selectById(id);
        if (courseSwap == null) {
            return false;
        }

        // 权限验证：只能由目标教师确认
        if (!courseSwap.getTargetTeacherId().equals(teacherId)) {
            return false;
        }

        // 状态验证：只能确认未确认状态的申请
        if (!TargetConfirmEnum.PENDING.getCode().equals(courseSwap.getTargetConfirm())) {
            return false;
        }

        if (confirm) {
            // 确认换课
            courseSwap.setTargetConfirm(TargetConfirmEnum.CONFIRMED.getCode());

            // 调用审批流程
            Long approvalId = createApprovalProcess(courseSwap);
            courseSwap.setApprovalId(approvalId);
        } else {
            // 拒绝换课
            courseSwap.setTargetConfirm(TargetConfirmEnum.REJECTED.getCode());
        }

        int updateCount = baseMapper.updateById(courseSwap);
        return updateCount > 0;
    }

    private void validateCourseSwapDTO(CourseSwapDTO courseSwapDTO, Long currentTeacherId) {
        if (courseSwapDTO.getMyScheduleId() == null) {
            throw new IllegalArgumentException("申请方课程表ID不能为空");
        }
        if (courseSwapDTO.getTargetTeacherId() == null) {
            throw new IllegalArgumentException("目标教师ID不能为空");
        }
        if (courseSwapDTO.getTargetScheduleId() == null) {
            throw new IllegalArgumentException("目标教师课程表ID不能为空");
        }
        if (courseSwapDTO.getReason() == null || courseSwapDTO.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("换课原因不能为空");
        }

        // 验证目标教师是否存在
        if (userService.getById(courseSwapDTO.getTargetTeacherId()) == null) {
            throw new IllegalArgumentException("目标教师不存在");
        }

        // 不能和自己换课
        if (courseSwapDTO.getTargetTeacherId().equals(currentTeacherId)) {
            throw new IllegalArgumentException("不能和自己换课");
        }
    }

    private String generateSwapNo() {
        return "SW" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private Long createApprovalProcess(CourseSwap courseSwap) {
        try {
            // 调用审批服务创建审批记录
            // processId=1L 表示默认审批流程
            // applyUserType=2 表示教师
            return approvalService.createApprovalRecord(
                    1L,                                        // processId - 默认流程
                    courseSwap.getApplyTeacherId(),            // applyUserId - 申请人ID
                    2,                                         // applyUserType - 2=教师
                    BusinessTypeEnum.COURSE_SWAP.getCode(),    // businessType - 换课
                    courseSwap.getId(),                        // businessId - 换课记录ID
                    courseSwap.getReason()                     // reason - 换课原因
            );
        } catch (Exception e) {
            log.warn("创建换课审批流程失败: {}", e.getMessage());
            return null;
        }
    }

    private CourseSwapVO convertToVO(CourseSwap courseSwap) {
        if (courseSwap == null) {
            return null;
        }

        CourseSwapVO courseSwapVO = new CourseSwapVO();
        BeanUtils.copyProperties(courseSwap, courseSwapVO);

        // 设置枚举文本
        courseSwapVO.setTargetConfirmText(TargetConfirmEnum.getTextByCode(courseSwap.getTargetConfirm()));
        courseSwapVO.setApprovalStatusText(ApprovalStatusEnum.getTextByCode(courseSwap.getApprovalStatus()));

        // 实际项目中查询课程表获取课程信息
        courseSwapVO.setApplyCourseInfo("申请人课程信息");
        courseSwapVO.setTargetCourseInfo("目标方课程信息");

        return courseSwapVO;
    }
}