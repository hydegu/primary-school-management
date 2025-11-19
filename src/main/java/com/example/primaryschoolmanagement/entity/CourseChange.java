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
@TableName("biz_course_change")
public class CourseChange extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("change_no")
    private String changeNo; // 调课单号（自动生成）

    @TableField("apply_teacher_id")
    private Long applyTeacherId; // 申请教师ID

    @TableField("apply_teacher_name")
    private String applyTeacherName; // 申请教师姓名

    @TableField("original_schedule_id")
    private Long originalScheduleId; // 原课程表ID

    @TableField("original_date")
    private LocalDate originalDate; // 原上课日期

    @TableField("original_period")
    private Integer originalPeriod; // 原上课节次

    @TableField("new_date")
    private LocalDate newDate; // 新上课日期

    @TableField("new_period")
    private Integer newPeriod; // 新上课节次

    @TableField("new_classroom")
    private String newClassroom; // 新教室

    private String reason; // 调课原因

    @TableField("apply_time")
    private LocalDateTime applyTime; // 申请时间

    @TableField("approval_status")
    private Integer approvalStatus; // 审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回

    @TableField("approval_id")
    private Long approvalId; // 关联审批记录ID

    private String remark; // 备注
}