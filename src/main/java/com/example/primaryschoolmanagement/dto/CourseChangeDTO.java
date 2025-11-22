package com.example.primaryschoolmanagement.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CourseChangeDTO {

    private Long originalScheduleId;

    private LocalDate originalDate;

    private Integer originalPeriod;

    private LocalDate newDate;

    private Integer newPeriod;

    private String newClassroom;

    private String reason;

    private String remark;
}