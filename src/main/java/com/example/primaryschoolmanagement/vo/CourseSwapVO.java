package com.example.primaryschoolmanagement.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseSwapVO {

    private Long id;

    private String swapNo;

    private Long applyTeacherId;

    private String applyTeacherName;

    private Long applyScheduleId;

    private String applyCourseInfo;

    private Long targetTeacherId;

    private String targetTeacherName;

    private Long targetScheduleId;

    private String targetCourseInfo;

    private String reason;

    private LocalDateTime applyTime;

    private Integer targetConfirm;

    private String targetConfirmText;

    private Integer approvalStatus;

    private String approvalStatusText;

    private Long approvalId;

    private String remark;
}