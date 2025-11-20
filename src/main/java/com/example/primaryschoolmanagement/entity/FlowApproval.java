package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("flow_approval")
public class FlowApproval {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("approval_no")
    private String approvalNo;

    @TableField("process_id")
    private Long processId;

    @TableField("apply_user_id")
    private Long applyUserId;

    @TableField("apply_user_type")
    private Integer applyUserType;

    @TableField("apply_time")
    private LocalDateTime applyTime;

    @TableField("business_type")
    private Integer businessType;

    @TableField("business_id")
    private Long businessId;

    @TableField("approval_status")
    private Integer approvalStatus;

    @TableField("current_approver_id")
    private Long currentApproverId;

    private String reason;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;
}