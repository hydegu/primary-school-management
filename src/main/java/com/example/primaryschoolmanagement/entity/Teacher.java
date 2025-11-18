package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("edu_teacher")
@Accessors(chain = true)
public class Teacher {
    private Integer id;
    private  Integer userId;
    private String teacherNo;
    private String teacherName;
    private String gender;
    private Date birthDate;
    private String idCard;
    private String phone;
    private String email;
    private String title;
    private Date hireDate;
    private Date createdAt;
    private Date updatedAt;
    private String isDeleted;
}
