package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("flow_process")
public class FlowProcess {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("process_name")
    private String processName;

    @TableField("process_code")
    private String processCode;

    @TableField("process_type")
    private Integer processType;

    @TableField("process_desc")
    private String processDesc;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;
}