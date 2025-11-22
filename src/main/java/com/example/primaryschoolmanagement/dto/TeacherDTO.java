package com.example.primaryschoolmanagement.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class TeacherDTO {
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




    private String username;
    private String password;
    private String realName;
    private Integer userType;
}
