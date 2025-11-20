/*
 Navicat Premium Dump SQL

 Source Server         : 本地Mysql
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : school_management

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 20/11/2025 16:51:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for biz_class_transfer
-- ----------------------------
DROP TABLE IF EXISTS `biz_class_transfer`;
CREATE TABLE `biz_class_transfer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '调班ID',
  `transfer_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调班单号',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `original_class_id` bigint NOT NULL COMMENT '原班级ID',
  `original_class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原班级名称',
  `target_class_id` bigint NOT NULL COMMENT '目标班级ID',
  `target_class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标班级名称',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调班原因',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `approval_status` tinyint NULL DEFAULT 1 COMMENT '审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回',
  `approval_id` bigint NULL DEFAULT NULL COMMENT '关联审批记录ID',
  `effective_date` date NULL DEFAULT NULL COMMENT '生效日期',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_transfer_no`(`transfer_no` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_original_class_id`(`original_class_id` ASC) USING BTREE,
  INDEX `idx_target_class_id`(`target_class_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '调班申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_class_transfer
-- ----------------------------

-- ----------------------------
-- Table structure for biz_course_change
-- ----------------------------
DROP TABLE IF EXISTS `biz_course_change`;
CREATE TABLE `biz_course_change`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '调课ID',
  `change_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调课单号',
  `apply_teacher_id` bigint NOT NULL COMMENT '申请教师ID',
  `apply_teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申请教师姓名',
  `original_schedule_id` bigint NOT NULL COMMENT '原课程表ID',
  `original_date` date NOT NULL COMMENT '原上课日期',
  `original_period` tinyint NOT NULL COMMENT '原上课节次',
  `new_date` date NOT NULL COMMENT '新上课日期',
  `new_period` tinyint NOT NULL COMMENT '新上课节次',
  `new_classroom` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '新教室',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调课原因',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `approval_status` tinyint NULL DEFAULT 1 COMMENT '审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回',
  `approval_id` bigint NULL DEFAULT NULL COMMENT '关联审批记录ID',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_change_no`(`change_no` ASC) USING BTREE,
  INDEX `idx_apply_teacher_id`(`apply_teacher_id` ASC) USING BTREE,
  INDEX `idx_original_schedule_id`(`original_schedule_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '调课申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_course_change
-- ----------------------------

-- ----------------------------
-- Table structure for biz_course_swap
-- ----------------------------
DROP TABLE IF EXISTS `biz_course_swap`;
CREATE TABLE `biz_course_swap`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '换课ID',
  `swap_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '换课单号',
  `apply_teacher_id` bigint NOT NULL COMMENT '申请教师ID',
  `apply_teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申请教师姓名',
  `apply_schedule_id` bigint NOT NULL COMMENT '申请人课程表ID',
  `target_teacher_id` bigint NOT NULL COMMENT '目标教师ID',
  `target_teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标教师姓名',
  `target_schedule_id` bigint NOT NULL COMMENT '目标教师课程表ID',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '换课原因',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `target_confirm` tinyint NULL DEFAULT 0 COMMENT '对方确认：0-未确认 1-已确认 2-已拒绝',
  `approval_status` tinyint NULL DEFAULT 1 COMMENT '审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回',
  `approval_id` bigint NULL DEFAULT NULL COMMENT '关联审批记录ID',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_swap_no`(`swap_no` ASC) USING BTREE,
  INDEX `idx_apply_teacher_id`(`apply_teacher_id` ASC) USING BTREE,
  INDEX `idx_target_teacher_id`(`target_teacher_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '换课申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_course_swap
-- ----------------------------

-- ----------------------------
-- Table structure for biz_leave
-- ----------------------------
DROP TABLE IF EXISTS `biz_leave`;
CREATE TABLE `biz_leave`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '请假ID',
  `leave_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请假单号',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `leave_type` tinyint NOT NULL COMMENT '请假类型：1-病假 2-事假 3-其他',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `leave_days` decimal(5, 1) NOT NULL COMMENT '请假天数',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请假原因',
  `proof_files` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '证明材料（JSON数组）',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `approval_status` tinyint NULL DEFAULT 1 COMMENT '审批状态：1-待审批 2-已通过 3-已拒绝 4-已撤回',
  `approval_id` bigint NULL DEFAULT NULL COMMENT '关联审批记录ID',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_leave_no`(`leave_no` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_start_date`(`start_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '请假申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_leave
-- ----------------------------

-- ----------------------------
-- Table structure for edu_class
-- ----------------------------
DROP TABLE IF EXISTS `edu_class`;
CREATE TABLE `edu_class`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `class_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级编号',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级名称（如：一年一班）',
  `grade_id` bigint NOT NULL COMMENT '年级ID',
  `head_teacher_id` bigint NULL DEFAULT NULL COMMENT '班主任ID（教师ID）',
  `classroom` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上课教室',
  `max_students` int NULL DEFAULT 50 COMMENT '最大学生数',
  `current_students` int NULL DEFAULT 0 COMMENT '当前学生数',
  `school_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学年（如：2023-2024）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-停用 1-启用',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_class_no`(`class_no` ASC) USING BTREE,
  INDEX `idx_grade_id`(`grade_id` ASC) USING BTREE,
  INDEX `idx_head_teacher_id`(`head_teacher_id` ASC) USING BTREE,
  INDEX `idx_school_year`(`school_year` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of edu_class
-- ----------------------------

-- ----------------------------
-- Table structure for edu_course
-- ----------------------------
DROP TABLE IF EXISTS `edu_course`;
CREATE TABLE `edu_course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `subject_id` bigint NOT NULL COMMENT '科目ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `teacher_id` bigint NOT NULL COMMENT '任课教师ID',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学期（如：2023-2024-1）',
  `weekly_hours` int NULL DEFAULT 0 COMMENT '每周课时数',
  `total_hours` int NULL DEFAULT 0 COMMENT '总课时数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-停用 1-进行中 2-已结束',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_subject_id`(`subject_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_semester`(`semester` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of edu_course
-- ----------------------------

-- ----------------------------
-- Table structure for edu_grade
-- ----------------------------
DROP TABLE IF EXISTS `edu_grade`;
CREATE TABLE `edu_grade`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '年级ID',
  `grade_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '年级名称（如：一年级）',
  `grade_level` int NOT NULL COMMENT '年级级别：1-6',
  `school_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学年（如：2023-2024）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-停用 1-启用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_grade_level`(`grade_level` ASC) USING BTREE,
  INDEX `idx_school_year`(`school_year` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '年级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of edu_grade
-- ----------------------------
INSERT INTO `edu_grade` VALUES (1, '一年级', 1, '2023-2024', 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `edu_grade` VALUES (2, '二年级', 2, '2023-2024', 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `edu_grade` VALUES (3, '三年级', 3, '2023-2024', 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `edu_grade` VALUES (4, '四年级', 4, '2023-2024', 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `edu_grade` VALUES (5, '五年级', 5, '2023-2024', 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `edu_grade` VALUES (6, '六年级', 6, '2023-2024', 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);

-- ----------------------------
-- Table structure for edu_schedule
-- ----------------------------
DROP TABLE IF EXISTS `edu_schedule`;
CREATE TABLE `edu_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课表ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `subject_id` bigint NOT NULL COMMENT '科目ID',
  `week_day` tinyint NOT NULL COMMENT '星期几：1-7（1=周一，7=周日）',
  `period` tinyint NOT NULL COMMENT '第几节课：1-8',
  `classroom` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教室',
  `start_time` time NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` time NULL DEFAULT NULL COMMENT '结束时间',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期（如：2023-2024-1）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-停用 1-启用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_week_period`(`week_day` ASC, `period` ASC) USING BTREE,
  INDEX `idx_semester`(`semester` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '排课时间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of edu_schedule
-- ----------------------------

-- ----------------------------
-- Table structure for edu_student
-- ----------------------------
DROP TABLE IF EXISTS `edu_student`;
CREATE TABLE `edu_student`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：1-男 2-女',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `class_id` bigint NULL DEFAULT NULL COMMENT '当前班级ID',
  `grade_id` bigint NULL DEFAULT NULL COMMENT '当前年级ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_no`(`student_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_grade_id`(`grade_id` ASC) USING BTREE,
  INDEX `idx_student_name`(`student_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of edu_student
-- ----------------------------

-- ----------------------------
-- Table structure for edu_subject
-- ----------------------------
DROP TABLE IF EXISTS `edu_subject`;
CREATE TABLE `edu_subject`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '科目ID',
  `subject_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '科目名称（语文/数学/英语/体育/微机/音乐/道法/科学）',
  `subject_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '科目编码',
  `sort_order` int NULL DEFAULT 0 COMMENT '显示排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-停用 1-启用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_subject_code`(`subject_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '科目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of edu_subject
-- ----------------------------
INSERT INTO `edu_subject` VALUES (1, '语文', 'chinese', 1, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0, NULL);
INSERT INTO `edu_subject` VALUES (2, '数学', 'math', 2, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0, NULL);
INSERT INTO `edu_subject` VALUES (3, '英语', 'english', 3, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0, NULL);
INSERT INTO `edu_subject` VALUES (4, '科学', 'science', 4, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0, NULL);
INSERT INTO `edu_subject` VALUES (5, '道法', 'moral_law', 5, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0, NULL);
INSERT INTO `edu_subject` VALUES (6, '音乐', 'music', 6, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0, NULL);
INSERT INTO `edu_subject` VALUES (7, '体育', 'pe', 7, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0, NULL);
INSERT INTO `edu_subject` VALUES (8, '微机', 'computer', 8, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0, NULL);
INSERT INTO `edu_subject` VALUES (9, '美术', 'art', 9, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0, NULL);

-- ----------------------------
-- Table structure for edu_subject_teacher
-- ----------------------------
DROP TABLE IF EXISTS `edu_subject_teacher`;
CREATE TABLE `edu_subject_teacher`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `subject_id` bigint NOT NULL COMMENT '科目ID',
  `teacher_id` bigint NOT NULL COMMENT '老师ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of edu_subject_teacher
-- ----------------------------

-- ----------------------------
-- Table structure for edu_teacher
-- ----------------------------
DROP TABLE IF EXISTS `edu_teacher`;
CREATE TABLE `edu_teacher`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `teacher_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师编号',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师姓名',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：1-男 2-女',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职称（如：班主任/语文老师）',
  `hire_date` date NULL DEFAULT NULL COMMENT '入职日期',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_teacher_no`(`teacher_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_teacher_name`(`teacher_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教师表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of edu_teacher
-- ----------------------------

-- ----------------------------
-- Table structure for flow_approval
-- ----------------------------
DROP TABLE IF EXISTS `flow_approval`;
CREATE TABLE `flow_approval`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审批记录ID',
  `approval_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审批编号',
  `process_id` bigint NOT NULL COMMENT '流程ID',
  `apply_user_id` bigint NOT NULL COMMENT '申请人ID',
  `apply_user_type` tinyint NOT NULL COMMENT '申请人类型：1-管理员 2-教师 3-学生 4-家长',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `business_type` tinyint NOT NULL COMMENT '业务类型：1-请假 2-调课 3-换课 4-调班',
  `business_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `approval_status` tinyint NULL DEFAULT 1 COMMENT '审批状态：1-待审批 2-审批中 3-已通过 4-已拒绝 5-已撤回',
  `current_approver_id` bigint NULL DEFAULT NULL COMMENT '当前审批人ID',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '申请原因',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_approval_no`(`approval_no` ASC) USING BTREE,
  INDEX `idx_process_id`(`process_id` ASC) USING BTREE,
  INDEX `idx_apply_user_id`(`apply_user_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_apply_time`(`apply_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审批记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flow_approval
-- ----------------------------

-- ----------------------------
-- Table structure for flow_approval_node
-- ----------------------------
DROP TABLE IF EXISTS `flow_approval_node`;
CREATE TABLE `flow_approval_node`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '节点记录ID',
  `approval_id` bigint NOT NULL COMMENT '审批记录ID',
  `node_level` int NOT NULL COMMENT '节点层级',
  `approver_id` bigint NOT NULL COMMENT '审批人ID',
  `approver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批人姓名',
  `approval_status` tinyint NULL DEFAULT 1 COMMENT '节点状态：1-待审批 2-已通过 3-已拒绝',
  `approval_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `approval_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审批意见',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_approval_id`(`approval_id` ASC) USING BTREE,
  INDEX `idx_approver_id`(`approver_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审批节点记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flow_approval_node
-- ----------------------------

-- ----------------------------
-- Table structure for flow_process
-- ----------------------------
DROP TABLE IF EXISTS `flow_process`;
CREATE TABLE `flow_process`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '流程ID',
  `process_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流程名称',
  `process_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流程编码',
  `process_type` tinyint NOT NULL COMMENT '流程类型：1-请假 2-调课 3-换课 4-调班',
  `process_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-停用 1-启用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_process_code`(`process_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审批流程配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flow_process
-- ----------------------------
INSERT INTO `flow_process` VALUES (1, '学生请假审批流程', 'leave_process', 1, '学生请假需要班主任审批', 1, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `flow_process` VALUES (2, '教师调课审批流程', 'change_process', 2, '教师调课需要教务主任审批', 1, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `flow_process` VALUES (3, '教师换课审批流程', 'swap_process', 3, '教师换课需要双方确认+教务主任审批', 1, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `flow_process` VALUES (4, '学生调班审批流程', 'transfer_process', 4, '学生调班需要原班主任和新班主任双重审批', 1, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID，0表示顶级菜单',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `menu_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单编码',
  `menu_type` tinyint NOT NULL COMMENT '菜单类型：1-目录 2-菜单 3-按钮',
  `route_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由地址',
  `component_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `permission` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `sort_order` int NULL DEFAULT 0 COMMENT '显示排序',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 85 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '首页', 'home', 1, '/home', NULL, NULL, 'home', 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (10, 0, '用户管理', 'user_manage', 1, '/user', NULL, NULL, 'user', 10, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (11, 10, '用户列表', 'user_list', 2, '/user/list', NULL, NULL, NULL, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (12, 10, '用户角色', 'user_role', 2, '/user/role', NULL, NULL, NULL, 2, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (13, 11, '添加用户', 'user_add', 3, '/user/list/reg', NULL, NULL, NULL, 1, NULL, '2025-11-15 10:13:40', '2025-11-19 09:55:22', 0);
INSERT INTO `sys_menu` VALUES (14, 11, '修改用户', 'user_edit', 3, '/user/list/change', NULL, NULL, NULL, 2, NULL, '2025-11-15 10:13:40', '2025-11-19 09:55:26', 0);
INSERT INTO `sys_menu` VALUES (15, 11, '删除用户', 'user_delete', 3, '/user/list/delete', NULL, NULL, NULL, 3, NULL, '2025-11-15 10:13:40', '2025-11-19 09:55:35', 0);
INSERT INTO `sys_menu` VALUES (20, 0, '菜单管理', 'menu_manage', 2, '/menu', NULL, NULL, 'menu', 20, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (21, 20, '添加菜单', 'menu_add', 3, '/menu/add', NULL, NULL, NULL, 1, NULL, '2025-11-15 10:13:40', '2025-11-19 09:56:28', 0);
INSERT INTO `sys_menu` VALUES (22, 20, '修改菜单', 'menu_edit', 3, '/menu/change', NULL, NULL, NULL, 2, NULL, '2025-11-15 10:13:40', '2025-11-19 09:56:35', 0);
INSERT INTO `sys_menu` VALUES (23, 20, '删除菜单', 'menu_delete', 3, '/menu/delete', NULL, NULL, NULL, 3, NULL, '2025-11-15 10:13:40', '2025-11-19 09:56:41', 0);
INSERT INTO `sys_menu` VALUES (30, 0, '学生管理', 'student_manage', 2, '/student', NULL, NULL, 'student', 30, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (31, 30, '添加学生', 'student_add', 3, '/student/add', NULL, NULL, NULL, 1, NULL, '2025-11-15 10:13:40', '2025-11-19 09:56:46', 0);
INSERT INTO `sys_menu` VALUES (32, 30, '修改学生', 'student_edit', 3, '/student/change', NULL, NULL, NULL, 2, NULL, '2025-11-15 10:13:40', '2025-11-19 09:56:52', 0);
INSERT INTO `sys_menu` VALUES (33, 30, '删除学生', 'student_delete', 3, '/student/delete', NULL, NULL, NULL, 3, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:00', 0);
INSERT INTO `sys_menu` VALUES (34, 30, '查看详情', 'student_detail', 3, '/student/detail', NULL, NULL, NULL, 4, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:05', 0);
INSERT INTO `sys_menu` VALUES (40, 0, '教师管理', 'teacher_manage', 2, '/teacher', NULL, NULL, 'teacher', 40, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (41, 40, '添加教师', 'teacher_add', 3, '/teacher/add', NULL, NULL, NULL, 1, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:17', 0);
INSERT INTO `sys_menu` VALUES (42, 40, '修改教师', 'teacher_edit', 3, '/teacher/change', NULL, NULL, NULL, 2, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:20', 0);
INSERT INTO `sys_menu` VALUES (43, 40, '删除教师', 'teacher_delete', 3, '/teacher/delete', NULL, NULL, NULL, 3, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:24', 0);
INSERT INTO `sys_menu` VALUES (44, 40, '查看详情', 'teacher_detail', 3, '/teacher/detail', NULL, NULL, NULL, 4, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:28', 0);
INSERT INTO `sys_menu` VALUES (50, 0, '班级管理', 'class_manage', 2, '/class', NULL, NULL, 'class', 50, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (51, 50, '添加班级', 'class_add', 3, '/class/add', NULL, NULL, NULL, 1, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:32', 0);
INSERT INTO `sys_menu` VALUES (52, 50, '修改班级', 'class_edit', 3, '/class/change', NULL, NULL, NULL, 2, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:34', 0);
INSERT INTO `sys_menu` VALUES (53, 50, '删除班级', 'class_delete', 3, '/class/delete', NULL, NULL, NULL, 3, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:39', 0);
INSERT INTO `sys_menu` VALUES (54, 50, '学生信息', 'class_student', 2, '/class/student', NULL, NULL, NULL, 4, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (55, 50, '课程信息', 'class_course', 2, '/class/course', NULL, NULL, NULL, 5, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (56, 50, '调班管理', 'class_transfer', 2, '/class/transfer', NULL, NULL, NULL, 6, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (60, 0, '课程管理', 'course_manage', 1, '/course', NULL, NULL, 'course', 60, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (61, 60, '课程列表', 'course_list', 2, '/course/list', NULL, NULL, NULL, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (62, 60, '课程详情', 'course_detail', 2, '/course/detail', NULL, NULL, NULL, 2, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (63, 60, '排课管理', 'schedule_manage', 2, '/course/schedule', NULL, NULL, NULL, 3, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (64, 60, '添加课程', 'course_add', 3, '/course/add', NULL, NULL, NULL, 4, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:48', 0);
INSERT INTO `sys_menu` VALUES (65, 60, '修改课程', 'course_edit', 3, '/course/change', NULL, NULL, NULL, 5, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:51', 0);
INSERT INTO `sys_menu` VALUES (66, 60, '删除课程', 'course_delete', 3, '/course/delete', NULL, NULL, NULL, 6, NULL, '2025-11-15 10:13:40', '2025-11-19 09:57:54', 0);
INSERT INTO `sys_menu` VALUES (70, 0, '请假管理', 'leave_manage', 2, '/leave', NULL, NULL, 'leave', 70, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (71, 70, '请假申请', 'leave_apply', 3, '/leave/application', NULL, NULL, NULL, 1, NULL, '2025-11-15 10:13:40', '2025-11-19 09:59:11', 0);
INSERT INTO `sys_menu` VALUES (72, 70, '查看详情', 'leave_detail', 3, '/leave/detail', NULL, NULL, NULL, 2, NULL, '2025-11-15 10:13:40', '2025-11-19 09:59:06', 0);
INSERT INTO `sys_menu` VALUES (80, 0, '审批管理', 'approval_manage', 1, '/approval', NULL, NULL, 'approval', 80, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (81, 80, '学生请假', 'approval_leave', 2, '/approval/leave', NULL, NULL, NULL, 1, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (82, 80, '调课申请', 'approval_change', 2, '/approval/change', NULL, NULL, NULL, 2, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (83, 80, '换课申请', 'approval_swap', 2, '/approval/swap', NULL, NULL, NULL, 3, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_menu` VALUES (84, 80, '调班申请', 'approval_transfer', 2, '/approval/transfer', NULL, NULL, NULL, 4, NULL, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `role_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '显示排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'super_admin', '拥有系统所有权限', 1, 1, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_role` VALUES (2, '教务管理员', 'edu_admin', '负责教务管理工作', 2, 1, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_role` VALUES (3, '教师', 'teacher', '普通教师角色', 3, 1, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_role` VALUES (4, '班主任', 'head_teacher', '班主任角色', 4, 1, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);
INSERT INTO `sys_role` VALUES (5, '学生', 'student', '学生角色', 5, 1, '2025-11-15 10:13:40', '2025-11-15 10:13:40', 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_menu`(`role_id` ASC, `menu_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（加密存储）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `user_type` tinyint NOT NULL COMMENT '用户类型：1-管理员 2-教师 3-学生 4-家长',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：1-男 2-女',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$ZUh.a8caUHfFwRkAtVep8eS6o75W2B1ubK4bPLWKvnlBH26wlh/tS', '系统管理员', 1, NULL, NULL, NULL, 1, 1, '2025-11-20 16:36:54', '127.0.0.1', '2025-11-15 10:13:40', '2025-11-17 20:10:11', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, '2025-11-15 10:13:40');

SET FOREIGN_KEY_CHECKS = 1;
