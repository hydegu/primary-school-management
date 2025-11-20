package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("edu_subject")
public class Subject {
    private Long id;
    private String subjectName;
    private String subjectCode;

}
