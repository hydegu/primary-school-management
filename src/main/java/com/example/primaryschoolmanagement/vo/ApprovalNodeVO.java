package com.example.primaryschoolmanagement.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApprovalNodeVO {

    private Long id;

    private Long approvalId;

    private Integer nodeLevel;

    private Long approverId;

    private String approverName;

    private Integer approvalStatus;

    private String approvalStatusText;

    private LocalDateTime approvalTime;

    private String approvalOpinion;

    private Integer businessType;

    private String businessTypeText;

    private String applyReason;

    private LocalDateTime estimateProcessTime;

    private String approverContact;

    private String approverRole;
}