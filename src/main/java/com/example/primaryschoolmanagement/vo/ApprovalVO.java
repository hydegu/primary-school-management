package com.example.primaryschoolmanagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("审批记录VO")
public class ApprovalVO {
    
    @ApiModelProperty("审批记录ID")
    private Long id;
    
    @ApiModelProperty("审批编号")
    private String approvalNo;
    
    @ApiModelProperty("流程ID")
    private Long processId;
    
    @ApiModelProperty("流程名称")
    private String processName;
    
    @ApiModelProperty("申请人ID")
    private Long applyUserId;
    
    @ApiModelProperty("申请人姓名")
    private String applyUserName;
    
    @ApiModelProperty("申请时间")
    private LocalDateTime applyTime;
    
    @ApiModelProperty("业务类型")
    private Integer businessType;
    
    @ApiModelProperty("业务类型文本")
    private String businessTypeText;
    
    @ApiModelProperty("关联业务ID")
    private Long businessId;
    
    @ApiModelProperty("审批状态")
    private Integer approvalStatus;
    
    @ApiModelProperty("审批状态文本")
    private String approvalStatusText;
    
    @ApiModelProperty("当前审批人ID")
    private Long currentApproverId;
    
    @ApiModelProperty("当前审批人姓名")
    private String currentApproverName;
    
    @ApiModelProperty("申请原因")
    private String reason;
    
    @ApiModelProperty("审批节点列表")
    private List<ApprovalNodeVO> approvalNodes;
}