package com.example.primaryschoolmanagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("调课申请DTO")
public class CourseChangeDTO {

    @ApiModelProperty(value = "原课程表ID", required = true)
    private Long originalScheduleId;

    @ApiModelProperty(value = "原上课日期", required = true)
    private LocalDate originalDate;

    @ApiModelProperty(value = "原上课节次", required = true)
    private Integer originalPeriod;

    @ApiModelProperty(value = "新上课日期", required = true)
    private LocalDate newDate;

    @ApiModelProperty(value = "新上课节次", required = true)
    private Integer newPeriod;

    @ApiModelProperty(value = "新教室")
    private String newClassroom;

    @ApiModelProperty(value = "调课原因", required = true)
    private String reason;

    @ApiModelProperty(value = "备注")
    private String remark;
}