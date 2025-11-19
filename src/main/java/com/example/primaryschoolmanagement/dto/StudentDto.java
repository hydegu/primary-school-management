package com.example.primaryschoolmanagement.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StudentDto {

    private Integer userId;
    private String studentNo;
    private String studentName;
    private Integer gender;//性别
    private String birthDate;
    private String idCard;
    private Integer classId;
    private Integer gradeId;

    private String username;
    private String password;
    private String realName;
    private Integer userType;


}
