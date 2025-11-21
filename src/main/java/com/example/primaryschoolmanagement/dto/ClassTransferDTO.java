package com.example.primaryschoolmanagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("调班申请DTO")
public class ClassTransferDTO {

    @ApiModelProperty(value = "当前班级ID", required = true)
    private Long currentClassId;

    @ApiModelProperty(value = "目标班级ID", required = true)
    private Long targetClassId;

    @ApiModelProperty(value = "调班原因", required = true)
    private String reason;

    @ApiModelProperty(value = "生效日期")
    private LocalDate effectiveDate;

    @ApiModelProperty(value = "备注")
    private String remark;
}