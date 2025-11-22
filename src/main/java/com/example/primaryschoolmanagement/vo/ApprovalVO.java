package com.example.primaryschoolmanagement.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApprovalVO {

    private Long id;

    private String approvalNo;

    private Long processId;

    private String processName;

    private Long applyUserId;

    private String applyUserName;

    private LocalDateTime applyTime;

    private Integer businessType;

    private String businessTypeText;

    private Long businessId;

    private Integer approvalStatus;

    private String approvalStatusText;

    private Long currentApproverId;

    private String currentApproverName;

    private String reason;

    private List<ApprovalNodeVO> approvalNodes;
}