-- ============================================
-- 小学教务管理系统 - 数据库初始化脚本
-- 包含建库语句及表创建（带IF NOT EXISTS）
-- ============================================

-- 创建数据库（若不存在）
CREATE DATABASE IF NOT EXISTS school_management;
-- 使用目标数据库
USE school_management;

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 一、系统管理模块
-- ============================================

-- 1.1 用户表（统一用户表：管理员、教师、学生、家长）
CREATE TABLE IF NOT EXISTS `sys_user` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                                          `username` varchar(50) NOT NULL COMMENT '登录账号',
    `password` varchar(100) NOT NULL COMMENT '密码（加密存储）',
    `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
    `user_type` tinyint NOT NULL COMMENT '用户类型：1-管理员 2-教师 3-学生 4-家长',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
    `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `gender` tinyint DEFAULT NULL COMMENT '性别：1-男 2-女',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_status` (`status`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 1.2 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                                          `role_name` varchar(50) NOT NULL COMMENT '角色名称',
    `role_code` varchar(50) NOT NULL COMMENT '角色编码',
    `role_desc` varchar(200) DEFAULT NULL COMMENT '角色描述',
    `sort_order` int DEFAULT 0 COMMENT '显示排序',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 1.3 用户角色关联表（多对多）
CREATE TABLE IF NOT EXISTS `sys_user_role` (
                                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                               `user_id` bigint NOT NULL COMMENT '用户ID',
                                               `role_id` bigint NOT NULL COMMENT '角色ID',
                                               `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 1.4 菜单表
CREATE TABLE IF NOT EXISTS `sys_menu` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                                          `parent_id` bigint DEFAULT 0 COMMENT '父菜单ID，0表示顶级菜单',
                                          `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
    `menu_code` varchar(50) DEFAULT NULL COMMENT '菜单编码',
    `menu_type` tinyint NOT NULL COMMENT '菜单类型：1-目录 2-菜单 3-按钮',
    `route_path` varchar(200) DEFAULT NULL COMMENT '路由地址',
    `component_path` varchar(200) DEFAULT NULL COMMENT '组件路径',
    `permission` varchar(100) DEFAULT NULL COMMENT '权限标识',
    `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
    `sort_order` int DEFAULT 0 COMMENT '显示排序',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 1.5 角色菜单关联表（多对多）
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
                                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                               `role_id` bigint NOT NULL COMMENT '角色ID',
                                               `menu_id` bigint NOT NULL COMMENT '菜单ID',
                                               `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ============================================
-- 二、教务核心模块
-- ============================================

-- 2.1 教师表
CREATE TABLE IF NOT EXISTS `edu_teacher` (
                                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教师ID',
                                             `user_id` bigint NOT NULL COMMENT '关联用户ID',
                                             `teacher_no` varchar(50) NOT NULL COMMENT '教师编号',
    `teacher_name` varchar(50) NOT NULL COMMENT '教师姓名',
    `gender` tinyint DEFAULT NULL COMMENT '性别：1-男 2-女',
    `birth_date` date DEFAULT NULL COMMENT '出生日期',
    `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
    `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `title` varchar(50) DEFAULT NULL COMMENT '职称（如：班主任/语文老师）',
    `hire_date` date DEFAULT NULL COMMENT '入职日期',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_teacher_no` (`teacher_no`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_teacher_name` (`teacher_name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 2.2 学生表
CREATE TABLE IF NOT EXISTS `edu_student` (
                                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生ID',
                                             `user_id` bigint NOT NULL COMMENT '关联用户ID',
                                             `student_no` varchar(50) NOT NULL COMMENT '学号',
    `student_name` varchar(50) NOT NULL COMMENT '学生姓名',
    `gender` tinyint DEFAULT NULL COMMENT '性别：1-男 2-女',
    `birth_date` date DEFAULT NULL COMMENT '出生日期',
    `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
    `class_id` bigint DEFAULT NULL COMMENT '当前班级ID',
    `grade_id` bigint DEFAULT NULL COMMENT '当前年级ID',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_grade_id` (`grade_id`),
    KEY `idx_student_name` (`student_name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 2.5 年级表
CREATE TABLE IF NOT EXISTS `edu_grade` (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '年级ID',
                                           `grade_name` varchar(50) NOT NULL COMMENT '年级名称（如：一年级）',
    `grade_level` int NOT NULL COMMENT '年级级别：1-6',
    `school_year` varchar(20) NOT NULL COMMENT '学年（如：2023-2024）',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-停用 1-启用',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_grade_level` (`grade_level`),
    KEY `idx_school_year` (`school_year`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年级表';

-- 2.6 班级表
CREATE TABLE IF NOT EXISTS `edu_class` (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '班级ID',
                                           `class_no` varchar(50) NOT NULL COMMENT '班级编号',
    `class_name` varchar(50) NOT NULL COMMENT '班级名称（如：一年一班）',
    `grade_id` bigint NOT NULL COMMENT '年级ID',
    `head_teacher_id` bigint DEFAULT NULL COMMENT '班主任ID（教师ID）',
    `classroom` varchar(50) DEFAULT NULL COMMENT '上课教室',
    `max_students` int DEFAULT 50 COMMENT '最大学生数',
    `current_students` int DEFAULT 0 COMMENT '当前学生数',
    `school_year` varchar(20) NOT NULL COMMENT '学年（如：2023-2024）',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-停用 1-启用',
    `remark` text COMMENT '备注',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_no` (`class_no`),
    KEY `idx_grade_id` (`grade_id`),
    KEY `idx_head_teacher_id` (`head_teacher_id`),
    KEY `idx_school_year` (`school_year`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 2.7 科目表
CREATE TABLE IF NOT EXISTS `edu_subject` (
                                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '科目ID',
                                             `subject_name` varchar(50) NOT NULL COMMENT '科目名称（语文/数学/英语/体育/微机/音乐/道法/科学）',
    `subject_code` varchar(50) NOT NULL COMMENT '科目编码',
    `sort_order` int DEFAULT 0 COMMENT '显示排序',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-停用 1-启用',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_subject_code` (`subject_code`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科目表';

-- 2.8 课程表（某个班级在某学期的某科目课程）
CREATE TABLE IF NOT EXISTS `edu_course` (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
                                            `course_name` varchar(100) NOT NULL COMMENT '课程名称',
    `subject_id` bigint NOT NULL COMMENT '科目ID',
    `class_id` bigint NOT NULL COMMENT '班级ID',
    `teacher_id` bigint NOT NULL COMMENT '任课教师ID',
    `semester` varchar(20) NOT NULL COMMENT '学期（如：2023-2024-1）',
    `weekly_hours` int DEFAULT 0 COMMENT '每周课时数',
    `total_hours` int DEFAULT 0 COMMENT '总课时数',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-停用 1-进行中 2-已结束',
    `remark` text COMMENT '备注',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_subject_id` (`subject_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_semester` (`semester`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 2.9 排课时间表
CREATE TABLE IF NOT EXISTS `edu_schedule` (
                                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课表ID',
                                              `course_id` bigint NOT NULL COMMENT '课程ID',
                                              `class_id` bigint NOT NULL COMMENT '班级ID',
                                              `teacher_id` bigint NOT NULL COMMENT '教师ID',
                                              `subject_id` bigint NOT NULL COMMENT '科目ID',
                                              `week_day` tinyint NOT NULL COMMENT '星期几：1-7（1=周一，7=周日）',
                                              `period` tinyint NOT NULL COMMENT '第几节课：1-8',
                                              `classroom` varchar(50) DEFAULT NULL COMMENT '教室',
    `start_time` time DEFAULT NULL COMMENT '开始时间',
    `end_time` time DEFAULT NULL COMMENT '结束时间',
    `semester` varchar(20) NOT NULL COMMENT '学期（如：2023-2024-1）',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-停用 1-启用',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_week_period` (`week_day`, `period`),
    KEY `idx_semester` (`semester`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排课时间表';

-- ============================================
-- 三、业务流程模块
-- ============================================

-- 3.1 审批流程配置表
CREATE TABLE IF NOT EXISTS `flow_process` (
                                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '流程ID',
                                              `process_name` varchar(100) NOT NULL COMMENT '流程名称',
    `process_code` varchar(50) NOT NULL COMMENT '流程编码',
    `process_type` tinyint NOT NULL COMMENT '流程类型：1-请假 2-调课 3-换课 4-调班',
    `process_desc` varchar(500) DEFAULT NULL COMMENT '流程描述',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-停用 1-启用',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_process_code` (`process_code`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批流程配置表';

-- 3.2 审批记录表
CREATE TABLE IF NOT EXISTS `flow_approval` (
                                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审批记录ID',
                                               `approval_no` varchar(50) NOT NULL COMMENT '审批编号',
    `process_id` bigint NOT NULL COMMENT '流程ID',
    `apply_user_id` bigint NOT NULL COMMENT '申请人ID',
    `apply_user_type` tinyint NOT NULL COMMENT '申请人类型：1-管理员 2-教师 3-学生 4-家长',
    `apply_time` datetime NOT NULL COMMENT '申请时间',
    `business_type` tinyint NOT NULL COMMENT '业务类型：1-请假 2-调课 3-换课 4-调班',
    `business_id` bigint DEFAULT NULL COMMENT '关联业务ID',
    `approval_status` tinyint DEFAULT 1 COMMENT '审批状态：1-待审批 2-审批中 3-已通过 4-已拒绝 5-已撤回',
    `current_approver_id` bigint DEFAULT NULL COMMENT '当前审批人ID',
    `reason` text COMMENT '申请原因',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_approval_no` (`approval_no`),
    KEY `idx_process_id` (`process_id`),
    KEY `idx_apply_user_id` (`apply_user_id`),
    KEY `idx_approval_status` (`approval_status`),
    KEY `idx_apply_time` (`apply_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';

-- 3.3 审批节点记录表
CREATE TABLE IF NOT EXISTS `flow_approval_node` (
                                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '节点记录ID',
                                                    `approval_id` bigint NOT NULL COMMENT '审批记录ID',
                                                    `node_level` int NOT NULL COMMENT '节点层级',
                                                    `approver_id` bigint NOT NULL COMMENT '审批人ID',
                                                    `approver_name` varchar(50) DEFAULT NULL COMMENT '审批人姓名',
    `approval_status` tinyint DEFAULT 1 COMMENT '节点状态：1-待审批 2-已通过 3-已拒绝',
    `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
    `approval_opinion` text COMMENT '审批意见',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_approval_id` (`approval_id`),
    KEY `idx_approver_id` (`approver_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批节点记录表';

-- 3.4 请假申请表
CREATE TABLE IF NOT EXISTS `biz_leave` (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '请假ID',
                                           `leave_no` varchar(50) NOT NULL COMMENT '请假单号',
    `student_id` bigint NOT NULL COMMENT '学生ID',
    `student_name` varchar(50) NOT NULL COMMENT '学生姓名',
    `class_id` bigint NOT NULL COMMENT '班级ID',
    `leave_type` tinyint NOT NULL COMMENT '请假类型：1-病假 2-事假 3-其他',
    `start_date` date NOT NULL COMMENT '开始日期',
    `end_date` date NOT NULL COMMENT '结束日期',
    `leave_days` decimal(5,1) NOT NULL COMMENT '请假天数',
    `reason` text COMMENT '请假原因',
    `proof_files` text COMMENT '证明材料（JSON数组）',
    `apply_time` datetime NOT NULL COMMENT '申请时间',
    `approval_status` tinyint DEFAULT 1 COMMENT '审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回',
    `approval_id` bigint DEFAULT NULL COMMENT '关联审批记录ID',
    `remark` text COMMENT '备注',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_leave_no` (`leave_no`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_approval_status` (`approval_status`),
    KEY `idx_start_date` (`start_date`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';

-- 3.5 调课申请表
CREATE TABLE IF NOT EXISTS `biz_course_change` (
                                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '调课ID',
                                                   `change_no` varchar(50) NOT NULL COMMENT '调课单号',
    `apply_teacher_id` bigint NOT NULL COMMENT '申请教师ID',
    `apply_teacher_name` varchar(50) NOT NULL COMMENT '申请教师姓名',
    `original_schedule_id` bigint NOT NULL COMMENT '原课程表ID',
    `original_date` date NOT NULL COMMENT '原上课日期',
    `original_period` tinyint NOT NULL COMMENT '原上课节次',
    `new_date` date NOT NULL COMMENT '新上课日期',
    `new_period` tinyint NOT NULL COMMENT '新上课节次',
    `new_classroom` varchar(50) DEFAULT NULL COMMENT '新教室',
    `reason` text COMMENT '调课原因',
    `apply_time` datetime NOT NULL COMMENT '申请时间',
    `approval_status` tinyint DEFAULT 1 COMMENT '审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回',
    `approval_id` bigint DEFAULT NULL COMMENT '关联审批记录ID',
    `remark` text COMMENT '备注',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_change_no` (`change_no`),
    KEY `idx_apply_teacher_id` (`apply_teacher_id`),
    KEY `idx_original_schedule_id` (`original_schedule_id`),
    KEY `idx_approval_status` (`approval_status`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调课申请表';

-- 3.6 换课申请表
CREATE TABLE IF NOT EXISTS `biz_course_swap` (
                                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '换课ID',
                                                 `swap_no` varchar(50) NOT NULL COMMENT '换课单号',
    `apply_teacher_id` bigint NOT NULL COMMENT '申请教师ID',
    `apply_teacher_name` varchar(50) NOT NULL COMMENT '申请教师姓名',
    `apply_schedule_id` bigint NOT NULL COMMENT '申请人课程表ID',
    `target_teacher_id` bigint NOT NULL COMMENT '目标教师ID',
    `target_teacher_name` varchar(50) NOT NULL COMMENT '目标教师姓名',
    `target_schedule_id` bigint NOT NULL COMMENT '目标教师课程表ID',
    `reason` text COMMENT '换课原因',
    `apply_time` datetime NOT NULL COMMENT '申请时间',
    `target_confirm` tinyint DEFAULT 0 COMMENT '对方确认：0-未确认 1-已确认 2-已拒绝',
    `approval_status` tinyint DEFAULT 1 COMMENT '审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回',
    `approval_id` bigint DEFAULT NULL COMMENT '关联审批记录ID',
    `remark` text COMMENT '备注',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_swap_no` (`swap_no`),
    KEY `idx_apply_teacher_id` (`apply_teacher_id`),
    KEY `idx_target_teacher_id` (`target_teacher_id`),
    KEY `idx_approval_status` (`approval_status`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='换课申请表';

-- 3.7 调班申请表
CREATE TABLE IF NOT EXISTS `biz_class_transfer` (
                                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '调班ID',
                                                    `transfer_no` varchar(50) NOT NULL COMMENT '调班单号',
    `student_id` bigint NOT NULL COMMENT '学生ID',
    `student_name` varchar(50) NOT NULL COMMENT '学生姓名',
    `original_class_id` bigint NOT NULL COMMENT '原班级ID',
    `original_class_name` varchar(50) DEFAULT NULL COMMENT '原班级名称',
    `target_class_id` bigint NOT NULL COMMENT '目标班级ID',
    `target_class_name` varchar(50) DEFAULT NULL COMMENT '目标班级名称',
    `reason` text COMMENT '调班原因',
    `apply_time` datetime NOT NULL COMMENT '申请时间',
    `approval_status` tinyint DEFAULT 1 COMMENT '审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回',
    `approval_id` bigint DEFAULT NULL COMMENT '关联审批记录ID',
    `effective_date` date DEFAULT NULL COMMENT '生效日期',
    `remark` text COMMENT '备注',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transfer_no` (`transfer_no`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_original_class_id` (`original_class_id`),
    KEY `idx_target_class_id` (`target_class_id`),
    KEY `idx_approval_status` (`approval_status`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调班申请表';

SET FOREIGN_KEY_CHECKS = 1;