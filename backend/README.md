# 竞赛训练过程管理系统 Backend

## 后端技术栈

- Java 17
- Spring Boot 3.3.5
- PostgreSQL
- MyBatis Plus starter + Spring JDBC 数据访问
- Spring Security + JWT
- Spring Validation
- Apache POI Excel 导出

## 数据库连接配置位置

配置文件：`backend/src/main/resources/application.properties`

当前配置：

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/competition_training
spring.datasource.username=postgres
spring.datasource.password=HouQing2580
spring.datasource.driver-class-name=org.postgresql.Driver
```

已保留 MyBatis Plus 配置：

```properties
mybatis-plus.configuration.map-underscore-to-camel-case=true
mybatis-plus.mapper-locations=classpath*:mapper/**/*.xml
```

## 如何启动后端

```bash
cd backend
./mvnw spring-boot:run
```

Windows 也可以使用：

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

## 默认端口

默认端口：`8080`

## 默认管理员账号密码

管理员登录读取数据库 `admin_users` 表，并使用 `password_hash` 进行 BCrypt 校验。

推荐默认账号：`admin`

推荐默认密码：`admin123456`

如果当前数据库已有管理员，请以现有 `admin_users` 数据为准；后端不会重建、清空或覆盖管理员数据。

## 队员端测试邀请码

队员邀请码读取数据库 `teams.login_code`，可使用数据库已存在的启用队伍邀请码测试。

前端 mock 中常用测试邀请码：

- `CQSX0075`
- `CQGY0088`
- `CQCG0103`
- `SCXX0056`
- `CDZY0091`
- `CQSX0076`

## 前端联调地址

CORS 已允许：

- 管理端：`http://localhost:5173`
- 队员端：`http://localhost:5174`

后端地址：

- `http://localhost:8080`

## 扫描确认结果

已读取：

- `docs/Codex后端开发说明与提示词_已有数据库版.md`
- `frontend-admin/src/api`
- `frontend-admin/src/types`
- `frontend-admin/src/mock`
- `frontend-team/src/api`
- `frontend-team/src/types`
- `frontend-team/src/mock`

项目内未发现独立 `.sql` 文件；真实表结构来自 IDE 数据源元数据。核心表包括：

- `admin_users`
- `teams`
- `members`
- `skill_categories`
- `skill_tasks`
- `daily_reports`
- `time_logs`
- `issues`
- `audit_logs`

关键字段按真实数据库元数据实现：

- `time_logs.duration_minutes` 对应 `durationMinutes`，业务单位固定为分钟。
- `time_logs.is_voided` 用于作废，不做物理删除。
- `skill_tasks` 包含 `expected_minutes`、`difficulty_level`、`score_weight`。
- `issues` 表没有 `owner_member_id`，后端不依赖该字段。

前端字段差异已兼容：

- 管理端队伍同时返回 `institutionName/schoolName`、`loginCode/inviteCode`、`trackName/track`。
- 管理端展示时长同时返回 `durationMinutes/hours`，但数值仍是分钟。
- 队员端登录同时返回 `team` 和 `teamInfo`。
- 队员端表单同时返回 `categories` 和 `modules`。
- 队员端提交同时兼容 `taskId/skillId`、`progressStatus/completionStatus`、`resultDesc/todayOutput`、`problemDesc/issue`、`needSupport/needHelp`。

## 主要接口说明

队员端：

- `POST /api/team/auth/code-login`
- `GET /api/team/form-options`
- `POST /api/team/time-logs`

队员端未实现也未开放：

- `GET /api/team/time-logs`
- `PUT /api/team/time-logs/{id}`
- `DELETE /api/team/time-logs/{id}`

管理端：

- `POST /api/admin/auth/login`
- `GET /api/admin/dashboard`
- `GET /api/admin/teams`
- `POST /api/admin/teams`
- `PUT /api/admin/teams/{id}`
- `GET /api/admin/members`
- `POST /api/admin/members`
- `PUT /api/admin/members/{id}`
- `GET /api/admin/skills/categories`
- `POST /api/admin/skills/categories`
- `PUT /api/admin/skills/categories/{id}`
- `GET /api/admin/skills/tasks`
- `POST /api/admin/skills/tasks`
- `PUT /api/admin/skills/tasks/{id}`
- `GET /api/admin/time-logs`
- `POST /api/admin/time-logs/{id}/void`
- `GET /api/admin/issues`
- `POST /api/admin/issues`
- `PUT /api/admin/issues/{id}`
- `GET /api/admin/export/time-logs`

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

分页：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 0,
    "page": 1,
    "pageSize": 10
  }
}
```

## 常见问题排查

- 登录失败：确认 `admin_users.password_hash` 是 BCrypt 密文，且用户 `status=1`。
- 邀请码无效：确认 `teams.login_code` 存在，且队伍 `status=1`。
- 队员提交失败：确认 `member_id` 属于当前登录队伍，`skill_tasks.status=1`。
- 展示时长校验失败：`durationMinutes` 必须是 1 到 180 的整数，单位是分钟。
- Dashboard 数字不一致：统计会排除 `time_logs.is_voided=true` 的记录。
- Excel 下载乱码：接口已使用 `filename*=UTF-8''...` 设置中文文件名。
- CORS 报错：确认前端运行在 `http://localhost:5173` 或 `http://localhost:5174`。
