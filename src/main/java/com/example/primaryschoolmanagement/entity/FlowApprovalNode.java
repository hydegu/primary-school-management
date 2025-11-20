package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("flow_approval_node")
public class FlowApprovalNode {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("approval_id")
    private Long approvalId;

    @TableField("node_level")
    private Integer nodeLevel;

    @TableField("approver_id")
    private Long approverId;

    @TableField("approver_name")
    private String approverName;

    @TableField("approval_status")
    private Integer approvalStatus;

    @TableField("approval_time")
    private LocalDateTime approvalTime;

    @TableField("approval_opinion")
    private String approvalOpinion;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}