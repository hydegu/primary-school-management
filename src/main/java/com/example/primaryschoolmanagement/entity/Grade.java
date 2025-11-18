package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("edu_grade")
@Accessors(chain = true)
public class Grade {
    private Integer id;
    private String gradeName;
    private String gradeLevel;
    private String schoolYear;
    private Integer status;
    private String remark;
    private Date createdAt;
    private Date updatedAt;
    private Integer isDeleted;

}
