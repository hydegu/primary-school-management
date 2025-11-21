package com.example.primaryschoolmanagement.dto;

import lombok.Data;

// 导入lombok（如果没引入，可手动写getter/setter）
@Data
public class ClassesQueryDTO {
    private String classNo;       // 班级编号
    private String className;     // 班级名称
    private Integer headTeacherId;// 班主任ID
}