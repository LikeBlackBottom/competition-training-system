# Codex 后端开发说明与提示词

## 1. 当前项目背景

项目名称：竞赛训练过程管理系统

项目根目录：

```text
competition-training-system
├─ backend
├─ docs
├─ frontend-admin
├─ frontend-team
└─ 前端原型设计
```

当前状态：

- 前端已经开发完成。
- 管理端目录：`frontend-admin`
- 队员端目录：`frontend-team`
- PostgreSQL 数据库已经创建完成。
- 数据库初始化 SQL 已经由人工准备完成，不需要 Codex 从零重新设计数据库。
- Codex 本次任务是开发 `backend`，并让后端成功对接现有数据库和现有前端。

## 2. 最重要的开发原则

请严格遵守：

1. 不要重写前端。
2. 不要破坏 `frontend-admin` 和 `frontend-team`。
3. 不要修改 `前端原型设计`。
4. 不要删除、重建、清空现有 PostgreSQL 数据库。
5. 不要随意改已有表名、字段名、约束名。
6. 后端必须优先兼容现有数据库结构。
7. 后端必须优先兼容现有前端接口调用。
8. 如果数据库字段、前端字段、本文档字段不完全一致，请优先读取真实代码和真实 SQL 后做兼容。
9. 队员端只能新增记录，不能查看历史、不能修改、不能删除。
10. 管理端可以查看、筛选、统计、导出、作废记录。
11. 展示时长单位统一为分钟，不允许使用小时作为业务展示单位。

## 3. 本次开发目标

请在 `backend` 中完成 Spring Boot 后端开发，实现以下目标：

```text
队员端邀请码登录
→ 队员端获取表单选项
→ 队员端提交代码展示时长记录
→ 管理端登录
→ 管理端 Dashboard 统计
→ 管理端队伍管理
→ 管理端队员管理
→ 管理端技能树管理
→ 管理端展示时长记录管理
→ 管理端作废记录
→ 管理端问题闭环管理
→ 管理端 Excel 导出
```

最终要求：

```text
backend 能启动
backend 能连接 PostgreSQL
frontend-team 能成功调用后端接口
frontend-admin 能成功调用后端接口
展示时长记录可以从队员端提交，并在管理端看到
```

## 4. 技术栈要求

如果 `backend` 已经是 Spring Boot 项目，请基于现有结构继续开发。

如果 `backend` 为空或不完整，请使用以下技术栈：

```text
Java 17 或 Java 21
Spring Boot 3.x
Maven
PostgreSQL Driver
MyBatis Plus 或 MyBatis
Spring Web
Spring Validation
Spring Security
JWT
Lombok
Apache POI
```

建议包名：

```text
com.competition.training
```

建议后端分层：

```text
backend
├─ src/main/java/com/competition/training
│  ├─ CompetitionTrainingApplication.java
│  ├─ common
│  │  ├─ ApiResponse.java
│  │  ├─ PageResponse.java
│  │  ├─ BusinessException.java
│  │  ├─ GlobalExceptionHandler.java
│  │  └─ ResultCode.java
│  ├─ config
│  │  ├─ CorsConfig.java
│  │  ├─ SecurityConfig.java
│  │  └─ MyBatisPlusConfig.java
│  ├─ security
│  │  ├─ JwtUtil.java
│  │  ├─ JwtAuthFilter.java
│  │  └─ LoginUser.java
│  ├─ module
│  │  ├─ auth
│  │  ├─ team
│  │  ├─ member
│  │  ├─ skill
│  │  ├─ timelog
│  │  ├─ issue
│  │  ├─ dashboard
│  │  └─ export
│  └─ util
│     └─ ExcelExportUtil.java
└─ src/main/resources
   ├─ application.yml
   └─ mapper
```

## 5. 数据库说明

数据库类型：

```text
PostgreSQL
```

数据库名称：

```text
competition_training
```

默认 schema：

```text
public
```

重要说明：

```text
数据库已经准备好。
不要重新创建数据库。
不要 DROP DATABASE。
不要 DROP TABLE。
不要 TRUNCATE 业务表。
不要清空现有数据。
```

请优先检查以下位置是否已有 SQL 文件或数据库文档：

```text
docs
backend/src/main/resources
项目根目录下的 .sql 文件
```

如果发现现有 SQL 文件，请读取它，并以它为准生成 Entity、Mapper、DTO、VO。

如果发现本文档字段与现有 SQL 不一致：

```text
优先级：真实数据库 SQL > 前端真实接口 > 本文档描述
```

## 6. 可能存在的核心表

数据库中预计包含以下业务表：

```text
admin_users
teams
members
skill_categories
skill_tasks
time_logs
issues
```

### 6.1 admin_users 管理员表

常见字段：

```text
id
username
password_hash
name
role
status
created_at
updated_at
```

要求：

- 密码使用 BCrypt。
- 不允许明文密码。
- 如果数据库已有默认管理员，直接使用。
- 如果没有默认管理员，可以提供 seed SQL，但不要强制覆盖已有数据。

### 6.2 teams 队伍表

常见字段：

```text
id
institution_name
team_name
track_name
login_code
status
created_at
updated_at
```

要求：

- `login_code` 唯一。
- 队员端通过 `login_code` 登录。
- 停用队伍不能登录。

### 6.3 members 队员表

常见字段：

```text
id
team_id
name
role
status
sort_order
created_at
updated_at
```

要求：

- 队员必须归属某个队伍。
- 队员端提交记录时，`member_id` 必须属于当前登录队伍。

### 6.4 skill_categories 技能模块表

常见字段：

```text
id
name
track_name
sort_order
status
created_at
updated_at
```

要求：

- 队员端只显示启用状态模块。
- 管理端可以管理模块。

### 6.5 skill_tasks 技能点表

常见字段：

```text
id
category_id
name
description
difficulty_level
score_weight
sort_order
status
created_at
updated_at
```

要求：

- 技能点必须归属某个模块。
- 队员端只显示启用状态技能点。
- 管理端可以管理技能点。

### 6.6 time_logs 展示时长记录表

常见字段：

```text
id
team_id
member_id
task_id
record_date
duration_minutes
progress_status
result_desc
problem_desc
need_support
is_voided
void_reason
created_at
updated_at
```

业务含义：

```text
duration_minutes 表示代码展示 / 代码讲解 / 项目展示时长，单位是分钟。
```

要求：

- 队员端只能新增。
- 队员端不能查询历史。
- 队员端不能修改。
- 队员端不能删除。
- 管理端可以查询。
- 管理端可以作废。
- 不允许物理删除。
- Dashboard 统计必须排除 `is_voided = true` 的记录。

### 6.7 issues 问题闭环表

常见字段：

```text
id
team_id
task_id
member_id
title
description
severity
status
owner_member_id
solution
created_at
updated_at
resolved_at
```

要求：

- status 建议支持：待处理、处理中、已解决、已关闭。
- severity 建议支持：低、中、高。
- 管理端可以查看、筛选、更新。

## 7. 统一响应格式

所有 JSON 接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

失败返回：

```json
{
  "code": 400,
  "message": "错误原因",
  "data": null
}
```

分页返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "pageSize": 10
  }
}
```

JSON 字段必须使用 camelCase。

例如：

```text
duration_minutes -> durationMinutes
need_support -> needSupport
record_date -> recordDate
created_at -> createdAt
```

## 8. 权限设计

系统有两种登录方式：

```text
队员端：邀请码登录
管理端：账号密码登录
```

### 8.1 队员端权限

队员端登录成功后返回 team token。

team token 中至少包含：

```text
role=TEAM
teamId
```

队员端只允许访问：

```text
POST /api/team/auth/code-login
GET  /api/team/form-options
POST /api/team/time-logs
```

队员端禁止访问：

```text
GET    /api/team/time-logs
PUT    /api/team/time-logs/{id}
DELETE /api/team/time-logs/{id}
```

如果队员端请求了未授权接口，返回 403。

### 8.2 管理端权限

管理端登录成功后返回 admin token。

admin token 中至少包含：

```text
role=ADMIN
adminId
```

管理端允许访问：

```text
/api/admin/**
```

## 9. CORS 要求

允许以下前端地址访问后端：

```text
http://localhost:5173
http://localhost:5174
```

5173 是管理端。
5174 是队员端。

## 10. application.yml 示例

请根据本机 PostgreSQL 账号密码调整。

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/competition_training
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

jwt:
  secret: competition-training-secret-key-change-me
  expire-hours: 24

cors:
  allowed-origins:
    - http://localhost:5173
    - http://localhost:5174
```

如果项目中已有配置，请不要强行覆盖，应该兼容现有配置。

## 11. 队员端接口

### 11.1 邀请码登录

```text
POST /api/team/auth/code-login
```

请求：

```json
{
  "loginCode": "CQSX0075"
}
```

返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "team-token",
    "team": {
      "id": 1,
      "institutionName": "重庆财经职业学院",
      "teamName": "CQSX-A队",
      "trackName": "新一代信息技术"
    }
  }
}
```

要求：

- loginCode 必须存在。
- 队伍必须启用。
- 登录成功返回 JWT。
- 不需要队员个人账号。

### 11.2 获取提交表单选项

```text
GET /api/team/form-options
```

请求头：

```text
Authorization: Bearer team-token
```

返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "team": {
      "id": 1,
      "institutionName": "重庆财经职业学院",
      "teamName": "CQSX-A队",
      "trackName": "新一代信息技术"
    },
    "members": [
      {
        "id": 1,
        "name": "张宇航",
        "role": "队长"
      }
    ],
    "categories": [
      {
        "id": 1,
        "name": "技能实操",
        "tasks": [
          {
            "id": 1,
            "name": "模型训练"
          }
        ]
      }
    ]
  }
}
```

要求：

- members 只返回当前队伍成员。
- categories 返回启用模块。
- tasks 返回启用技能点。
- 按 sortOrder 排序。

### 11.3 提交展示时长记录

```text
POST /api/team/time-logs
```

请求头：

```text
Authorization: Bearer team-token
```

请求：

```json
{
  "memberId": 1,
  "taskId": 2,
  "recordDate": "2026-05-20",
  "durationMinutes": 8,
  "progressStatus": "已完成",
  "resultDesc": "完成项目技术方案代码展示",
  "problemDesc": "无",
  "needSupport": false
}
```

返回：

```json
{
  "code": 200,
  "message": "展示时长记录已提交",
  "data": {
    "success": true
  }
}
```

校验：

```text
durationMinutes 必须是分钟
durationMinutes 最小 1
durationMinutes 最大 180
durationMinutes 必须是整数
memberId 必须属于当前 teamId
taskId 必须存在且启用
```

## 12. 管理端接口

### 12.1 管理员登录

```text
POST /api/admin/auth/login
```

请求：

```json
{
  "username": "admin",
  "password": "admin123456"
}
```

返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "admin-token",
    "admin": {
      "id": 1,
      "username": "admin",
      "name": "系统管理员",
      "role": "SUPER_ADMIN"
    }
  }
}
```

### 12.2 Dashboard

```text
GET /api/admin/dashboard
```

返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summary": {
      "totalTeams": 4,
      "submittedTodayTeams": 3,
      "notSubmittedTodayTeams": 1,
      "totalDisplayMinutes": 1280,
      "pendingIssues": 5
    },
    "teamRanking": [
      {
        "teamName": "CQSX-A队",
        "institutionName": "重庆财经职业学院",
        "displayMinutes": 360
      }
    ],
    "skillDistribution": [
      {
        "skillName": "模型训练",
        "displayMinutes": 180
      }
    ],
    "dailyTrend": [
      {
        "date": "2026-05-20",
        "displayMinutes": 300
      }
    ],
    "recentLogs": []
  }
}
```

要求：

- 所有时间单位都是分钟。
- 不返回 h、hour、小时。
- 排除已作废记录。

### 12.3 队伍管理

```text
GET  /api/admin/teams
POST /api/admin/teams
PUT  /api/admin/teams/{id}
```

查询参数：

```text
keyword
status
page
pageSize
```

要求：

- loginCode 唯一。
- 支持启用、停用。
- 不做物理删除。

### 12.4 队员管理

```text
GET  /api/admin/members
POST /api/admin/members
PUT  /api/admin/members/{id}
```

查询参数：

```text
teamId
keyword
status
page
pageSize
```

要求：

- 队员必须归属队伍。
- 同一队伍下队员姓名建议不重复。

### 12.5 技能树管理

```text
GET  /api/admin/skills/categories
POST /api/admin/skills/categories
PUT  /api/admin/skills/categories/{id}

GET  /api/admin/skills/tasks
POST /api/admin/skills/tasks
PUT  /api/admin/skills/tasks/{id}
```

查询参数：

```text
categoryId
status
```

要求：

- 停用模块或技能点后，队员端不显示。
- 不物理删除。

### 12.6 展示时长记录管理

```text
GET /api/admin/time-logs
```

查询参数：

```text
startDate
endDate
teamId
memberId
categoryId
taskId
needSupport
progressStatus
isVoided
page
pageSize
```

返回字段建议：

```text
id
recordDate
institutionName
teamName
memberName
categoryName
taskName
durationMinutes
progressStatus
resultDesc
problemDesc
needSupport
isVoided
createdAt
```

要求：

- durationMinutes 单位是分钟。
- 默认按 recordDate、createdAt 倒序。
- 支持筛选作废与未作废记录。

### 12.7 作废展示时长记录

```text
POST /api/admin/time-logs/{id}/void
```

请求：

```json
{
  "voidReason": "误提交"
}
```

要求：

- 不允许物理删除。
- 只设置 isVoided = true。
- 保存 voidReason。
- 已作废记录不能重复作废。

### 12.8 问题闭环

```text
GET  /api/admin/issues
POST /api/admin/issues
PUT  /api/admin/issues/{id}
```

查询参数：

```text
teamId
status
severity
page
pageSize
```

要求：

- status 支持：待处理、处理中、已解决、已关闭。
- severity 支持：低、中、高。
- 已解决时可填写 solution 和 resolvedAt。

### 12.9 Excel 导出

```text
GET /api/admin/export/time-logs
```

筛选参数同 time-logs：

```text
startDate
endDate
teamId
memberId
categoryId
taskId
needSupport
progressStatus
isVoided
```

Excel 列名：

```text
日期
院校
队伍
队员
模块
技能点
展示时长（分钟）
完成状态
今日产出
遇到问题
是否需要协助
记录状态
提交时间
```

要求：

- 使用 Apache POI。
- 文件名：展示时长记录_yyyyMMddHHmmss.xlsx
- 中文文件名正确编码。
- Content-Type 正确。

## 13. 数据校验

请使用 Spring Validation。

重点校验：

```text
loginCode 非空
username 非空
password 非空
durationMinutes 非空
durationMinutes >= 1
durationMinutes <= 180
durationMinutes 必须是整数
recordDate 非空
memberId 非空
taskId 非空
needSupport 必须是 boolean
```

progressStatus 建议允许：

```text
未开始
进行中
已完成
已掌握
部分完成
```

## 14. 异常处理

请实现统一异常处理。

需要处理：

```text
参数错误
登录失败
邀请码不存在
队伍已停用
token 无效
权限不足
数据不存在
状态不允许
数据库异常
```

不要把 Java 堆栈直接返回给前端。

## 15. 前端对接优先级

在正式编码前，请先扫描：

```text
frontend-admin/src/api
frontend-admin/src/types
frontend-admin/src/mock
frontend-team/src/api
frontend-team/src/types
frontend-team/src/mock
```

然后列出：

```text
前端实际请求路径
前端实际请求参数
前端实际返回字段
当前文档中的接口差异
```

如果发现差异，请以后端兼容前端为最高优先级。

不要写一个“看起来正确但前端调不通”的后端。

## 16. 开发完成后的自检清单

完成后请检查：

```text
1. backend 能启动。
2. backend 能连接 PostgreSQL。
3. mvn clean package 通过。
4. 管理端可以登录。
5. 队员端可以邀请码登录。
6. 队员端可以获取成员和技能树。
7. 队员端可以提交展示时长记录。
8. 队员端没有任何查看、修改、删除记录接口。
9. 管理端 Dashboard 有真实统计。
10. 管理端展示时长记录能看到队员端提交的数据。
11. 管理端可以作废记录。
12. 作废记录不参与 Dashboard 统计。
13. Excel 导出可以下载。
14. 所有时间单位都是分钟。
15. CORS 不报错。
16. JSON 字段是 camelCase。
17. 不物理删除展示时长记录。
```

## 17. README 要求

请在 `backend/README.md` 中写清楚：

```text
1. 后端技术栈。
2. 如何修改 PostgreSQL 连接配置。
3. 如何启动后端。
4. 默认端口。
5. 默认管理员账号密码。
6. 队员端测试邀请码。
7. 前端联调地址。
8. 主要接口说明。
9. 常见问题排查。
```

