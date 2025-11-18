package com.example.primaryschoolmanagement.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.example.primaryschoolmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("edu_student")
@Accessors(chain = true) //链式结构
public class Student extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String studentNo;
    private String studentName;
    private Integer gender;//性别
    private String birthDate;
    private String idCard;
    private Integer classId;
    private Integer gradeId;




}
