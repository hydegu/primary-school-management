package com.example.primaryschoolmanagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("换课申请DTO")
public class CourseSwapDTO {

    @ApiModelProperty(value = "申请方课程表ID（API文档字段名）", required = true)
    private Long myScheduleId;

    @ApiModelProperty(value = "申请人课程表ID（内部字段名，兼容）")
    private Long applyScheduleId;

    @ApiModelProperty(value = "目标教师ID", required = true)
    private Long targetTeacherId;

    /**
     * 获取申请方课程表ID，优先使用myScheduleId
     */
    public Long getEffectiveApplyScheduleId() {
        return myScheduleId != null ? myScheduleId : applyScheduleId;
    }

    @ApiModelProperty(value = "目标教师课程表ID", required = true)
    private Long targetScheduleId;

    @ApiModelProperty(value = "换课原因", required = true)
    private String reason;

    @ApiModelProperty(value = "备注")
    private String remark;
}