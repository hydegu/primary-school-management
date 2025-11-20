package com.example.primaryschoolmanagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
@ApiModel("审批操作DTO")
public class ApprovalActionDTO {
    
    @ApiModelProperty(value = "审批意见", required = false)
    private String approvalOpinion;
    
    @ApiModelProperty(value = "审批记录ID", required = true)
    @NotNull(message = "审批记录ID不能为空")
    private Long approvalId;
}