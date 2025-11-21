package com.example.primaryschoolmanagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("换课确认DTO")
public class CourseSwapConfirmDTO {

    @ApiModelProperty(value = "是否确认：true-同意 false-拒绝", required = true)
    private Boolean confirm;
}
