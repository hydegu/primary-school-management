package com.example.primaryschoolmanagement.dto;

import lombok.Data;

@Data
public class CourseSwapDTO {

    private Long myScheduleId;

    private Long targetTeacherId;

    private Long targetScheduleId;

    private String reason;

    private String remark;
}
