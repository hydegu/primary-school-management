# 小学教务管理系统 - ER关系图

## 主要实体关系图（Mermaid格式）

```mermaid
erDiagram
    %% 系统管理模块
    sys_user ||--o{ sys_user_role : "用户-角色"
    sys_role ||--o{ sys_user_role : "角色-用户"
    sys_role ||--o{ sys_role_menu : "角色-菜单"
    sys_menu ||--o{ sys_role_menu : "菜单-角色"
    
    %% 用户与角色实体关联
    sys_user ||--|| edu_teacher : "1对1"
    sys_user ||--|| edu_student : "1对1"
    sys_user ||--|| edu_parent : "1对1"
    
    
    %% 年级、班级、学生层级关系
    edu_grade ||--o{ edu_class : "1对多"
    edu_class ||--o{ edu_student : "1对多"
    edu_teacher ||--o{ edu_class : "班主任-班级"
    
    %% 课程相关关系
    edu_subject ||--o{ edu_course : "1对多"
    edu_class ||--o{ edu_course : "1对多"
    edu_teacher ||--o{ edu_course : "任课教师"
    edu_course ||--o{ edu_schedule : "1对多"
    
    %% 审批流程关系
    flow_process ||--o{ flow_approval : "1对多"
    flow_approval ||--o{ flow_approval_node : "1对多"
    flow_approval ||--|| biz_leave : "1对1"
    flow_approval ||--|| biz_course_change : "1对1"
    flow_approval ||--|| biz_course_swap : "1对1"
    flow_approval ||--|| biz_class_transfer : "1对1"
    
    %% 学生业务关系
    edu_student ||--o{ biz_leave : "1对多"
    edu_student ||--o{ biz_class_transfer : "1对多"
    edu_class ||--o{ biz_leave : "1对多"
    
    %% 教师业务关系
    edu_teacher ||--o{ biz_course_change : "1对多"
    edu_teacher ||--o{ biz_course_swap : "申请人"
    
    %% 表定义
    sys_user {
        bigint id PK
        varchar username UK
        varchar password
        varchar real_name
        tinyint user_type
        tinyint status
    }
    
    sys_role {
        bigint id PK
        varchar role_name
        varchar role_code UK
        varchar role_desc
    }
    
    sys_menu {
        bigint id PK
        bigint parent_id
        varchar menu_name
        tinyint menu_type
        varchar route_path
    }
    
    edu_teacher {
        bigint id PK
        bigint user_id UK
        varchar teacher_no UK
        varchar teacher_name
        varchar title
        tinyint status
    }
    
    edu_student {
        bigint id PK
        bigint user_id UK
        varchar student_no UK
        varchar student_name
        bigint class_id FK
        bigint grade_id FK
        tinyint status
    }
    
    edu_parent {
        bigint id PK
        bigint user_id UK
        varchar parent_name
        varchar phone
    }
    
    edu_grade {
        bigint id PK
        varchar grade_name
        int grade_level
        varchar school_year
    }
    
    edu_class {
        bigint id PK
        varchar class_no UK
        varchar class_name
        bigint grade_id FK
        bigint head_teacher_id FK
        int current_students
    }
    
    edu_subject {
        bigint id PK
        varchar subject_name
        varchar subject_code UK
    }
    
    edu_course {
        bigint id PK
        varchar course_name
        bigint subject_id FK
        bigint class_id FK
        bigint teacher_id FK
        varchar semester
    }
    
    edu_schedule {
        bigint id PK
        bigint course_id FK
        tinyint week_day
        tinyint period
        varchar classroom
    }
    
    flow_approval {
        bigint id PK
        varchar approval_no UK
        bigint process_id FK
        bigint apply_user_id FK
        tinyint approval_status
    }
    
    biz_leave {
        bigint id PK
        varchar leave_no UK
        bigint student_id FK
        bigint class_id FK
        tinyint leave_type
        date start_date
        date end_date
    }
```

---

## 详细模块关系图

### 1. 用户权限模块

```mermaid
graph LR
    A[sys_user 用户表] -->|M:N| B[sys_role 角色表]
    B -->|M:N| C[sys_menu 菜单表]
    A -.关联.-> D[sys_user_role 关联表]
    B -.关联.-> D
    B -.关联.-> E[sys_role_menu 关联表]
    C -.关联.-> E
```

### 2. 教务管理模块

```mermaid
graph TD
    A[edu_grade 年级表] -->|1:N| B[edu_class 班级表]
    B -->|1:N| C[edu_student 学生表]
    D[edu_teacher 教师表] -->|班主任| B
    D -->|任课教师| E[edu_course 课程表]
    F[edu_subject 科目表] -->|1:N| E
    B -->|1:N| E
    E -->|1:N| G[edu_schedule 排课表]
    
    H[sys_user 用户表] -.1:1.-> D
    H -.1:1.-> C
    H -.1:1.-> I[edu_parent 家长表]
    
    C -.M:N.-> I
```

### 3. 审批流程模块

```mermaid
graph TD
    A[flow_process 流程配置] -->|1:N| B[flow_approval 审批记录]
    B -->|1:N| C[flow_approval_node 审批节点]
    
    B -.关联业务.-> D[biz_leave 请假申请]
    B -.关联业务.-> E[biz_course_change 调课申请]
    B -.关联业务.-> F[biz_course_swap 换课申请]
    B -.关联业务.-> G[biz_class_transfer 调班申请]
    
    H[edu_student 学生] -->|申请| D
    H -->|申请| G
    I[edu_teacher 教师] -->|申请| E
    I -->|申请| F
```

---

## 核心业务流程图

### 学生请假流程

```mermaid
sequenceDiagram
    participant S as 学生/家长
    participant L as biz_leave表
    participant A as flow_approval表
    participant N as flow_approval_node表
    participant T as 班主任
    
    S->>L: 1. 提交请假申请
    L->>A: 2. 创建审批记录
    A->>N: 3. 创建审批节点
    N->>T: 4. 通知班主任审批
    T->>N: 5. 提交审批意见
    N->>A: 6. 更新审批状态
    A->>L: 7. 更新请假状态
    L->>S: 8. 通知审批结果
```

### 教师调课流程

```mermaid
sequenceDiagram
    participant T as 教师
    participant C as biz_course_change表
    participant A as flow_approval表
    participant M as 教务主任
    participant S as edu_schedule表
    
    T->>C: 1. 提交调课申请
    C->>A: 2. 创建审批记录
    A->>M: 3. 通知教务主任
    M->>A: 4. 审批通过
    A->>S: 5. 更新课程表
    S->>T: 6. 通知调课成功
```

### 学生调班流程

```mermaid
sequenceDiagram
    participant S as 学生/家长
    participant T as biz_class_transfer表
    participant A as flow_approval表
    participant OT as 原班主任
    participant NT as 新班主任
    participant C as edu_class表
    participant ST as edu_student表
    
    S->>T: 1. 提交调班申请
    T->>A: 2. 创建审批记录
    A->>OT: 3. 原班主任审批
    A->>NT: 4. 新班主任审批
    NT->>A: 5. 审批通过
    A->>C: 6. 更新班级学生数
    A->>ST: 7. 更新学生班级
    ST->>S: 8. 通知调班成功
```

---

## 数据流向图

```mermaid
graph LR
    A[前端页面] -->|HTTP请求| B[后端API]
    B -->|查询/更新| C[(MySQL数据库)]
    
    C -->|用户认证| D[sys_user]
    C -->|权限验证| E[sys_role + sys_menu]
    C -->|业务查询| F[edu_* 业务表]
    C -->|流程审批| G[flow_* 流程表]
    
    B -->|缓存| H[Redis]
    H -.热点数据.-> I[菜单树]
    H -.热点数据.-> J[字典数据]
    H -.热点数据.-> K[用户权限]
```

---

## 表分类与数量统计

```mermaid
pie title 数据库表分类统计
    "系统管理模块" : 5
    "教务核心模块" : 11
    "业务流程模块" : 7
    "系统配置模块" : 5
```

---

## 如何查看ER图

### 方法一：在线Mermaid编辑器
1. 访问 https://mermaid.live/
2. 复制上述代码块中的 mermaid 代码
3. 粘贴到编辑器中即可查看

### 方法二：VS Code插件
1. 安装 "Markdown Preview Mermaid Support" 插件
2. 在 VS Code 中打开本文件
3. 按 `Ctrl+Shift+V` 预览

### 方法三：使用数据库建模工具
1. **Navicat**：导入数据库后，使用"逆向工程"功能自动生成ER图
2. **PowerDesigner**：导入SQL脚本生成物理数据模型
3. **DBeaver**：自带ER图功能，可视化展示表关系

---

## 表关系说明

### 一对一关系 (1:1)
- `sys_user` ←→ `edu_teacher` （用户与教师）
- `sys_user` ←→ `edu_student` （用户与学生）
- `sys_user` ←→ `edu_parent` （用户与家长）
- `flow_approval` ←→ `biz_leave` （审批与请假）

### 一对多关系 (1:N)
- `edu_grade` → `edu_class` （年级包含多个班级）
- `edu_class` → `edu_student` （班级包含多个学生）
- `edu_teacher` → `edu_class` （教师担任多个班级班主任）
- `edu_teacher` → `edu_course` （教师教授多门课程）
- `edu_course` → `edu_schedule` （课程对应多个时间段）

### 多对多关系 (M:N)
- `sys_user` ←→ `sys_role` （通过 sys_user_role）
- `sys_role` ←→ `sys_menu` （通过 sys_role_menu）
- `edu_student` ←→ `edu_parent` （通过 edu_student_parent）

---

**提示**：建议使用专业的数据库建模工具查看更详细的ER图，本文档提供的是简化版本。
