package com.example.primaryschoolmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.primaryschoolmanagement.common.base.EntityBase;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
@Accessors(chain = true)
public class AppUser extends EntityBase {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String userName;
    private String password;
    private String realName;
    private Boolean userType;
    private String avatar;
    private String phone;
    private String email;
    private Byte gender;
    private Boolean status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime lastLoginTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime lastLoginIp;
}
