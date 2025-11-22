package com.example.primaryschoolmanagement.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClassTransferVO {

    private Long id;

    private String transferNo;


    private Long studentId;


    private String studentName;


    private Long currentClassId;

    private String currentClassName;

    private Long originalClassId;

    private String originalClassName;

    private Long targetClassId;

    private String targetClassName;

    private String reason;

    private LocalDateTime applyTime;

    private Integer approvalStatus;

    private String approvalStatusText;

    private Long approvalId;

    private LocalDate effectiveDate;

    private String remark;

    private String originalClassTeacher;

    private String targetClassTeacher;

    private String lastApprover;

    private LocalDateTime lastApprovalTime;
}