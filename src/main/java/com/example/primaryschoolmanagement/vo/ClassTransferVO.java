package com.example.primaryschoolmanagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("调班详情VO")
public class ClassTransferVO {

    @ApiModelProperty("调班记录ID")
    private Long id;

    @ApiModelProperty("调班单号")
    private String transferNo;

    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("当前班级ID")
    private Long currentClassId;

    @ApiModelProperty("当前班级名称")
    private String currentClassName;

    @ApiModelProperty("原班级ID（别名）")
    private Long originalClassId;

    @ApiModelProperty("原班级名称（别名）")
    private String originalClassName;

    @ApiModelProperty("目标班级ID")
    private Long targetClassId;

    @ApiModelProperty("目标班级名称")
    private String targetClassName;

    @ApiModelProperty("调班原因")
    private String reason;

    @ApiModelProperty("申请时间")
    private LocalDateTime applyTime;

    @ApiModelProperty("审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回")
    private Integer approvalStatus;

    @ApiModelProperty("审批状态文本")
    private String approvalStatusText;

    @ApiModelProperty("审批记录ID")
    private Long approvalId;

    @ApiModelProperty("生效日期")
    private LocalDate effectiveDate;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("原班主任")
    private String originalClassTeacher;

    @ApiModelProperty("目标班主任")
    private String targetClassTeacher;

    @ApiModelProperty("最近审批人")
    private String lastApprover;

    @ApiModelProperty("最近审批时间")
    private LocalDateTime lastApprovalTime;
}