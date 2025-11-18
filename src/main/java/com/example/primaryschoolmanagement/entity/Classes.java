package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("edu_class")
@Accessors(chain = true)
public class Classes {
    private Integer id;
    private String classNo;
    private String className;
    private Integer gradeId;
    private Integer headTeacherId;
    private String classroom;
    private Integer maxStudents;
    private Integer currentStudents;
    private String schoolYear;
    private Integer status;
    private String remark;
    private Date createdAt;
    private Date updatedAt;
    private Integer isDeleted;
}
