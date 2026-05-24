# 🏆 竞赛训练管理系统 CTMS

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.5-6ee7ff?logo=vuedotjs" alt="Vue" />
  <img src="https://img.shields.io/badge/TypeScript-6.0-6ee7ff?logo=typescript" alt="TypeScript" />
  <img src="https://img.shields.io/badge/Vite-8.0-ff7adf?logo=vite" alt="Vite" />
  <img src="https://img.shields.io/badge/Element_Plus-2.14-6ee7ff?logo=elementplus" alt="Element Plus" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.3-ff7adf?logo=springboot" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Java-17-6ee7ff?logo=openjdk" alt="Java" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-ff7adf?logo=postgresql" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/ECharts-6.0-6ee7ff?logo=apacheecharts" alt="ECharts" />
</p>

<p align="center">
  <b>🎀 少女粉蓝 × 👾 像素科技风 — 双端竞赛训练全流程管理平台</b>
</p>

---

## 📖 项目简介

**竞赛训练管理系统 (Competition Training Management System)** 是一个面向技能竞赛训练场景的全栈管理平台。系统采用 **双前端 + 统一后端** 架构，为管理员和参赛队员分别提供专属操作界面。

- 🛠️ **管理端 (frontend-admin)** — 队伍/队员/技能树管理、训练时长追踪、问题闭环、数据导出、可视化仪表盘
- 🎯 **队员端 (frontend-team)** — 邀请码登录、训练记录提交、进度追踪
- ⚙️ **后端 (backend)** — Spring Boot RESTful API，JWT 鉴权，PostgreSQL 持久化

> 🎨 UI 主题：少女粉蓝 + 像素科技风 — 深色背景、霓虹发光、像素边框、扫描线叠加，专业且充满活力的视觉体验。

---

## 🧱 技术栈

| 层级 | 技术 | 说明 |
|:---:|------|------|
| 🖥️ 前端框架 | Vue 3.5 + TypeScript 6.0 | Composition API `<script setup>` |
| 🧭 路由 | Vue Router 5.0 | 双端独立路由 + 导航守卫 |
| 📦 状态管理 | Pinia 3.0 | localStorage 持久化 token |
| 🎨 UI 组件库 | Element Plus 2.14 | 全局深色主题覆写 |
| 📊 图表 | ECharts 6.0 + vue-echarts 8.0 | 仪表盘数据可视化 |
| 🌐 HTTP | Axios 1.16 | 拦截器自动注入 JWT |
| ⚡ 构建 | Vite 8.0 | 毫秒级 HMR |
| 🎭 样式 | SCSS (Sass 1.99) | 赛博朋克像素主题 |
| ☕ 后端 | Spring Boot 3.3.5 | Java 17, Maven |
| 🔐 认证 | Spring Security + JJWT 0.12 | 双角色 JWT (ADMIN/TEAM) |
| 🗄️ 数据库 | PostgreSQL 16 | JdbcTemplate 原生 SQL |
| 📎 Excel | Apache POI 5.3 | 训练数据导出 |

---

## 📁 项目结构

```
📁 competition-training-system/
├── 📁 backend/                          # Spring Boot 后端
│   ├── pom.xml                          # Maven 依赖配置
│   └── src/main/java/com/competition/training/
│       ├── CompetitionTrainingApplication.java   # 🚀 启动入口
│       ├── common/                       # 通用组件 (ApiResponse, 异常处理, 分页)
│       ├── config/                       # 配置 (CORS, Security, MyBatisPlus)
│       ├── module/                       # 业务模块
│       │   ├── auth/                     # 🔐 认证 (管理员登录 + 队员邀请码登录)
│       │   ├── dashboard/                # 📊 仪表盘聚合统计
│       │   ├── export/                   # 📎 Excel 导出
│       │   ├── issue/                    # 🐛 问题闭环 CRUD
│       │   ├── member/                   # 👤 队员管理 CRUD
│       │   ├── skill/                    # 🌳 技能树 (分类 + 任务) CRUD
│       │   ├── team/                     # 🏢 队伍管理 CRUD + 队员端接口
│       │   └── timelog/                  # ⏱️ 培训记录查询 + 作废
│       ├── security/                     # 🔒 JWT 过滤器 + 工具类
│       └── util/                         # 🧰 ExcelExportUtil
│
├── 📁 frontend-admin/                    # 🛠️ 管理端 SPA (端口 5173)
│   └── src/
│       ├── api/                          # 📡 8 个 API 模块 (auth, teams, members, skills...)
│       ├── assets/styles/                # 🎨 SCSS 主题文件
│       │   ├── variables.scss            # 色彩变量 (粉蓝调色板)
│       │   ├── cyberpunk.scss            # 赛博朋克基础样式
│       │   ├── theme-pixel-girl.scss     # 👾 像素风少女主题覆写
│       │   └── global.scss               # 全局重置与排版
│       ├── layouts/AdminLayout.vue       # 📐 侧边栏 + 顶栏管理布局
│       ├── router/index.ts               # 🧭 8 条路由 (含守卫)
│       ├── stores/auth.ts                # 🔐 Pinia 认证 Store
│       ├── types/index.ts                # 🏷️ TypeScript 类型定义
│       └── views/                        # 📄 7 个业务页面
│           ├── login/AdminLogin.vue       # 🔑 管理员登录
│           ├── dashboard/Dashboard.vue    # 📊 数据看板 (ECharts × 4)
│           ├── teams/Teams.vue            # 🏢 队伍管理
│           ├── members/Members.vue        # 👤 队员管理
│           ├── skills/Skills.vue          # 🌳 技能树管理
│           ├── time-logs/TimeLogs.vue     # ⏱️ 培训时长记录
│           ├── issues/Issues.vue          # 🐛 问题闭环看板
│           └── exports/Exports.vue        # 📎 数据导出中心
│
├── 📁 frontend-team/                     # 🎯 队员端 SPA (端口 5174)
│   └── src/
│       ├── api/                          # 📡 API 模块 (team)
│       ├── assets/styles/                # 🎨 SCSS 主题文件 (同上结构)
│       ├── router/index.ts               # 🧭 3 条路由 (含守卫)
│       ├── stores/auth.ts                # 🔐 队员认证 Store
│       ├── types/index.ts                # 🏷️ TypeScript 类型定义
│       └── views/                        # 📄 3 个页面
│           ├── TeamLogin.vue             # 🔑 邀请码登录
│           ├── TeamSubmit.vue            # 📝 训练记录提交
│           └── TeamSuccess.vue           # ✅ 提交成功动画页
│
├── 📄 competition_training_clean.sql     # 🗄️ 完整数据库 DDL + 种子数据
├── 📄 前端接口修复.md                     # 📋 API 接入规范文档
└── 📄 README.md                          # 📖 本文件
```

---

## ✨ 功能矩阵

### 🛠️ 管理端 (frontend-admin)

| 模块 | 功能 | 亮点 |
|------|------|------|
| 🔑 **管理员登录** | 用户名/密码认证, JWT 鉴权 | 呼吸发光卡片, 像素装饰动画 |
| 📊 **数据看板** | 5 项核心统计 + 柱状图/折线图/饼图 | ECharts 自适应图例, 粉蓝色调色板 |
| 🏢 **队伍管理** | 增删改查, 启用/停用, 搜索筛选 | 邀请码高亮, 统计卡片实时刷新 |
| 👤 **队员管理** | 增删改查, 按队伍筛选, 角色标签 | 头像渐变, 角色徽章色标 |
| 🌳 **技能树管理** | 二级结构 (模块 → 技能点), 难度/权重 | 左侧分类面板, 难度星级评定 |
| ⏱️ **时长记录** | 多维度筛选, 记录作废, 累计统计 | 可展开式过滤面板, 时长汇总 |
| 🐛 **问题闭环** | 看板视图 (待处理→处理中→已解决→已关闭) | 严重度色标, 风控提示条 |
| 📎 **数据导出** | 5 类 Excel 导出模板 | 一键生成下载, 进度动画 |

### 🎯 队员端 (frontend-team)

| 模块 | 功能 | 亮点 |
|------|------|------|
| 🔑 **邀请码登录** | 队伍邀请码认证 | 像素边框输入框, 权限须知面板 |
| 📝 **训练提交** | 队员/模块/技能点级联选择, 时长录入 | 表单验证, 提交前确认提醒 |
| ✅ **提交成功** | 结果动画展示, 隐私提示 | 像素星光动画, 脉冲勾选特效 |

---

## 🗄️ 数据库模型

<details>
<summary><b>📊 9 张数据表 — 点击展开 ER 关系</b></summary>

```
┌─────────────────┐     ┌─────────────────┐     ┌──────────────────────┐
│   admin_users   │     │      teams      │     │       members        │
│─────────────────│     │─────────────────│     │──────────────────────│
│ id (PK)         │     │ id (PK)         │◄────│ team_id (FK)         │
│ username (UQ)   │     │ institution_name│     │ name                 │
│ password_hash   │     │ team_name       │     │ role                 │
│ name            │     │ track_name      │     │ sort_order           │
│ role            │     │ login_code (UQ) │     │ status               │
└───────┬─────────┘     │ status          │     └──────────┬───────────┘
        │               └────────┬────────┘                │
        │                        │                         │
        ▼                        ▼                         ▼
┌─────────────────┐     ┌──────────────────────┐     ┌─────────────────┐
│   audit_logs    │     │    daily_reports     │     │    time_logs    │
│─────────────────│     │──────────────────────│     │─────────────────│
│ admin_id (FK)───┘     │ team_id (FK,UQ+date) │     │ daily_report_id │
│ target_type     │     │ report_date          │     │ team_id (FK)    │
│ target_id       │     │ total_training_mins  │     │ member_id (FK)  │
│ action          │     │ summary              │     │ task_id (FK)────│──┐
│ before_data     │     │ problems             │     │ record_date     │  │
│ after_data      │     │ next_plan            │     │ duration_mins   │  │
│ reason          │     │ submitted_by         │     │ progress_status │  │
└─────────────────┘     └──────────────────────┘     │ result_desc     │  │
                                                     │ problem_desc    │  │
┌──────────────────┐    ┌──────────────────┐         │ need_support    │  │
│ skill_categories │    │   skill_tasks    │         │ score_self      │  │
│──────────────────│    │──────────────────│         │ is_voided       │  │
│ id (PK)          │◄───│ category_id (FK) │         │ void_reason     │  │
│ track_name       │    │ name             │◄────────┘                 │  │
│ name             │    │ description      │                           │  │
│ sort_order       │    │ expected_minutes │                           │  │
│ status           │    │ difficulty_level │                           │  │
└──────────────────┘    │ score_weight     │   ┌─────────────────┐     │
                        └──────────────────┘   │     issues      │     │
                                               │─────────────────│     │
                                               │ team_id (FK)    │     │
                                               │ task_id (FK)────│─────┘
                                               │ member_id (FK)  │
                                               │ title           │
                                               │ severity        │
                                               │ status          │
                                               │ solution        │
                                               └─────────────────┘
```
</details>

---

## 🚀 快速启动

### 📋 环境要求

| 工具 | 版本要求 |
|------|---------|
| ☕ JDK | 17+ |
| 🟢 Node.js | 18+ |
| 🐘 PostgreSQL | 16+ |
| 🔧 Maven | 3.8+ |

### 🗄️ 1. 初始化数据库

```bash
# 创建数据库
psql -U postgres -c "CREATE DATABASE competition_training;"

# 导入表结构和种子数据
psql -U postgres -d competition_training -f competition_training_clean.sql
```

### ☕ 2. 启动后端

```bash
cd backend
mvn spring-boot:run
# ✅ 后端运行在 http://localhost:8080
```

### 🛠️ 3. 启动管理端

```bash
cd frontend-admin
npm install
npm run dev
# ✅ 管理端运行在 http://localhost:5173/admin/
```

### 🎯 4. 启动队员端

```bash
cd frontend-team
npm install
npm run dev
# ✅ 队员端运行在 http://localhost:5174/team/
```

---

## 🔐 测试账号

| 角色 | 入口 | 登录凭据 |
|------|------|---------|
| 🛠️ 管理员 | `http://localhost:5173/admin/login` | `admin` / `admin123456` |
| 🎯 队员 | `http://localhost:5174/team/login` | 邀请码: `CQSX0075` |

---

## 📡 API 接口总览

### 🔐 认证

| 方法 | 端点 | 鉴权 | 说明 |
|------|------|:---:|------|
| POST | `/api/admin/auth/login` | — | 管理员用户名密码登录 |
| POST | `/api/team/auth/code-login` | — | 队员邀请码登录 |

### 🛠️ 管理端 (需 `ROLE_ADMIN`)

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/api/admin/dashboard` | 📊 仪表盘聚合数据 |
| GET/POST/PUT | `/api/admin/teams` | 🏢 队伍 CRUD |
| GET/POST/PUT | `/api/admin/members` | 👤 队员 CRUD |
| GET/POST/PUT | `/api/admin/skills/categories` | 📂 技能模块 CRUD |
| GET/POST/PUT | `/api/admin/skills/tasks` | 📝 技能任务 CRUD |
| GET | `/api/admin/time-logs` | ⏱️ 训练记录查询 |
| POST | `/api/admin/time-logs/{id}/void` | 🗑️ 作废记录 |
| GET/POST/PUT | `/api/admin/issues` | 🐛 问题闭环 CRUD |
| GET | `/api/admin/export/time-logs` | 📎 Excel 导出 |

### 🎯 队员端 (需 `ROLE_TEAM`)

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/api/team/form-options` | 📋 获取队员/模块/任务选项 |
| POST | `/api/team/time-logs` | 📝 提交训练记录 |

> 📌 统一响应格式: `{ code: 200, message: "success", data: {...} }`  
> 📌 分页格式: `data: { records: [...], total: N, page: N, pageSize: N }`

---

## 🎨 视觉主题

```
🎀 调色板
┌─────────────────────────────────────────────────────┐
│  💗 #ff7adf  主打粉色 — 边框 / 发光 / 强调         │
│  💙 #6ee7ff  主打蓝色 — 表头 / 图标 / 高亮         │
│  💜 #a78bfa  辅助紫色 — 标签 / 次要强调             │
│  💚 #7cffcb  成功薄荷 — 状态启用 / 已完成 / 成功    │
│  🧡 #ffd166  警告橙黄 — 未提交 / 提醒               │
│  ❤️ #ff5c9e  危险玫红 — 错误 / 禁用 / 高风险       │
│  🌑 #090b1f  深色背景 — 主背景色                    │
│  🖤 #0d0f2a  深色面板 — 卡片 / 弹窗 / 下拉          │
└─────────────────────────────────────────────────────┘

👾 像素科技元素
├── 🟦 伪元素像素网格叠加 (12px / 40px 网格)
├── 📺 CRT 扫描线 (repeating-linear-gradient)
├── ✨ 4px 偏移像素阴影 (box-shadow)
├── 🔲 6px 圆角像素边框
├── 💫 呼吸发光动画 (breatheGlow)
├── 🃏 卡片浮动入场动画 (cardFloat)
├── ✨ 像素星光闪烁 (pixelBlink / sparkle)
└── 🔘 按钮渐变流动效果
```

---

## 🔧 构建部署

```bash
# 🛠️ 管理端构建
cd frontend-admin
npm run build
# 产物输出到 dist/，基础路径 /admin/

# 🎯 队员端构建
cd frontend-team
npm run build
# 产物输出到 dist/，基础路径 /team/

# ☕ 后端打包
cd backend
mvn clean package -DskipTests
# 产物输出到 target/backend-0.0.1-SNAPSHOT.jar
```

---

## 📂 开发约定

- 🎯 **API 接入原则**: 必须走真实后端，无 mock fallback，`code === 200` 才算成功
- 🔒 **Token 管理**: 管理端用 `adminToken`，队员端用 `teamToken`，均存 localStorage
- 🚫 **队员端权限**: 队员只能提交记录，不能查看/修改/删除任何记录
- 🎨 **样式约定**: SCSS 变量统一在 `variables.scss`，Element Plus 主题覆写在 `theme-pixel-girl.scss`
- 📐 **目录规范**: API 模块放 `api/`，状态放 `stores/`，类型定义在 `types/index.ts`

---

<p align="center">
  <sub>Made with 💖 + ☕ + 👾 | 少女粉蓝像素科技风竞赛训练管理系统</sub>
</p>
