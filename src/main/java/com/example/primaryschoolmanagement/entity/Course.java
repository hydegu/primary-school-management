package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.primaryschoolmanagement.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName("edu_course")
public class Course extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String courseName;
    private Integer subjectId;
    private Integer classId;
    private Integer teacherId;
    private String semester;
    private Integer weeklyHours;
    private Integer totalHours;
    private Integer status;
    private String remark;

}
