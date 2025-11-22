package com.example.primaryschoolmanagement.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LeaveVO {

    private Long id;

    private String leaveNo;

    private Long studentId;

    private String studentName;

    private Long classId;

    private String className;

    private Integer leaveType;

    private String leaveTypeText;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal leaveDays;

    private String reason;

    private String proofFiles;

    private LocalDateTime applyTime;

    private Integer approvalStatus;

    private String approvalStatusText;

    private Long approvalId;

    private String remark;

    private String lastApprover;

    private LocalDateTime lastApprovalTime;
}