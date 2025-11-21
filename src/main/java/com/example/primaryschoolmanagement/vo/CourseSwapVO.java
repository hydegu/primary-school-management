package com.example.primaryschoolmanagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("换课详情VO")
public class CourseSwapVO {

    @ApiModelProperty("换课记录ID")
    private Long id;

    @ApiModelProperty("换课单号")
    private String swapNo;

    @ApiModelProperty("申请教师ID")
    private Long applyTeacherId;

    @ApiModelProperty("申请教师姓名")
    private String applyTeacherName;

    @ApiModelProperty("申请人课程表ID")
    private Long applyScheduleId;

    @ApiModelProperty("申请人课程信息")
    private String applyCourseInfo;

    @ApiModelProperty("目标教师ID")
    private Long targetTeacherId;

    @ApiModelProperty("目标教师姓名")
    private String targetTeacherName;

    @ApiModelProperty("目标教师课程表ID")
    private Long targetScheduleId;

    @ApiModelProperty("目标方课程信息")
    private String targetCourseInfo;

    @ApiModelProperty("换课原因")
    private String reason;

    @ApiModelProperty("申请时间")
    private LocalDateTime applyTime;

    @ApiModelProperty("确认状态：1-待确认 2-已同意 3-已拒绝")
    private Integer confirmStatus;

    @ApiModelProperty("确认时间")
    private LocalDateTime confirmTime;

    @ApiModelProperty("对方确认：0-未确认 1-已确认 2-已拒绝")
    private Integer targetConfirm;

    @ApiModelProperty("对方确认文本")
    private String targetConfirmText;

    @ApiModelProperty("审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回")
    private Integer approvalStatus;

    @ApiModelProperty("审批状态文本")
    private String approvalStatusText;

    @ApiModelProperty("审批记录ID")
    private Long approvalId;

    @ApiModelProperty("备注")
    private String remark;
}