package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("sys_user_role")
@Accessors(chain = true)
public class UserRole {
    private Integer userId;
    private Integer roleId;
}
