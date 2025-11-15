-- ============================================
-- 小学教务管理系统 - 初始化数据脚本
-- ============================================

-- ============================================
-- 一、系统角色初始化
-- ============================================
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_desc`, `sort_order`, `status`) VALUES
(1, '超级管理员', 'super_admin', '拥有系统所有权限', 1, 1),
(2, '教务管理员', 'edu_admin', '负责教务管理工作', 2, 1),
(3, '教师', 'teacher', '普通教师角色', 3, 1),
(4, '班主任', 'head_teacher', '班主任角色', 4, 1),
(5, '学生', 'student', '学生角色', 5, 1),
(6, '家长', 'parent', '家长角色', 6, 1);

-- ============================================
-- 二、系统菜单初始化
-- ============================================
-- 首页
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_code`, `menu_type`, `route_path`, `icon`, `sort_order`) VALUES
(1, 0, '首页', 'home', 1, '/home', 'home', 1);

-- 用户管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_code`, `menu_type`, `route_path`, `icon`, `sort_order`) VALUES
(10, 0, '用户管理', 'user_manage', 1, '/user', 'user', 10),
(11, 10, '用户列表', 'user_list', 2, '/user/list', NULL, 1),
(12, 10, '用户角色', 'user_role', 2, '/user/role', NULL, 2),
(13, 11, '添加用户', 'user_add', 3, NULL, NULL, 1),
(14, 11, '修改用户', 'user_edit', 3, NULL, NULL, 2),
(15, 11, '删除用户', 'user_delete', 3, NULL, NULL, 3);

-- 菜单管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_code`, `menu_type`, `route_path`, `icon`, `sort_order`) VALUES
(20, 0, '菜单管理', 'menu_manage', 2, '/menu', 'menu', 20),
(21, 20, '添加菜单', 'menu_add', 3, NULL, NULL, 1),
(22, 20, '修改菜单', 'menu_edit', 3, NULL, NULL, 2),
(23, 20, '删除菜单', 'menu_delete', 3, NULL, NULL, 3);

-- 学生管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_code`, `menu_type`, `route_path`, `icon`, `sort_order`) VALUES
(30, 0, '学生管理', 'student_manage', 2, '/student', 'student', 30),
(31, 30, '添加学生', 'student_add', 3, NULL, NULL, 1),
(32, 30, '修改学生', 'student_edit', 3, NULL, NULL, 2),
(33, 30, '删除学生', 'student_delete', 3, NULL, NULL, 3),
(34, 30, '查看详情', 'student_detail', 3, NULL, NULL, 4);

-- 教师管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_code`, `menu_type`, `route_path`, `icon`, `sort_order`) VALUES
(40, 0, '教师管理', 'teacher_manage', 2, '/teacher', 'teacher', 40),
(41, 40, '添加教师', 'teacher_add', 3, NULL, NULL, 1),
(42, 40, '修改教师', 'teacher_edit', 3, NULL, NULL, 2),
(43, 40, '删除教师', 'teacher_delete', 3, NULL, NULL, 3),
(44, 40, '查看详情', 'teacher_detail', 3, NULL, NULL, 4);

-- 班级管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_code`, `menu_type`, `route_path`, `icon`, `sort_order`) VALUES
(50, 0, '班级管理', 'class_manage', 2, '/class', 'class', 50),
(51, 50, '添加班级', 'class_add', 3, NULL, NULL, 1),
(52, 50, '修改班级', 'class_edit', 3, NULL, NULL, 2),
(53, 50, '删除班级', 'class_delete', 3, NULL, NULL, 3),
(54, 50, '学生信息', 'class_student', 2, '/class/student', NULL, 4),
(55, 50, '课程信息', 'class_course', 2, '/class/course', NULL, 5),
(56, 50, '调班管理', 'class_transfer', 2, '/class/transfer', NULL, 6);

-- 课程管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_code`, `menu_type`, `route_path`, `icon`, `sort_order`) VALUES
(60, 0, '课程管理', 'course_manage', 1, '/course', 'course', 60),
(61, 60, '课程列表', 'course_list', 2, '/course/list', NULL, 1),
(62, 60, '课程详情', 'course_detail', 2, '/course/detail', NULL, 2),
(63, 60, '排课管理', 'schedule_manage', 2, '/course/schedule', NULL, 3),
(64, 60, '添加课程', 'course_add', 3, NULL, NULL, 4),
(65, 60, '修改课程', 'course_edit', 3, NULL, NULL, 5),
(66, 60, '删除课程', 'course_delete', 3, NULL, NULL, 6);

-- 请假管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_code`, `menu_type`, `route_path`, `icon`, `sort_order`) VALUES
(70, 0, '请假管理', 'leave_manage', 2, '/leave', 'leave', 70),
(71, 70, '请假申请', 'leave_apply', 3, NULL, NULL, 1),
(72, 70, '查看详情', 'leave_detail', 3, NULL, NULL, 2);

-- 审批管理
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_code`, `menu_type`, `route_path`, `icon`, `sort_order`) VALUES
(80, 0, '审批管理', 'approval_manage', 1, '/approval', 'approval', 80),
(81, 80, '学生请假', 'approval_leave', 2, '/approval/leave', NULL, 1),
(82, 80, '调课申请', 'approval_change', 2, '/approval/change', NULL, 2),
(83, 80, '换课申请', 'approval_swap', 2, '/approval/swap', NULL, 3),
(84, 80, '调班申请', 'approval_transfer', 2, '/approval/transfer', NULL, 4);

-- ============================================
-- 三、年级初始化（小学1-6年级）
-- ============================================
INSERT INTO `edu_grade` (`id`, `grade_name`, `grade_level`, `school_year`, `status`) VALUES
(1, '一年级', 1, '2023-2024', 1),
(2, '二年级', 2, '2023-2024', 1),
(3, '三年级', 3, '2023-2024', 1),
(4, '四年级', 4, '2023-2024', 1),
(5, '五年级', 5, '2023-2024', 1),
(6, '六年级', 6, '2023-2024', 1);

-- ============================================
-- 四、科目初始化
-- ============================================
INSERT INTO `edu_subject` (`id`, `subject_name`, `subject_code`, `sort_order`, `status`) VALUES
(1, '语文', 'chinese', 1, 1),
(2, '数学', 'math', 2, 1),
(3, '英语', 'english', 3, 1),
(4, '科学', 'science', 4, 1),
(5, '道法', 'moral_law', 5, 1),
(6, '音乐', 'music', 6, 1),
(7, '体育', 'pe', 7, 1),
(8, '微机', 'computer', 8, 1),
(9, '美术', 'art', 9, 1);

-- ============================================
-- 五、默认管理员账号初始化（密码需要加密后填入）
-- ============================================
-- 注意：以下密码为明文 'admin123'，实际使用时需要用 BCrypt 等算法加密
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `user_type`, `gender`, `status`) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU', '系统管理员', 1, 1, 1);

-- 管理员角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- ============================================
-- 六、审批流程配置初始化
-- ============================================
INSERT INTO `flow_process` (`id`, `process_name`, `process_code`, `process_type`, `process_desc`, `status`) VALUES
(1, '学生请假审批流程', 'leave_process', 1, '学生请假需要班主任审批', 1),
(2, '教师调课审批流程', 'change_process', 2, '教师调课需要教务主任审批', 1),
(3, '教师换课审批流程', 'swap_process', 3, '教师换课需要双方确认+教务主任审批', 1),
(4, '学生调班审批流程', 'transfer_process', 4, '学生调班需要原班主任和新班主任双重审批', 1);
