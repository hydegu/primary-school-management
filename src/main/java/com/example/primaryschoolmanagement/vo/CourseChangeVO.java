package com.example.primaryschoolmanagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("调课详情VO")
public class CourseChangeVO {

    @ApiModelProperty("调课记录ID")
    private Long id;

    @ApiModelProperty("调课单号")
    private String changeNo;

    @ApiModelProperty("申请教师ID")
    private Long applyTeacherId;

    @ApiModelProperty("申请教师姓名")
    private String applyTeacherName;

    @ApiModelProperty("原课程表ID")
    private Long originalScheduleId;

    @ApiModelProperty("原上课日期")
    private LocalDate originalDate;

    @ApiModelProperty("原上课节次")
    private Integer originalPeriod;

    @ApiModelProperty("新上课日期")
    private LocalDate newDate;

    @ApiModelProperty("新上课节次")
    private Integer newPeriod;

    @ApiModelProperty("新教室")
    private String newClassroom;

    @ApiModelProperty("调课原因")
    private String reason;

    @ApiModelProperty("申请时间")
    private LocalDateTime applyTime;

    @ApiModelProperty("审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回")
    private Integer approvalStatus;

    @ApiModelProperty("审批状态文本")
    private String approvalStatusText;

    @ApiModelProperty("审批记录ID")
    private Long approvalId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("原课程信息")
    private String originalCourseInfo; // 扩展字段：原课程名称、班级等

    @ApiModelProperty("新时间冲突检查")
    private Boolean hasTimeConflict; // 扩展字段：新时间是否有冲突

    @ApiModelProperty("最近审批人")
    private String lastApprover;

    @ApiModelProperty("最近审批时间")
    private LocalDateTime lastApprovalTime;
}