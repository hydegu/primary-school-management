package com.example.primaryschoolmanagement.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClassTransferDTO {

    private Long currentClassId;

    private Long targetClassId;

    private String reason;

    private LocalDate effectiveDate;

    private String remark;
}