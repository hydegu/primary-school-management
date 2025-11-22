package com.example.primaryschoolmanagement.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CourseChangeVO {

    private Long id;

    private String changeNo;

    private Long applyTeacherId;

    private String applyTeacherName;

    private Long originalScheduleId;

    private LocalDate originalDate;

    private Integer originalPeriod;

    private LocalDate newDate;

    private Integer newPeriod;

    private String newClassroom;

    private String reason;

    private LocalDateTime applyTime;

    private Integer approvalStatus;

    private String approvalStatusText;

    private Long approvalId;

    private String remark;

    private String originalCourseInfo; // 扩展字段：原课程名称、班级等

    private Boolean hasTimeConflict; // 扩展字段：新时间是否有冲突

    private String lastApprover;

    private LocalDateTime lastApprovalTime;
}