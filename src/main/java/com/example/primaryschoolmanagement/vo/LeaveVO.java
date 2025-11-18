package com.example.primaryschoolmanagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("请假详情VO")
public class LeaveVO {

    @ApiModelProperty("请假记录ID")
    private Long id;

    @ApiModelProperty("请假单号")
    private String leaveNo;

    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("班级名称")
    private String className; // 扩展字段，从班级表关联查询

    @ApiModelProperty("请假类型：1-病假 2-事假 3-其他")
    private Integer leaveType;

    @ApiModelProperty("请假类型文本")
    private String leaveTypeText; // 扩展字段，如"病假"

    @ApiModelProperty("开始日期")
    private LocalDate startDate;

    @ApiModelProperty("结束日期")
    private LocalDate endDate;

    @ApiModelProperty("请假天数")
    private BigDecimal leaveDays;

    @ApiModelProperty("请假原因")
    private String reason;

    @ApiModelProperty("证明材料（JSON数组）")
    private String proofFiles;

    @ApiModelProperty("申请时间")
    private LocalDateTime applyTime;

    @ApiModelProperty("审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回")
    private Integer approvalStatus;

    @ApiModelProperty("审批状态文本")
    private String approvalStatusText; // 扩展字段，如"待审批"

    @ApiModelProperty("审批记录ID")
    private Long approvalId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("最近审批人")
    private String lastApprover; // 扩展字段，从审批节点表关联查询

    @ApiModelProperty("最近审批时间")
    private LocalDateTime lastApprovalTime; // 扩展字段
}