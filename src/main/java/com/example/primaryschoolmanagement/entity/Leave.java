package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.primaryschoolmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_leave")
public class Leave extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("leave_no")
    private String leaveNo;

    @TableField("student_id")
    private Long studentId;

    @TableField("student_name")
    private String studentName;

    @TableField("class_id")
    private Long classId;

    @TableField("leave_type")
    private Integer leaveType;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("leave_days")
    private BigDecimal leaveDays;

    private String reason;

    @TableField("proof_files")
    private String proofFiles;

    @TableField("apply_time")
    private LocalDateTime applyTime;

    @TableField("approval_status")
    private Integer approvalStatus;

    @TableField("approval_id")
    private Long approvalId;

    private String remark;

    // 添加 VO 需要的扩展字段，不映射到数据库
    @TableField(exist = false)
    private String className;

    @TableField(exist = false)
    private String leaveTypeText;

    @TableField(exist = false)
    private String approvalStatusText;

    @TableField(exist = false)
    private String lastApprover;

    @TableField(exist = false)
    private LocalDateTime lastApprovalTime;
}