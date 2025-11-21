package com.example.primaryschoolmanagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("换课申请DTO")
public class CourseSwapDTO {

    @ApiModelProperty(value = "申请方课程表ID", required = true)
    private Long myScheduleId;

    @ApiModelProperty(value = "目标教师ID", required = true)
    private Long targetTeacherId;

    @ApiModelProperty(value = "目标教师课程表ID", required = true)
    private Long targetScheduleId;

    @ApiModelProperty(value = "换课原因", required = true)
    private String reason;

    @ApiModelProperty(value = "备注")
    private String remark;
}
