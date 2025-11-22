package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.primaryschoolmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 科目实体类
 */
@Data
@TableName("edu_subject")
@Accessors(chain = true)
public class Subject extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String subjectName;
    private String subjectCode;
    private Integer sortOrder;
    private Integer status;
    private String remark;
    @TableField("avatar")
    private String avatar;
}
