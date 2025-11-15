# 小学教务管理系统 - 数据库设计

## 📦 文件清单

本项目包含以下数据库设计文件：

| 文件名 | 说明 | 大小 |
|--------|------|------|
| `database.sql` | 表结构（第一部分）：系统管理模块 + 教务核心模块 | ~15KB |
| `database_init_data.sql` | 初始化数据脚本：角色、菜单、年级、科目、流程 | ~5KB |
| `数据库设计文档.md` | 完整设计文档：表关系、字段说明、扩展性说明 | ~20KB |
| `README.md` | 本文件：使用说明 | ~3KB |

---

## 🚀 快速开始

### 1. 环境要求
- MySQL 5.7+ 或 MySQL 8.0+
- 数据库字符集：utf8mb4
- 时区设置：UTC+8 (东八区)

### 2. 创建数据库
```sql
CREATE DATABASE primary_school_system 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

USE primary_school_system;
```

### 3. 执行SQL脚本

#### 方法一：使用命令行（推荐）
```bash
# Windows PowerShell
cd "C:\Users\22417\Desktop\hy\原型图"

mysql -u root -p primary_school_system < database.sql
mysql -u root -p primary_school_system < database_init_data.sql
```

#### 方法二：使用 Navicat/DBeaver 等工具
1. 打开数据库连接
2. 选择 `school_management` 数据库
3. 依次执行以下 SQL 文件：
   - `database.sql`
   - `database_init_data.sql`

### 4. 验证安装
```sql
-- 查看创建的表数量（应该是 23 张表）
SELECT COUNT(*) AS table_count 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'primary_school_system';

-- 查看年级数据
SELECT * FROM edu_grade;

-- 查看科目数据
SELECT * FROM edu_subject;
```

---

## 📊 数据库结构概览

### 核心模块（23 张表）

#### 系统管理模块（5张表）
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_user_role` - 用户角色关联表
- `sys_menu` - 菜单表
- `sys_role_menu` - 角色菜单关联表

#### 教务核心模块（11张表）
- `edu_teacher` - 教师表
- `edu_student` - 学生表
- `edu_parent` - 家长表
- `edu_student_parent` - 学生家长关联表
- `edu_grade` - 年级表
- `edu_class` - 班级表
- `edu_subject` - 科目表
- `edu_course` - 课程表
- `edu_schedule` - 排课时间表

#### 业务流程模块（7张表）
- `flow_process` - 审批流程配置表
- `flow_approval` - 审批记录表
- `flow_approval_node` - 审批节点记录表
- `biz_leave` - 请假申请表
- `biz_course_change` - 调课申请表
- `biz_course_swap` - 换课申请表
- `biz_class_transfer` - 调班申请表

---

## 🔑 默认账号

执行初始化脚本后，系统会创建一个默认管理员账号：

| 项目 | 值 |
|------|-----|
| 账号 | admin |
| 密码 | admin123 |
| 说明 | 密码已加密存储（BCrypt） |

⚠️ **安全提醒**：正式环境请立即修改默认密码！

---

## 📖 设计亮点

### 1. 统一用户管理
- 管理员、教师、学生、家长共用一个用户表
- 通过 `user_type` 字段区分用户类型
- 便于统一登录认证和权限管理

### 2. 灵活的角色权限系统
- 支持多级菜单（目录→菜单→按钮）
- 支持一人多角色（如：教师同时是班主任）
- 通过角色绑定菜单实现动态权限控制

### 3. 完善的审批流程
- 统一的审批流程引擎
- 支持多级审批
- 支持请假、调课、换课、调班等多种业务

### 4. 精细的排课系统
- 支持学期、周次、节次的精确排课
- 支持教室冲突检测
- 支持教师课表、班级课表查询

### 5. 软删除设计
- 所有核心业务表采用 `is_deleted` 字段
- 数据不会真正删除，便于恢复和审计

### 6. 良好的扩展性
- 预留 `remark` 备注字段
- 支持 JSON 字段存储扩展属性
- 支持多校区扩展
- 预留成绩管理、考勤管理等模块

---

## 🔍 表关系图

```
┌─────────────┐         ┌─────────────┐
│  sys_user   │────1:1─→│ edu_teacher │
│   (用户表)   │         │  (教师表)    │
└─────────────┘         └─────────────┘
       │
       │ 1:1
       ↓
┌─────────────┐         ┌─────────────┐
│ edu_student │────N:1─→│  edu_class  │
│  (学生表)    │         │  (班级表)    │
└─────────────┘         └─────────────┘
       │                       │
       │ M:N                   │ N:1
       ↓                       ↓
┌─────────────┐         ┌─────────────┐
│ edu_parent  │         │  edu_grade  │
│  (家长表)    │         │  (年级表)    │
└─────────────┘         └─────────────┘
```

---

## 📝 字段命名规范

### 表名规范
- **系统管理**：`sys_` 前缀（如：`sys_user`）
- **教务管理**：`edu_` 前缀（如：`edu_teacher`）
- **业务流程**：`biz_` 前缀（如：`biz_leave`）
- **流程引擎**：`flow_` 前缀（如：`flow_approval`）

### 字段规范
- 主键：`id` (bigint 自增)
- 创建时间：`created_at` (datetime)
- 更新时间：`updated_at` (datetime)
- 软删除：`is_deleted` (tinyint)
- 状态：`status` (tinyint)

---

## 🛠️ 常见操作示例

### 查询学生信息（含班级、年级）
```sql
SELECT 
    s.student_no,
    s.student_name,
    c.class_name,
    g.grade_name
FROM edu_student s
LEFT JOIN edu_class c ON s.class_id = c.id
LEFT JOIN edu_grade g ON s.grade_id = g.id
WHERE s.is_deleted = 0;
```

### 查询教师课表
```sql
SELECT 
    sch.week_day,
    sch.period,
    subj.subject_name,
    c.class_name,
    sch.classroom
FROM edu_schedule sch
LEFT JOIN edu_course course ON sch.course_id = course.id
LEFT JOIN edu_subject subj ON sch.subject_id = subj.id
LEFT JOIN edu_class c ON sch.class_id = c.id
WHERE sch.teacher_id = 1001
  AND sch.semester = '2024-2025-1'
  AND sch.is_deleted = 0
ORDER BY sch.week_day, sch.period;
```

### 查询待审批的请假申请
```sql
SELECT 
    l.leave_no,
    l.student_name,
    l.leave_type,
    l.start_date,
    l.end_date,
    l.reason,
    l.apply_time
FROM biz_leave l
WHERE l.approval_status = 1  -- 待审批
  AND l.is_deleted = 0
ORDER BY l.apply_time DESC;
```

---

## 📚 相关文档

- **详细设计文档**：请查看 `数据库设计文档.md`
- **ER图**：可使用 Navicat 或 PowerDesigner 导入后自动生成
- **数据字典**：详见设计文档第八章节

---

## ⚠️ 注意事项

### 1. 密码安全
- 默认管理员密码需要使用 BCrypt 等算法加密
- 建议密码强度：至少8位，包含大小写字母、数字、特殊字符

### 2. 字符集
- 数据库必须使用 `utf8mb4` 字符集
- 支持存储 emoji 等特殊字符

### 3. 时区设置
```sql
-- 查看当前时区
SELECT @@global.time_zone, @@session.time_zone;

-- 设置时区为东八区
SET GLOBAL time_zone = '+8:00';
SET SESSION time_zone = '+8:00';
```

### 4. 索引优化
- 根据实际业务量调整索引
- 定期分析慢查询日志
- 考虑使用 EXPLAIN 分析查询计划

### 5. 备份策略
- **每日备份**：增量备份事务日志
- **每周备份**：完整备份数据库
- **定期测试**：验证备份文件可恢复性

---

## 🔧 性能优化建议

### 1. 分表策略
对于高频写入的表建议分表：
- `edu_score`：按年分区（预留，用于成绩管理）
- `sys_log`：按月分表（预留，用于操作日志）

### 2. 缓存策略
建议缓存以下数据：
- 菜单树结构
- 年级、科目等基础数据
- 角色权限信息
- 审批流程配置

### 3. 读写分离
- 主库：处理写操作
- 从库：处理读操作（报表、查询）

---

## 📞 技术支持

如有问题，请联系：
- 邮箱：support@example.com
- 文档：查看 `数据库设计文档.md`

---

## 📄 许可证

本项目仅供学习和参考使用。

---

**最后更新时间**：2024-11-15
