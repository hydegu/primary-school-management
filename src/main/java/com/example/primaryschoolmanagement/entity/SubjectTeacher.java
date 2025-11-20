package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 科目教师关联实体类
 */
@Data
@TableName("edu_subject_teacher")
@Accessors(chain = true)
public class SubjectTeacher {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long subjectId;
    private Long teacherId;
    private Date createdAt;
}
