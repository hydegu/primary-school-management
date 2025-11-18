package com.example.primaryschoolmanagement.dto;

import lombok.Data;

@Data
public class CourseAddDto {

    private String courseName;
    private Integer subjectId;
    private Integer classId;
    private Integer teacherId;
    private String semester;//学期
    private Integer weekiyHours;
    private Integer totalHours;
    private Integer status = 0;//停用0 启用1
    private String remark;//备注
}
