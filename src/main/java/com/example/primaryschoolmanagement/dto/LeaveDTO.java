package com.example.primaryschoolmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class LeaveDTO {

    private Long classId;

    private Integer leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal leaveDays;

    private String reason;

    private List<String> proofFiles;

    private String remark;
}