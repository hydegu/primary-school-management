package com.example.primaryschoolmanagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
@ApiModel("审批操作DTO")
public class ApprovalActionDTO {

    @ApiModelProperty(value = "是否通过：true-通过 false-拒绝（submit接口使用）")
    private Boolean approved;

    @ApiModelProperty(value = "审批意见")
    private String approvalOpinion;

    @ApiModelProperty(value = "审批记录ID", hidden = true)
    private Long approvalId;
}