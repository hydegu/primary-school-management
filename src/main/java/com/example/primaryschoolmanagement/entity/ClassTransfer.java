package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.primaryschoolmanagement.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("biz_class_transfer")
public class ClassTransfer extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("transfer_no")
    private String transferNo; // 调班单号（自动生成）

    @TableField("student_id")
    private Long studentId; // 学生ID

    @TableField("student_name")
    private String studentName; // 学生姓名

    @TableField("original_class_id")
    private Long originalClassId; // 原班级ID

    @TableField("original_class_name")
    private String originalClassName; // 原班级名称

    @TableField("target_class_id")
    private Long targetClassId; // 目标班级ID

    @TableField("target_class_name")
    private String targetClassName; // 目标班级名称

    private String reason; // 调班原因

    @TableField("apply_time")
    private LocalDateTime applyTime; // 申请时间

    @TableField("approval_status")
    private Integer approvalStatus; // 审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回

    @TableField("approval_id")
    private Long approvalId; // 关联审批记录ID

    @TableField("effective_date")
    private LocalDate effectiveDate; // 生效日期

    private String remark; // 备注
}