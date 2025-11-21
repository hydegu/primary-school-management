package com.example.primaryschoolmanagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("请假申请DTO")
public class LeaveDTO {

    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @ApiModelProperty(value = "请假类型：1-病假 2-事假 3-其他", required = true)
    private Integer leaveType;

    @ApiModelProperty(value = "开始日期", required = true)
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期", required = true)
    private LocalDate endDate;

    @ApiModelProperty(value = "请假天数")
    private BigDecimal leaveDays;

    @ApiModelProperty(value = "请假原因", required = true)
    private String reason;

    @ApiModelProperty(value = "证明材料URL列表")
    private List<String> proofFiles;

    @ApiModelProperty(value = "备注")
    private String remark;
}