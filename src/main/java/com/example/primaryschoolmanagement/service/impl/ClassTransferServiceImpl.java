package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.enums.ApprovalStatusEnum;
import com.example.primaryschoolmanagement.common.enums.BusinessTypeEnum;
import com.example.primaryschoolmanagement.dao.ClassTransferMapper;
import com.example.primaryschoolmanagement.dto.ClassTransferDTO;
import com.example.primaryschoolmanagement.entity.ClassTransfer;
import com.example.primaryschoolmanagement.service.ApprovalService;
import com.example.primaryschoolmanagement.service.ClassTransferService;
import com.example.primaryschoolmanagement.vo.ClassTransferVO;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 调班业务逻辑实现类
 */
@Service
public class ClassTransferServiceImpl extends ServiceImpl<ClassTransferMapper, ClassTransfer> implements ClassTransferService {

    private static final Logger log = LoggerFactory.getLogger(ClassTransferServiceImpl.class);

    @Resource
    private ClassTransferMapper classTransferMapper;

    @Lazy
    @Resource
    private ApprovalService approvalService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitClassTransfer(ClassTransferDTO classTransferDTO, Long userId) {
        // 1. 数据验证
        validateClassTransferDTO(classTransferDTO);

        // 2. 创建调班实体
        ClassTransfer classTransfer = new ClassTransfer();
        BeanUtils.copyProperties(classTransferDTO, classTransfer);

        // 3. 设置额外字段
        classTransfer.setStudentId(classTransferDTO.getStudentId());
        classTransfer.setStudentName(getStudentName(classTransferDTO.getStudentId())); // 查询学生姓名
        // 优先使用DTO中的currentClassId，否则查询学生当前班级
        Long originalClassId = classTransferDTO.getCurrentClassId() != null
                ? classTransferDTO.getCurrentClassId()
                : getOriginalClassId(classTransferDTO.getStudentId());
        classTransfer.setOriginalClassId(originalClassId);
        classTransfer.setOriginalClassName(getClassName(originalClassId)); // 查询原班级名称
        classTransfer.setTargetClassName(getClassName(classTransferDTO.getTargetClassId())); // 查询目标班级名称
        classTransfer.setTransferNo(generateTransferNo());
        classTransfer.setApplyTime(LocalDateTime.now());
        classTransfer.setApprovalStatus(ApprovalStatusEnum.PENDING.getCode());

        // 4. 保存到数据库
        baseMapper.insert(classTransfer);

        // 5. 调用审批流程
        Long approvalId = createApprovalProcess(classTransfer);
        if (approvalId != null) {
            classTransfer.setApprovalId(approvalId);
            baseMapper.updateById(classTransfer);
        }

        return classTransfer.getId();
    }

    @Override
    public ClassTransferVO getClassTransferDetail(Long id) {
        // 使用 MyBatis Plus 的默认方法查询
        ClassTransfer classTransfer = baseMapper.selectById(id);
        if (classTransfer == null) {
            return null;
        }
        return convertToVO(classTransfer);
    }

    @Override
    public IPage<ClassTransferVO> getMyClassTransfers(Long studentId, int page, int size) {
        Page<ClassTransfer> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ClassTransfer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassTransfer::getStudentId, studentId)
                .orderByDesc(ClassTransfer::getApplyTime);

        IPage<ClassTransfer> classTransferPage = classTransferMapper.selectPage(pageParam, queryWrapper);
        return classTransferPage.convert(this::convertToVO);
    }

    @Override
    public IPage<ClassTransferVO> getClassTransferList(int page, int size) {
        Page<ClassTransfer> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ClassTransfer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(ClassTransfer::getApplyTime);

        IPage<ClassTransfer> classTransferPage = classTransferMapper.selectPage(pageParam, queryWrapper);
        return classTransferPage.convert(this::convertToVO);
    }

    private void validateClassTransferDTO(ClassTransferDTO classTransferDTO) {
        if (classTransferDTO.getStudentId() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }
        if (classTransferDTO.getCurrentClassId() == null) {
            throw new IllegalArgumentException("当前班级ID不能为空");
        }
        if (classTransferDTO.getTargetClassId() == null) {
            throw new IllegalArgumentException("目标班级ID不能为空");
        }
        if (classTransferDTO.getCurrentClassId().equals(classTransferDTO.getTargetClassId())) {
            throw new IllegalArgumentException("目标班级不能与当前班级相同");
        }
        if (classTransferDTO.getReason() == null || classTransferDTO.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("调班原因不能为空");
        }
        // 生效日期不能早于当前日期
        if (classTransferDTO.getEffectiveDate() != null &&
                classTransferDTO.getEffectiveDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("生效日期不能早于当前日期");
        }
    }

    private String generateTransferNo() {
        return "CT" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private Long createApprovalProcess(ClassTransfer classTransfer) {
        try {
            // 调用审批服务创建审批记录
            // processId=1L 表示默认审批流程
            // applyUserType=1 表示学生（调班是学生发起的）
            return approvalService.createApprovalRecord(
                    1L,                                            // processId - 默认流程
                    classTransfer.getStudentId(),                  // applyUserId - 申请人ID（学生）
                    1,                                             // applyUserType - 1=学生
                    BusinessTypeEnum.CLASS_TRANSFER.getCode(),     // businessType - 调班
                    classTransfer.getId(),                         // businessId - 调班记录ID
                    classTransfer.getReason()                      // reason - 调班原因
            );
        } catch (Exception e) {
            log.warn("创建调班审批流程失败: {}", e.getMessage());
            return null;
        }
    }

    private ClassTransferVO convertToVO(ClassTransfer classTransfer) {
        if (classTransfer == null) {
            return null;
        }

        ClassTransferVO classTransferVO = new ClassTransferVO();
        BeanUtils.copyProperties(classTransfer, classTransferVO);

        // 设置currentClassId和currentClassName（API文档格式）
        classTransferVO.setCurrentClassId(classTransfer.getOriginalClassId());
        classTransferVO.setCurrentClassName(classTransfer.getOriginalClassName());

        // 设置枚举文本
        classTransferVO.setApprovalStatusText(ApprovalStatusEnum.getTextByCode(classTransfer.getApprovalStatus()));

        // 设置班主任信息（实际项目中应该查询教师表）
        classTransferVO.setOriginalClassTeacher(getClassTeacher(classTransfer.getOriginalClassId()));
        classTransferVO.setTargetClassTeacher(getClassTeacher(classTransfer.getTargetClassId()));

        // 设置审批信息（实际项目中应该查询审批节点表）
        classTransferVO.setLastApprover(getLastApprover(classTransfer.getApprovalId(), classTransfer.getApprovalStatus()));
        classTransferVO.setLastApprovalTime(getLastApprovalTime(classTransfer.getApprovalId(), classTransfer.getApprovalStatus()));

        return classTransferVO;
    }

    // ========== 模拟数据方法 ==========

    /**
     * 获取学生姓名（简化版）
     */
    private String getStudentName(Long studentId) {
        // 实际项目中应该查询学生表
        return "学生" + studentId;
    }

    /**
     * 获取原班级ID（简化版）
     */
    private Long getOriginalClassId(Long studentId) {
        // 实际项目中应该查询学生表获取当前班级ID
        // 这里假设原班级ID为1
        return 1L;
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
     * 获取班主任姓名（简化版）
     */
    private String getClassTeacher(Long classId) {
        // 实际项目中应该查询班级表和教师表
        if (classId == null) {
            return "未知班主任";
        }
        return "班主任" + classId;
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