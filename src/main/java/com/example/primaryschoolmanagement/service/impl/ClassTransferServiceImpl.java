package com.example.primaryschoolmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.primaryschoolmanagement.common.enums.ApprovalStatusEnum;
import com.example.primaryschoolmanagement.dao.ClassTransferMapper;
import com.example.primaryschoolmanagement.dto.ClassTransferDTO;
import com.example.primaryschoolmanagement.entity.ClassTransfer;
import com.example.primaryschoolmanagement.service.ClassTransferService;
import com.example.primaryschoolmanagement.vo.ClassTransferVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 调班业务逻辑实现类
 */
@Service
public class ClassTransferServiceImpl extends ServiceImpl<ClassTransferMapper, ClassTransfer> implements ClassTransferService {

    @Resource
    private ClassTransferMapper classTransferMapper;

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
        classTransfer.setStudentName("学生姓名"); // 实际应从学生表查询
        classTransfer.setOriginalClassId(1L); // 实际应从学生表查询原班级ID
        classTransfer.setOriginalClassName("原班级名称"); // 实际应从班级表查询
        classTransfer.setTargetClassName("目标班级名称"); // 实际应从班级表查询
        classTransfer.setTransferNo(generateTransferNo());
        classTransfer.setApplyTime(LocalDateTime.now());
        classTransfer.setApprovalStatus(ApprovalStatusEnum.PENDING.getCode());

        // 4. 保存到数据库
        baseMapper.insert(classTransfer);

        // 5. 调用审批流程
        Long approvalId = createApprovalProcess(classTransfer);
        classTransfer.setApprovalId(approvalId);
        baseMapper.updateById(classTransfer);

        return classTransfer.getId();
    }

    @Override
    public ClassTransferVO getClassTransferDetail(Long id) {
        ClassTransfer classTransfer = classTransferMapper.selectClassTransferWithDetails(id);
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

    private void validateClassTransferDTO(ClassTransferDTO classTransferDTO) {
        if (classTransferDTO.getStudentId() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }
        if (classTransferDTO.getTargetClassId() == null) {
            throw new IllegalArgumentException("目标班级ID不能为空");
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
        // 集成工作流引擎或调用审批服务
        return System.currentTimeMillis(); // 模拟返回审批ID
    }

    private ClassTransferVO convertToVO(ClassTransfer classTransfer) {
        if (classTransfer == null) {
            return null;
        }

        ClassTransferVO classTransferVO = new ClassTransferVO();
        BeanUtils.copyProperties(classTransfer, classTransferVO);

        // 设置枚举文本
        classTransferVO.setApprovalStatusText(ApprovalStatusEnum.getTextByCode(classTransfer.getApprovalStatus()));

        // 实际项目中查询班级表获取班主任信息
        classTransferVO.setOriginalClassTeacher("原班主任");
        classTransferVO.setTargetClassTeacher("目标班主任");

        // 实际项目中查询审批节点表获取审批信息
        classTransferVO.setLastApprover("教务主任");
        classTransferVO.setLastApprovalTime(LocalDateTime.now());

        return classTransferVO;
    }
}