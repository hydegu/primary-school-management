package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.primaryschoolmanagement.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("biz_leave")
public class Leave extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("leave_no")
    private String leaveNo; // 请假单号（自动生成）

    @TableField("student_id")
    private Long studentId; // 学生ID

    @TableField("student_name")
    private String studentName; // 学生姓名

    @TableField("class_id")
    private Long classId; // 班级ID

    @TableField("leave_type")
    private Integer leaveType; // 请假类型：1-病假 2-事假 3-其他

    @TableField("start_date")
    private LocalDate startDate; // 开始日期

    @TableField("end_date")
    private LocalDate endDate; // 结束日期

    @TableField("leave_days")
    private BigDecimal leaveDays; // 请假天数

    private String reason; // 请假原因

    @TableField("proof_files")
    private String proofFiles; // 证明材料（JSON数组）

    @TableField("apply_time")
    private LocalDateTime applyTime; // 申请时间

    @TableField("approval_status")
    private Integer approvalStatus; // 审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回

    @TableField("approval_id")
    private Long approvalId; // 关联审批记录ID

    private String remark; // 备注
}