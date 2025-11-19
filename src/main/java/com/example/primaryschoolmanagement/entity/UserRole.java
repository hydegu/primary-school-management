package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户角色关联表实体
 */
@Data
@TableName("sys_user_role")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    public UserRole(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
        this.createdAt = LocalDateTime.now();
    }
}
