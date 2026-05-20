import type { Team, Member, SkillModule, WorkRecord, Issue, DashboardData } from '@/types'

export const MOCK_TEAMS: Team[] = [
  { id: 1, schoolName: '重庆财经职业学院', teamName: 'CQSX-A队', track: '人工智能赛道', inviteCode: 'CQSX0075', memberCount: 3, todaySubmitted: true, totalHours: 342, status: 'active' },
  { id: 2, schoolName: '重庆三峡职业学院', teamName: 'CQGY-B队', track: '云计算赛道', inviteCode: 'CQGY0088', memberCount: 3, todaySubmitted: false, totalHours: 285, status: 'active' },
  { id: 3, schoolName: '贵州航天职业技术学院', teamName: 'CQCG-A队', track: '大数据赛道', inviteCode: 'CQCG0103', memberCount: 4, todaySubmitted: true, totalHours: 410, status: 'active' },
  { id: 4, schoolName: '云南交通职业技术学院', teamName: 'SCXX-A队', track: '软件测试赛道', inviteCode: 'SCXX0056', memberCount: 3, todaySubmitted: true, totalHours: 378, status: 'active' },
  { id: 5, schoolName: '成都职业技术学院', teamName: 'CDZY-A队', track: '人工智能赛道', inviteCode: 'CDZY0091', memberCount: 3, todaySubmitted: false, totalHours: 198, status: 'inactive' },
  { id: 6, schoolName: '重庆三峡职业学院', teamName: 'CQSX-B队', track: '人工智能赛道', inviteCode: 'CQSX0076', memberCount: 4, todaySubmitted: true, totalHours: 456, status: 'active' },
]

export const MOCK_MEMBERS: Member[] = [
  { id: 1, name: '张宇航', schoolName: '重庆财经职业学院', teamName: 'CQSX-A队', teamId: 1, role: '队长', status: 'active', totalHours: 128, lastSubmit: '2026-05-21 17:30' },
  { id: 2, name: '李晓彤', schoolName: '重庆财经职业学院', teamName: 'CQSX-A队', teamId: 1, role: '队员', status: 'active', totalHours: 115, lastSubmit: '2026-05-21 16:45' },
  { id: 3, name: '王浩然', schoolName: '重庆财经职业学院', teamName: 'CQSX-A队', teamId: 1, role: '队员', status: 'active', totalHours: 99, lastSubmit: '2026-05-19 18:00' },
  { id: 4, name: '陈思远', schoolName: '重庆三峡职业学院', teamName: 'CQGY-B队', teamId: 2, role: '队长', status: 'active', totalHours: 102, lastSubmit: '2026-05-19 17:20' },
  { id: 5, name: '刘佳颖', schoolName: '重庆三峡职业学院', teamName: 'CQGY-B队', teamId: 2, role: '队员', status: 'active', totalHours: 95, lastSubmit: '2026-05-21 09:00' },
  { id: 6, name: '赵明辉', schoolName: '贵州航天职业技术学院', teamName: 'CQCG-A队', teamId: 3, role: '队长', status: 'active', totalHours: 112, lastSubmit: '2026-05-21 18:15' },
  { id: 7, name: '孙雨欣', schoolName: '贵州航天职业技术学院', teamName: 'CQCG-A队', teamId: 3, role: '队员', status: 'active', totalHours: 98, lastSubmit: '2026-05-21 17:50' },
  { id: 8, name: '周子涵', schoolName: '云南交通职业技术学院', teamName: 'SCXX-A队', teamId: 4, role: '队长', status: 'active', totalHours: 134, lastSubmit: '2026-05-21 16:30' },
  { id: 9, name: '吴俊杰', schoolName: '重庆三峡职业学院', teamName: 'CQSX-B队', teamId: 6, role: '队长', status: 'active', totalHours: 156, lastSubmit: '2026-05-21 15:10' },
  { id: 10, name: '林小华', schoolName: '成都职业技术学院', teamName: 'CDZY-A队', teamId: 5, role: '队员', status: 'inactive', totalHours: 56, lastSubmit: '2026-05-17 10:00' },
]

export const MOCK_MODULES: SkillModule[] = [
  {
    id: 1, name: '技能实操', color: '#00d4ff',
    skills: [
      { id: 101, moduleId: 1, name: '模型训练', difficulty: 2, weight: 30, sortOrder: 1 },
      { id: 102, moduleId: 1, name: '数据清洗', difficulty: 1, weight: 25, sortOrder: 2 },
      { id: 103, moduleId: 1, name: '特征工程', difficulty: 3, weight: 25, sortOrder: 3 },
      { id: 104, moduleId: 1, name: '模型评估', difficulty: 2, weight: 20, sortOrder: 4 },
    ],
  },
  {
    id: 2, name: '项目展示/演讲', color: '#a855f7',
    skills: [
      { id: 201, moduleId: 2, name: 'PPT设计', difficulty: 1, weight: 25, sortOrder: 1 },
      { id: 202, moduleId: 2, name: '讲解逻辑', difficulty: 2, weight: 30, sortOrder: 2 },
      { id: 203, moduleId: 2, name: '答辩技巧', difficulty: 3, weight: 25, sortOrder: 3 },
      { id: 204, moduleId: 2, name: '项目亮点提炼', difficulty: 2, weight: 20, sortOrder: 4 },
    ],
  },
  {
    id: 3, name: '文档编写', color: '#00ff9f',
    skills: [
      { id: 301, moduleId: 3, name: '技术方案', difficulty: 2, weight: 30, sortOrder: 1 },
      { id: 302, moduleId: 3, name: '实施报告', difficulty: 2, weight: 25, sortOrder: 2 },
      { id: 303, moduleId: 3, name: '操作手册', difficulty: 1, weight: 20, sortOrder: 3 },
      { id: 304, moduleId: 3, name: '数据分析报告', difficulty: 3, weight: 25, sortOrder: 4 },
    ],
  },
  {
    id: 4, name: '算法训练', color: '#ff9f00',
    skills: [
      { id: 401, moduleId: 4, name: '分类算法', difficulty: 2, weight: 25, sortOrder: 1 },
      { id: 402, moduleId: 4, name: '回归算法', difficulty: 2, weight: 20, sortOrder: 2 },
      { id: 403, moduleId: 4, name: '聚类算法', difficulty: 3, weight: 20, sortOrder: 3 },
      { id: 404, moduleId: 4, name: 'NLP基础', difficulty: 3, weight: 35, sortOrder: 4 },
    ],
  },
  {
    id: 5, name: '智能体搭建', color: '#ff2d55',
    skills: [
      { id: 501, moduleId: 5, name: 'Agent框架', difficulty: 3, weight: 35, sortOrder: 1 },
      { id: 502, moduleId: 5, name: '工具调用', difficulty: 2, weight: 25, sortOrder: 2 },
      { id: 503, moduleId: 5, name: '记忆机制', difficulty: 3, weight: 20, sortOrder: 3 },
      { id: 504, moduleId: 5, name: '多Agent协作', difficulty: 3, weight: 20, sortOrder: 4 },
    ],
  },
  {
    id: 6, name: '环境部署', color: '#06b6d4',
    skills: [
      { id: 601, moduleId: 6, name: 'Docker容器', difficulty: 2, weight: 25, sortOrder: 1 },
      { id: 602, moduleId: 6, name: 'Python环境', difficulty: 1, weight: 20, sortOrder: 2 },
      { id: 603, moduleId: 6, name: 'GPU配置', difficulty: 3, weight: 20, sortOrder: 3 },
      { id: 604, moduleId: 6, name: '服务部署', difficulty: 2, weight: 35, sortOrder: 4 },
    ],
  },
]

export const MOCK_WORK_RECORDS: WorkRecord[] = [
  { id: 1, date: '2026-05-21', teamName: 'CQSX-A队', memberName: '张宇航', moduleName: '技能实操', skillName: '模型训练', hours: 210, completionStatus: 'completed', todayOutput: '完成ResNet50图像分类模型训练，准确率达92.3%', issue: 'GPU显存不足导致batch_size受限', needHelp: true, recordStatus: 'normal', submitTime: '2026-05-21 17:30' },
  { id: 2, date: '2026-05-21', teamName: 'CQSX-A队', memberName: '李晓彤', moduleName: '文档编写', skillName: '技术方案', hours: 120, completionStatus: 'completed', todayOutput: '完成项目技术方案第一稿', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-21 16:45' },
  { id: 3, date: '2026-05-21', teamName: 'CQCG-A队', memberName: '赵明辉', moduleName: '算法训练', skillName: 'NLP基础', hours: 240, completionStatus: 'completed', todayOutput: '完成文本分类模型调优', issue: 'BERT模型fine-tune效果不稳定', needHelp: true, recordStatus: 'normal', submitTime: '2026-05-21 18:15' },
  { id: 4, date: '2026-05-21', teamName: 'SCXX-A队', memberName: '周子涵', moduleName: '智能体搭建', skillName: 'Agent框架', hours: 180, completionStatus: 'partial', todayOutput: '完成基础Agent框架搭建', issue: '工具调用接口对接失败', needHelp: true, recordStatus: 'normal', submitTime: '2026-05-21 16:30' },
  { id: 5, date: '2026-05-21', teamName: 'CQSX-B队', memberName: '吴俊杰', moduleName: '环境部署', skillName: 'Docker容器', hours: 150, completionStatus: 'completed', todayOutput: '完成完整开发环境容器化', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-21 15:10' },
  { id: 6, date: '2026-05-21', teamName: 'CQGY-B队', memberName: '刘佳颖', moduleName: '技能实操', skillName: '数据清洗', hours: 120, completionStatus: 'completed', todayOutput: '清洗竞赛数据集，标记异常值', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-21 09:00' },
  { id: 7, date: '2026-05-19', teamName: 'CQSX-B队', memberName: '吴俊杰', moduleName: '环境部署', skillName: 'Docker容器', hours: 150, completionStatus: 'completed', todayOutput: '完成完整开发环境容器化', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-19 15:30' },
  { id: 8, date: '2026-05-19', teamName: 'CQGY-B队', memberName: '陈思远', moduleName: '项目展示/演讲', skillName: '讲解逻辑', hours: 90, completionStatus: 'partial', todayOutput: '完成演讲大纲草稿', issue: '不清楚评委评分重点', needHelp: true, recordStatus: 'normal', submitTime: '2026-05-19 17:20' },
  { id: 9, date: '2026-05-19', teamName: 'SCXX-A队', memberName: '周子涵', moduleName: '技能实操', skillName: '模型评估', hours: 180, completionStatus: 'completed', todayOutput: '完成多模型对比评估报告', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-19 16:50' },
  { id: 10, date: '2026-05-19', teamName: 'CQCG-A队', memberName: '孙雨欣', moduleName: '文档编写', skillName: '实施报告', hours: 150, completionStatus: 'completed', todayOutput: '更新项目实施报告第二版', issue: '数据可视化图表格式不统一', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-19 14:20' },
  { id: 11, date: '2026-05-18', teamName: 'CQCG-A队', memberName: '孙雨欣', moduleName: '技能实操', skillName: '数据清洗', hours: 180, completionStatus: 'completed', todayOutput: '清洗原始数据集，处理缺失值和异常值', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-18 17:50' },
  { id: 12, date: '2026-05-18', teamName: 'SCXX-A队', memberName: '周子涵', moduleName: '算法训练', skillName: 'NLP基础', hours: 270, completionStatus: 'completed', todayOutput: 'YOLOv8目标检测模型训练完成', issue: '小目标检测准确率偏低', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-18 16:30' },
  { id: 13, date: '2026-05-18', teamName: 'CQSX-A队', memberName: '张宇航', moduleName: '智能体搭建', skillName: 'Agent框架', hours: 240, completionStatus: 'partial', todayOutput: '完成Agent基础架构设计', issue: '多轮对话上下文丢失', needHelp: true, recordStatus: 'normal', submitTime: '2026-05-18 15:00' },
  { id: 14, date: '2026-05-18', teamName: 'CQSX-B队', memberName: '吴俊杰', moduleName: '技能实操', skillName: '模型训练', hours: 210, completionStatus: 'completed', todayOutput: '完成Transformer模型训练', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-18 11:30' },
  { id: 15, date: '2026-05-17', teamName: 'CQSX-A队', memberName: '李晓彤', moduleName: '文档编写', skillName: '操作手册', hours: 90, completionStatus: 'completed', todayOutput: '编写API接口操作手册', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-17 17:00' },
  { id: 16, date: '2026-05-17', teamName: 'CQGY-B队', memberName: '陈思远', moduleName: '算法训练', skillName: '分类算法', hours: 180, completionStatus: 'completed', todayOutput: '完成随机森林分类器训练', issue: '类别不平衡影响模型效果', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-17 16:20' },
  { id: 17, date: '2026-05-17', teamName: 'CDZY-A队', memberName: '林小华', moduleName: '环境部署', skillName: 'Python环境', hours: 120, completionStatus: 'blocked', todayOutput: '环境配置进行中', issue: 'Python依赖包版本冲突', needHelp: true, recordStatus: 'voided', submitTime: '2026-05-17 10:30' },
  { id: 18, date: '2026-05-16', teamName: 'CQCG-A队', memberName: '赵明辉', moduleName: '智能体搭建', skillName: '工具调用', hours: 210, completionStatus: 'completed', todayOutput: '集成外部API工具调用功能', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-16 18:00' },
  { id: 19, date: '2026-05-16', teamName: 'SCXX-A队', memberName: '周子涵', moduleName: '环境部署', skillName: '服务部署', hours: 180, completionStatus: 'completed', todayOutput: '完成模型推理服务部署', issue: 'API接口响应超时', needHelp: true, recordStatus: 'normal', submitTime: '2026-05-16 14:00' },
  { id: 20, date: '2026-05-15', teamName: 'CQSX-B队', memberName: '吴俊杰', moduleName: '项目展示/演讲', skillName: 'PPT设计', hours: 120, completionStatus: 'completed', todayOutput: '完成竞赛答辩PPT初稿', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-15 12:00' },
]

export const MOCK_ISSUES: Issue[] = [
  { id: 1, title: 'GPU显存不足，batch_size受限', teamName: 'CQSX-A队', skillName: '模型训练', severity: 'high', assignee: '张指导', memberName: '张宇航', submitTime: '2026-05-21 17:30', status: 'pending' },
  { id: 2, title: 'BERT模型fine-tune效果不稳定', teamName: 'CQCG-A队', skillName: 'NLP基础', severity: 'high', assignee: '李指导', memberName: '赵明辉', submitTime: '2026-05-21 18:15', status: 'processing' },
  { id: 3, title: '工具调用接口对接失败', teamName: 'SCXX-A队', skillName: 'Agent框架', severity: 'critical', assignee: '张指导', memberName: '周子涵', submitTime: '2026-05-21 16:30', status: 'pending' },
  { id: 4, title: '不清楚评委评分重点', teamName: 'CQGY-B队', skillName: '讲解逻辑', severity: 'low', assignee: '王指导', memberName: '陈思远', submitTime: '2026-05-19 17:20', status: 'processing' },
  { id: 5, title: 'Docker网络配置冲突', teamName: 'CQSX-B队', skillName: 'Docker容器', severity: 'normal', assignee: '李指导', memberName: '吴俊杰', submitTime: '2026-05-18 14:00', status: 'resolved' },
  { id: 6, title: 'Python依赖包版本冲突', teamName: 'CDZY-A队', skillName: 'Python环境', severity: 'normal', assignee: '王指导', memberName: '林小华', submitTime: '2026-05-17 10:30', status: 'closed' },
  { id: 7, title: 'API接口响应超时', teamName: 'SCXX-A队', skillName: '服务部署', severity: 'high', assignee: '张指导', memberName: '周子涵', submitTime: '2026-05-16 14:00', status: 'processing' },
  { id: 8, title: '小目标检测准确率偏低', teamName: 'SCXX-A队', skillName: 'NLP基础', severity: 'normal', assignee: '李指导', memberName: '周子涵', submitTime: '2026-05-18 16:30', status: 'pending' },
]

export const MOCK_DASHBOARD: DashboardData = {
  totalTeams: 6,
  todaySubmitted: 4,
  todayNotSubmitted: 2,
  totalHours: 1869,
  pendingIssues: 4,
  teamHoursRank: [
    { name: 'CQSX-B队', hours: 456 },
    { name: 'CQCG-A队', hours: 410 },
    { name: 'SCXX-A队', hours: 378 },
    { name: 'CQSX-A队', hours: 342 },
    { name: 'CQGY-B队', hours: 285 },
    { name: 'CDZY-A队', hours: 198 },
  ],
  skillDist: [
    { name: '技能实操', value: 38, color: '#00d4ff' },
    { name: '算法训练', value: 25, color: '#a855f7' },
    { name: '智能体搭建', value: 18, color: '#00ff9f' },
    { name: '环境部署', value: 10, color: '#ff9f00' },
    { name: '文档编写', value: 6, color: '#06b6d4' },
    { name: '项目展示', value: 3, color: '#ff2d55' },
  ],
  dailyTrend: [
    { date: '05-14', hours: 2520, records: 14 },
    { date: '05-15', hours: 3360, records: 18 },
    { date: '05-16', hours: 2280, records: 12 },
    { date: '05-17', hours: 4260, records: 23 },
    { date: '05-18', hours: 3900, records: 21 },
    { date: '05-19', hours: 4980, records: 26 },
    { date: '05-20', hours: 3480, records: 19 },
  ],
  issueSeverityDist: [
    { name: '致命', count: 1, color: '#ff2d55' },
    { name: '高危', count: 3, color: '#ff6b35' },
    { name: '中等', count: 2, color: '#ff9f00' },
    { name: '低危', count: 1, color: '#00ff9f' },
  ],
  recentRecords: [
    { id: 1, date: '2026-05-21', teamName: 'CQSX-A队', memberName: '张宇航', moduleName: '技能实操', skillName: '模型训练', hours: 210, completionStatus: 'completed', todayOutput: '完成ResNet50图像分类模型训练，准确率达92.3%', issue: 'GPU显存不足导致batch_size受限', needHelp: true, recordStatus: 'normal', submitTime: '2026-05-21 17:30' },
    { id: 2, date: '2026-05-21', teamName: 'CQSX-A队', memberName: '李晓彤', moduleName: '文档编写', skillName: '技术方案', hours: 120, completionStatus: 'completed', todayOutput: '完成项目技术方案第一稿', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-21 16:45' },
    { id: 3, date: '2026-05-21', teamName: 'CQCG-A队', memberName: '赵明辉', moduleName: '算法训练', skillName: 'NLP基础', hours: 240, completionStatus: 'completed', todayOutput: '完成文本分类模型调优', issue: 'BERT模型fine-tune效果不稳定', needHelp: true, recordStatus: 'normal', submitTime: '2026-05-21 18:15' },
    { id: 4, date: '2026-05-21', teamName: 'SCXX-A队', memberName: '周子涵', moduleName: '智能体搭建', skillName: 'Agent框架', hours: 180, completionStatus: 'partial', todayOutput: '完成基础Agent框架搭建', issue: '工具调用接口对接失败', needHelp: true, recordStatus: 'normal', submitTime: '2026-05-21 16:30' },
    { id: 5, date: '2026-05-21', teamName: 'CQSX-B队', memberName: '吴俊杰', moduleName: '环境部署', skillName: 'Docker容器', hours: 150, completionStatus: 'completed', todayOutput: '完成完整开发环境容器化', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-21 15:10' },
    { id: 6, date: '2026-05-21', teamName: 'CQGY-B队', memberName: '刘佳颖', moduleName: '技能实操', skillName: '数据清洗', hours: 120, completionStatus: 'completed', todayOutput: '清洗竞赛数据集，标记异常值', issue: '', needHelp: false, recordStatus: 'normal', submitTime: '2026-05-21 09:00' },
  ],
}

