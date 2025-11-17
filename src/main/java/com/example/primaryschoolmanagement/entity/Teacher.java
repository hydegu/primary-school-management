package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("edu_teacher")
@Accessors(chain = true)
public class Teacher {
    private  Integer id;
    private String teacher_no;
    private String teacher_name;
    private String gender;
    private Date birth_date;
    private Integer id_card;
    private String phone;
    private String email;
    private String title;
    private Date hire_date;
    private Date created_at;
    private Date Updated_at;
    private String is_deleted;
}
