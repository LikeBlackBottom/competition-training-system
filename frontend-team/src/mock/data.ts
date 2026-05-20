export const TEAMS = [
  { id: 1, school: '重庆电子工程职业学院', name: 'CQSX-A队', track: '人工智能应用', code: 'CQSX0075' },
  { id: 2, school: '重庆工业职业技术学院', name: 'CQGY-B队', track: '大数据技术', code: 'CQGY0088' },
  { id: 3, school: '重庆城市管理职业学院', name: 'CQCG-A队', track: '人工智能应用', code: 'CQCG0103' },
  { id: 4, school: '四川信息职业技术学院', name: 'SCXX-A队', track: '智能制造', code: 'SCXX0056' },
  { id: 5, school: '成都职业技术学院', name: 'CDZY-A队', track: '大数据技术', code: 'CDZY0091' },
  { id: 6, school: '重庆三峡职业学院', name: 'CQSX-B队', track: '人工智能应用', code: 'CQSX0076' },
]

export const MEMBERS = [
  { id: 1, name: '张宇航', teamId: 1, role: '队长' },
  { id: 2, name: '李晓彤', teamId: 1, role: '队员' },
  { id: 3, name: '王浩然', teamId: 1, role: '队员' },
  { id: 4, name: '陈思远', teamId: 2, role: '队长' },
  { id: 5, name: '刘佳颖', teamId: 2, role: '队员' },
  { id: 6, name: '赵明辉', teamId: 3, role: '队长' },
  { id: 7, name: '孙雨欣', teamId: 3, role: '队员' },
  { id: 8, name: '周子涵', teamId: 4, role: '队长' },
  { id: 9, name: '杨美玲', teamId: 4, role: '队员' },
  { id: 10, name: '林小华', teamId: 5, role: '队长' },
  { id: 11, name: '吴芳菲', teamId: 5, role: '队员' },
  { id: 12, name: '吴俊杰', teamId: 6, role: '队长' },
  { id: 13, name: '郑天明', teamId: 6, role: '队员' },
  { id: 14, name: '黄丽萍', teamId: 6, role: '队员' },
]

export const MODULES = [
  {
    id: 1,
    name: '技能实操',
    color: '#00d4ff',
    skills: [
      { id: 1, moduleId: 1, name: '模型训练' },
      { id: 2, moduleId: 1, name: '数据清洗' },
      { id: 3, moduleId: 1, name: '特征工程' },
      { id: 4, moduleId: 1, name: '模型评估' },
      { id: 5, moduleId: 1, name: '部署推理' },
    ],
  },
  {
    id: 2,
    name: '项目展示/演讲',
    color: '#a855f7',
    skills: [
      { id: 6, moduleId: 2, name: 'PPT设计' },
      { id: 7, moduleId: 2, name: '讲解逻辑' },
      { id: 8, moduleId: 2, name: '答辩技巧' },
      { id: 9, moduleId: 2, name: '项目亮点提炼' },
    ],
  },
  {
    id: 3,
    name: '文档编写',
    color: '#00ff9f',
    skills: [
      { id: 10, moduleId: 3, name: '技术方案' },
      { id: 11, moduleId: 3, name: '实施报告' },
      { id: 12, moduleId: 3, name: '操作手册' },
      { id: 13, moduleId: 3, name: '数据分析报告' },
    ],
  },
  {
    id: 4,
    name: '算法训练',
    color: '#ff9f00',
    skills: [
      { id: 14, moduleId: 4, name: '分类算法' },
      { id: 15, moduleId: 4, name: '回归算法' },
      { id: 16, moduleId: 4, name: '聚类算法' },
      { id: 17, moduleId: 4, name: 'NLP基础' },
      { id: 18, moduleId: 4, name: '计算机视觉' },
    ],
  },
  {
    id: 5,
    name: '智能体搭建',
    color: '#ff2d55',
    skills: [
      { id: 19, moduleId: 5, name: 'Agent框架' },
      { id: 20, moduleId: 5, name: '工具调用' },
      { id: 21, moduleId: 5, name: '记忆机制' },
      { id: 22, moduleId: 5, name: '多Agent协作' },
    ],
  },
  {
    id: 6,
    name: '环境部署',
    color: '#06b6d4',
    skills: [
      { id: 23, moduleId: 6, name: 'Docker容器' },
      { id: 24, moduleId: 6, name: 'Python环境' },
      { id: 25, moduleId: 6, name: 'GPU配置' },
      { id: 26, moduleId: 6, name: '服务部署' },
      { id: 27, moduleId: 6, name: 'API接口' },
    ],
  },
]
