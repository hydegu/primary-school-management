package com.example.primaryschoolmanagement.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class subjectteacherDTO {
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






    private Long subjectId;
    private Long teacherId;
    private Date createdAt;
}
