package com.example.primaryschoolmanagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("审批节点VO")
public class ApprovalNodeVO {

    @ApiModelProperty("节点记录ID")
    private Long id;

    @ApiModelProperty("审批记录ID")
    private Long approvalId;

    @ApiModelProperty("节点层级")
    private Integer nodeLevel;

    @ApiModelProperty("审批人ID")
    private Long approverId;

    @ApiModelProperty("审批人姓名")
    private String approverName;

    @ApiModelProperty("节点状态")
    private Integer approvalStatus;

    @ApiModelProperty("节点状态文本")
    private String approvalStatusText;

    @ApiModelProperty("审批时间")
    private LocalDateTime approvalTime;

    @ApiModelProperty("审批意见")
    private String approvalOpinion;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("业务类型文本")
    private String businessTypeText;

    @ApiModelProperty("申请原因")
    private String applyReason;

    @ApiModelProperty("预计处理时间")
    private LocalDateTime estimateProcessTime;

    @ApiModelProperty("审批人联系方式")
    private String approverContact;

    @ApiModelProperty("审批人角色")
    private String approverRole;
}