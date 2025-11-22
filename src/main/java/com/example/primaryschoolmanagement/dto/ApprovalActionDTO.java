package com.example.primaryschoolmanagement.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ApprovalActionDTO {

    private Boolean approved;

    private String approvalOpinion;

    private Long approvalId;
}