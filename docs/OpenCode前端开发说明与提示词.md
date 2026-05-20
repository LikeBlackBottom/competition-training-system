# OpenCode 前端开发说明与提示词

## 当前项目
项目根目录：C:\competition-training-system

目录：
- backend：后端，不要修改
- docs：文档，不要修改
- 前端原型设计：Figma/AI 生成的前端原型，只作为视觉和交互参考，不要破坏
- frontend-admin：正式管理端前端
- frontend-team：正式队员端前端

## 技术栈
两个正式前端统一使用：
Vue3 + Vite + TypeScript + Vue Router + Pinia + Axios + Element Plus + SCSS

管理端额外使用：
ECharts / vue-echarts

端口：
- frontend-admin：5173
- frontend-team：5174

## 设计风格
必须参考“前端原型设计”目录中的页面、样式、布局和视觉风格。

关键词：
赛博朋克科技风、深色背景、蓝紫霓虹、玻璃拟态卡片、发光边框、科技网格、数据驾驶舱、专业后台系统。

注意：
不要做成游戏界面，不要过度装饰，不要牺牲表格和表单的可读性。

## 队员端核心规则
队员端只能：
1. 输入邀请码登录
2. 新增训练记录
3. 查看提交成功页面

队员端严禁出现：
- 历史记录
- 查看详情
- 修改按钮
- 删除按钮
- 导出按钮
- 管理入口
- 其他队伍数据

## 队员端路由
- /team/login
- /team/submit
- /team/success

默认 / 跳转 /team/login。

## 管理端路由
- /admin/login
- /admin/dashboard
- /admin/teams
- /admin/members
- /admin/skills
- /admin/time-logs
- /admin/issues
- /admin/exports

默认 / 跳转 /admin/login。

## 队员端页面
1. TeamLogin.vue
标题：赛训数据中枢
副标题：Competition Training Command Center
输入框 placeholder：请输入队伍邀请码，例如 CQSX0075
说明：队员端仅支持新增训练记录，提交后不可查看、修改或删除。
登录成功后进入 /team/submit。

2. TeamSubmit.vue
展示队伍名称、赛道、当前日期。
表单字段：
- 队员
- 训练模块
- 技能点
- 耗时
- 完成状态
- 今日产出
- 遇到问题
- 是否需要老师协助

训练模块和技能点必须联动。
提交成功后跳转 /team/success。
页面不能出现历史记录、修改、删除入口。

3. TeamSuccess.vue
文案：训练记录已提交
副文案：请联系指导老师确认，队员端不支持查看、修改或删除记录。
按钮：继续提交一条记录、退出登录。

## 管理端页面
1. AdminLogin.vue：管理员登录页
2. AdminLayout.vue：左侧导航栏 + 顶部 Header + 内容区
3. Dashboard.vue：全局数据看板
4. Teams.vue：队伍管理
5. Members.vue：队员管理
6. Skills.vue：技能树管理
7. TimeLogs.vue：工时记录管理
8. Issues.vue：问题闭环管理
9. Exports.vue：数据导出

## Dashboard 内容
统计卡片：
- 总队伍数
- 今日已提交队伍
- 今日未提交队伍
- 累计训练时长
- 待解决问题数

图表：
- 队伍训练时长排行柱状图
- 技能点耗时分布环形图
- 每日训练趋势折线图
- 问题严重程度分布图

表格：
- 最近提交记录

## 队伍管理字段
- 院校名称
- 队伍名称
- 赛道
- 邀请码
- 队员数量
- 今日提交状态
- 累计训练时长
- 状态
- 操作

功能：
新增队伍、编辑队伍、启用/禁用邀请码。
邀请码用霓虹标签显示。

## 队员管理字段
- 队员姓名
- 所属院校
- 所属队伍
- 角色
- 状态
- 累计训练时长
- 最近提交时间

功能：
按队伍筛选、新增队员、编辑队员、启用/禁用。

## 技能树管理
左侧模块列表，右侧技能点列表。
支持：
- 新增模块
- 编辑模块
- 新增技能点
- 编辑技能点
- 设置难度
- 设置权重
- 排序

示例模块：
技能实操、项目展示/演讲、文档编写、算法训练、智能体搭建、环境部署。

## 工时记录管理
筛选：
- 日期范围
- 队伍
- 模块
- 技能点
- 是否需要协助

表格字段：
- 日期
- 队伍
- 队员
- 模块
- 技能点
- 耗时
- 完成状态
- 今日产出
- 遇到问题
- 是否需要协助
- 状态
- 提交时间
- 操作

操作只能是“作废”，不能叫“删除”，不能做物理删除。

## 问题闭环
看板列：
- 待处理
- 处理中
- 已解决
- 已关闭

卡片字段：
- 问题标题
- 所属队伍
- 关联技能点
- 严重程度
- 负责人
- 提交时间

高危问题使用红紫色霓虹警示效果。

## 数据导出
卡片入口：
- 导出工时明细 Excel
- 导出队伍对比 Excel
- 导出问题闭环 Excel
- 生成周报 Word
- 生成汇报 PDF

当前可以用 mock 提示。

## 目录结构要求

frontend-admin 需要创建：
src/api
src/assets/images
src/assets/styles
src/components
src/layouts
src/mock
src/router
src/stores
src/types
src/views/login
src/views/dashboard
src/views/teams
src/views/members
src/views/skills
src/views/time-logs
src/views/issues
src/views/exports

frontend-team 需要创建：
src/api
src/assets/images
src/assets/styles
src/components
src/mock
src/router
src/stores
src/types
src/views

两个项目都必须有：
- src/api/request.ts
- src/assets/styles/variables.scss
- src/assets/styles/global.scss
- src/assets/styles/cyberpunk.scss
- src/router/index.ts
- src/main.ts
- src/App.vue
- .env.development
- vite.config.ts

## Mock 数据
后端暂未接入，必须使用 mock 数据让前端完整可运行。

至少包含：
- 4 个院校队伍
- 每队 2-3 名队员
- 5 个训练模块
- 每个模块 2-4 个技能点
- 20 条工时记录
- 8 条问题记录
- Dashboard 图表数据

示例院校：
重庆财经职业学院、重庆三峡职业学院、贵州航天职业技术学院、云南交通职业技术学院。

示例技能点：
疲劳检测、模型训练、YOLO 模型调优、智能体搭建、项目路演、答辩训练、文档编写、环境部署。

## API 占位
封装 Axios，当前可以返回 mock 数据。

.env.development：
VITE_API_BASE_URL=http://localhost:8080

队员端后续真实接口：
- POST /api/team/auth/code-login
- GET /api/team/form-options
- POST /api/team/time-logs

队员端禁止设计：
- GET /api/team/time-logs
- PUT /api/team/time-logs/{id}
- DELETE /api/team/time-logs/{id}

管理端后续真实接口：
- POST /api/admin/auth/login
- GET /api/admin/dashboard
- GET /api/admin/teams
- POST /api/admin/teams
- PUT /api/admin/teams/{id}
- GET /api/admin/members
- POST /api/admin/members
- PUT /api/admin/members/{id}
- GET /api/admin/skills/categories
- POST /api/admin/skills/categories
- PUT /api/admin/skills/categories/{id}
- GET /api/admin/skills/tasks
- POST /api/admin/skills/tasks
- PUT /api/admin/skills/tasks/{id}
- GET /api/admin/time-logs
- POST /api/admin/time-logs/{id}/void
- GET /api/admin/issues
- GET /api/admin/export/time-logs

## 验收标准
完成后必须：
1. frontend-admin 可以 npm install && npm run build
2. frontend-team 可以 npm install && npm run build
3. frontend-admin 运行端口 5173
4. frontend-team 运行端口 5174
5. 两个项目均为 Vue3 + TypeScript
6. 页面风格参考“前端原型设计”
7. 队员端严格只有登录、提交、成功三个页面
8. 队员端没有历史、查看、修改、删除入口
9. 管理端页面齐全
10. Dashboard 有统计卡片和 ECharts 图表
11. 表格、表单、弹窗能正常交互
12. mock 数据能完整展示
13. 不修改 backend、docs、前端原型设计目录
