package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.primaryschoolmanagement.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("biz_course_swap")
public class CourseSwap extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("swap_no")
    private String swapNo; // 换课单号（自动生成）

    @TableField("apply_teacher_id")
    private Long applyTeacherId; // 申请教师ID

    @TableField("apply_teacher_name")
    private String applyTeacherName; // 申请教师姓名

    @TableField("apply_schedule_id")
    private Long applyScheduleId; // 申请人课程表ID

    @TableField("target_teacher_id")
    private Long targetTeacherId; // 目标教师ID

    @TableField("target_teacher_name")
    private String targetTeacherName; // 目标教师姓名

    @TableField("target_schedule_id")
    private Long targetScheduleId; // 目标教师课程表ID

    private String reason; // 换课原因

    @TableField("apply_time")
    private LocalDateTime applyTime; // 申请时间

    @TableField("target_confirm")
    private Integer targetConfirm; // 对方确认：0-未确认 1-已确认 2-已拒绝

    @TableField("approval_status")
    private Integer approvalStatus; // 审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回

    @TableField("approval_id")
    private Long approvalId; // 关联审批记录ID

    private String remark; // 备注
}