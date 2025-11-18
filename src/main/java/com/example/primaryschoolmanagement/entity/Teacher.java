package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.primaryschoolmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("edu_teacher")
@Accessors(chain = true)
public class Teacher extends BaseEntity {
    private Integer id;
    private  Integer userId;
    private String teacherNo;
    private String teacherName;
    private Integer gender;
    private Date birthDate;
    private String idCard;
    private String phone;
    private String email;
    private String title;
    private Date hireDate;
}
