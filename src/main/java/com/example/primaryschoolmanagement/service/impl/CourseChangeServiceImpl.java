package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.enums.ApprovalStatusEnum;
import com.example.primaryschoolmanagement.dao.CourseChangeMapper;
import com.example.primaryschoolmanagement.dto.CourseChangeDTO;
import com.example.primaryschoolmanagement.entity.CourseChange;
import com.example.primaryschoolmanagement.service.CourseChangeService;
import com.example.primaryschoolmanagement.vo.CourseChangeVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 调课业务逻辑实现类
 */
@Service
public class CourseChangeServiceImpl extends ServiceImpl<CourseChangeMapper, CourseChange> implements CourseChangeService {

    @Resource
    private CourseChangeMapper courseChangeMapper;

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
        courseChange.setApplyTeacherName("教师姓名"); // 实际应从教师表查询
        courseChange.setChangeNo(generateChangeNo());
        courseChange.setApplyTime(LocalDateTime.now());
        courseChange.setApprovalStatus(ApprovalStatusEnum.PENDING.getCode());

        // 5. 保存到数据库
        baseMapper.insert(courseChange);

        // 6. 调用审批流程
        Long approvalId = createApprovalProcess(courseChange);
        courseChange.setApprovalId(approvalId);
        baseMapper.updateById(courseChange);

        return courseChange.getId();
    }

    @Override
    public CourseChangeVO getCourseChangeDetail(Long id) {
        CourseChange courseChange = courseChangeMapper.selectCourseChangeWithApproval(id);
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
        // 集成工作流引擎或调用审批服务
        return System.currentTimeMillis(); // 模拟返回审批ID
    }

    private CourseChangeVO convertToVO(CourseChange courseChange) {
        if (courseChange == null) {
            return null;
        }

        CourseChangeVO courseChangeVO = new CourseChangeVO();
        BeanUtils.copyProperties(courseChange, courseChangeVO);

        // 设置枚举文本
        courseChangeVO.setApprovalStatusText(ApprovalStatusEnum.getTextByCode(courseChange.getApprovalStatus()));

        // 实际项目中查询课程表获取课程信息
        courseChangeVO.setOriginalCourseInfo("课程信息");

        // 检查时间冲突
        courseChangeVO.setHasTimeConflict(false);

        // 实际项目中查询审批节点表获取审批信息
        courseChangeVO.setLastApprover("教务主任");
        courseChangeVO.setLastApprovalTime(LocalDateTime.now());

        return courseChangeVO;
    }
}