package com.example.primaryschoolmanagement.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CourseVO {
    private Long id;
    private String courseName;
    private String semester;
    private Integer weeklyHours;
    private Integer totalHours;
    private Integer status;
    private String remark;
    private Date createdAt;
    private Date updatedAt;

    private String teacherName;
    private String className;
    private String subjectName;

}
