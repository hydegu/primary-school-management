package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.enums.ApprovalStatusEnum;
import com.example.primaryschoolmanagement.common.enums.BusinessTypeEnum;
import com.example.primaryschoolmanagement.dao.CourseChangeMapper;
import com.example.primaryschoolmanagement.dto.CourseChangeDTO;
import com.example.primaryschoolmanagement.entity.CourseChange;
import com.example.primaryschoolmanagement.service.ApprovalService;
import com.example.primaryschoolmanagement.service.CourseChangeService;
import com.example.primaryschoolmanagement.vo.CourseChangeVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 调课业务逻辑实现类
 */
@Service
public class CourseChangeServiceImpl extends ServiceImpl<CourseChangeMapper, CourseChange> implements CourseChangeService {

    private static final Logger log = LoggerFactory.getLogger(CourseChangeServiceImpl.class);

    @Resource
    private CourseChangeMapper courseChangeMapper;

    @Resource
    private ApprovalService approvalService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitCourseChange(CourseChangeDTO courseChangeDTO, Long teacherId) {
        // 1. 数据验证
        validateCourseChangeDTO(courseChangeDTO);

        // 2. 检查时间冲突
        checkTimeConflict(courseChangeDTO);

        // 3. 创建调课实体
        CourseChange courseChange = new CourseChange();
        BeanUtils.copyProperties(courseChangeDTO, courseChange);

        // 4. 设置额外字段
        courseChange.setApplyTeacherId(teacherId);
        courseChange.setApplyTeacherName(getTeacherName(teacherId)); // 查询教师姓名
        courseChange.setChangeNo(generateChangeNo());
        courseChange.setApplyTime(LocalDateTime.now());
        courseChange.setApprovalStatus(ApprovalStatusEnum.PENDING.getCode());

        // 5. 保存到数据库
        baseMapper.insert(courseChange);

        // 6. 调用审批流程
        Long approvalId = createApprovalProcess(courseChange);
        if (approvalId != null) {
            courseChange.setApprovalId(approvalId);
            baseMapper.updateById(courseChange);
        }

        return courseChange.getId();
    }

    @Override
    public CourseChangeVO getCourseChangeDetail(Long id) {
        // 使用 MyBatis Plus 的默认方法查询
        CourseChange courseChange = baseMapper.selectById(id);
        if (courseChange == null) {
            return null;
        }
        return convertToVO(courseChange);
    }

    @Override
    public IPage<CourseChangeVO> getMyCourseChanges(Long teacherId, int page, int size) {
        Page<CourseChange> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CourseChange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseChange::getApplyTeacherId, teacherId)
                .orderByDesc(CourseChange::getApplyTime);

        IPage<CourseChange> courseChangePage = courseChangeMapper.selectPage(pageParam, queryWrapper);
        return courseChangePage.convert(this::convertToVO);
    }

    private void validateCourseChangeDTO(CourseChangeDTO courseChangeDTO) {
        if (courseChangeDTO.getOriginalScheduleId() == null) {
            throw new IllegalArgumentException("原课程表ID不能为空");
        }
        if (courseChangeDTO.getOriginalDate() == null || courseChangeDTO.getNewDate() == null) {
            throw new IllegalArgumentException("原上课日期和新上课日期不能为空");
        }
        if (courseChangeDTO.getOriginalPeriod() == null || courseChangeDTO.getNewPeriod() == null) {
            throw new IllegalArgumentException("原上课节次和新上课节次不能为空");
        }
        if (courseChangeDTO.getReason() == null || courseChangeDTO.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("调课原因不能为空");
        }
        // 新日期不能早于当前日期
        if (courseChangeDTO.getNewDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("新上课日期不能早于当前日期");
        }
    }

    private void checkTimeConflict(CourseChangeDTO courseChangeDTO) {
        // 检查新时间是否与其他课程冲突
        LambdaQueryWrapper<CourseChange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseChange::getNewDate, courseChangeDTO.getNewDate())
                .eq(CourseChange::getNewPeriod, courseChangeDTO.getNewPeriod())
                .ne(courseChangeDTO.getOriginalScheduleId() != null,
                        CourseChange::getOriginalScheduleId, courseChangeDTO.getOriginalScheduleId())
                .in(CourseChange::getApprovalStatus,
                        ApprovalStatusEnum.PENDING.getCode(), ApprovalStatusEnum.APPROVED.getCode());

        long conflictCount = baseMapper.selectCount(queryWrapper);
        if (conflictCount > 0) {
            throw new IllegalArgumentException("新上课时间与其他课程冲突，请选择其他时间");
        }
    }

    private String generateChangeNo() {
        return "CC" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private Long createApprovalProcess(CourseChange courseChange) {
        try {
            // 调用审批服务创建审批记录
            // processId=1L 表示默认审批流程
            // applyUserType=2 表示教师
            return approvalService.createApprovalRecord(
                    1L,                                          // processId - 默认流程
                    courseChange.getApplyTeacherId(),            // applyUserId - 申请人ID
                    2,                                           // applyUserType - 2=教师
                    BusinessTypeEnum.COURSE_CHANGE.getCode(),    // businessType - 调课
                    courseChange.getId(),                        // businessId - 调课记录ID
                    courseChange.getReason()                     // reason - 调课原因
            );
        } catch (Exception e) {
            log.warn("创建调课审批流程失败: {}", e.getMessage());
            return null;
        }
    }

    private CourseChangeVO convertToVO(CourseChange courseChange) {
        if (courseChange == null) {
            return null;
        }

        CourseChangeVO courseChangeVO = new CourseChangeVO();
        BeanUtils.copyProperties(courseChange, courseChangeVO);

        // 设置枚举文本
        courseChangeVO.setApprovalStatusText(ApprovalStatusEnum.getTextByCode(courseChange.getApprovalStatus()));

        // 设置课程信息（实际项目中应该查询课程表）
        courseChangeVO.setOriginalCourseInfo(getCourseInfo(courseChange.getOriginalScheduleId()));

        // 检查时间冲突
        courseChangeVO.setHasTimeConflict(checkTimeConflictForVO(courseChange));

        // 设置审批信息（实际项目中应该查询审批节点表）
        courseChangeVO.setLastApprover(getLastApprover(courseChange.getApprovalId(), courseChange.getApprovalStatus()));
        courseChangeVO.setLastApprovalTime(getLastApprovalTime(courseChange.getApprovalId(), courseChange.getApprovalStatus()));

        return courseChangeVO;
    }

    // ========== 模拟数据方法 ==========

    /**
     * 获取教师姓名（简化版）
     */
    private String getTeacherName(Long teacherId) {
        // 实际项目中应该查询教师表
        return "教师" + teacherId;
    }

    /**
     * 获取课程信息（简化版）
     */
    private String getCourseInfo(Long scheduleId) {
        // 实际项目中应该查询课程表和排课表
        if (scheduleId == null) {
            return "未知课程";
        }
        return "课程" + scheduleId;
    }

    /**
     * 检查时间冲突（用于VO）
     */
    private boolean checkTimeConflictForVO(CourseChange courseChange) {
        if (courseChange.getNewDate() == null || courseChange.getNewPeriod() == null) {
            return false;
        }

        LambdaQueryWrapper<CourseChange> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseChange::getNewDate, courseChange.getNewDate())
                .eq(CourseChange::getNewPeriod, courseChange.getNewPeriod())
                .ne(courseChange.getOriginalScheduleId() != null,
                        CourseChange::getOriginalScheduleId, courseChange.getOriginalScheduleId())
                .ne(courseChange.getId() != null, CourseChange::getId, courseChange.getId())
                .in(CourseChange::getApprovalStatus,
                        ApprovalStatusEnum.PENDING.getCode(), ApprovalStatusEnum.APPROVED.getCode());

        long conflictCount = baseMapper.selectCount(queryWrapper);
        return conflictCount > 0;
    }

    /**
     * 获取最后审批人（简化版）
     */
    private String getLastApprover(Long approvalId, Integer approvalStatus) {
        if (approvalId == null || ApprovalStatusEnum.PENDING.getCode().equals(approvalStatus)) {
            return "待审批";
        }
        // 实际项目中应该查询审批节点表
        return "教务主任";
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