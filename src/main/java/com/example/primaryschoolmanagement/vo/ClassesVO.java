package com.example.primaryschoolmanagement.vo;

import lombok.Data;
import java.util.Date;

/**
 * 班级视图对象，包含班主任姓名
 */
@Data
public class ClassesVO {
    private Integer id;
    private String classNo;
    private String className;
    private Integer gradeId;
    private Integer headTeacherId;
    private String headTeacherName;  // 班主任姓名
    private String classroom;
    private Integer maxStudents;
    private Integer currentStudents;
    private String schoolYear;
    private Integer status;
    private String remark;
    private Date createdAt;
    private Date updatedAt;
}
