# RuoYi-Cloud-Nocode - 零代码智能微服务平台

> 后端项目：RuoYi-Cloud-Nocode | 前端项目：Ui-Nocode

## 项目介绍

RuoYi-Cloud-Nocode 是一款基于Spring生态的**零代码智能微服务平台**，采用 **Skill + LLM + Json2UI + Agent + 代码知识库** 核心技术架构，通过AI智能体实现复杂微服务项目的自动化构建。

**核心特性**：
- 🧠 **AI智能开发**：LLM理解需求 → 代码模板匹配 → Skill调度 → 自动生成代码
- 🎯 **零代码开发**：可视化配置 + AI生成JSON Schema → Json2UI动态渲染表单
- ⚡ **热插拔引擎**：Spring插件技术，业务模块动态加载，无需重启
- 🏗️ **微服务架构**：基于Spring Cloud 2025完整微服务方案
- 📚 **代码知识库**：Code RAG向量化检索，命中模板降低Token消耗60-80%

## 核心技术架构

```
用户需求 → AI理解 → 代码知识库检索 → Skill调度 → 代码生成 → Json2UI渲染
                                              ↓
                                    ┌─────────────────┐
                                    │  Skill能力矩阵   │
                                    │  • CRUD生成     │
                                    │  • 业务逻辑生成  │
                                    │  • 流程编排生成  │
                                    │  • API生成      │
                                    └─────────────────┘
```

## 技术栈

### 🔧 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.x | 底层框架 |
| Spring Cloud | 2025.x | 微服务治理 |
| Java | 17+ | 运行环境 |
| MyBatis-Plus | 3.5.x | ORM框架 |
| Sa-Token | 1.44.x | 认证授权 |
| Nacos | 2.x | 注册/配置中心 |
| Seata | 1.8.x | 分布式事务 |
| Warm-Flow | 1.8.x | 工作流引擎 |

### 🎨 前端技术 (Ui-Nocode)

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.x | 核心框架 |
| TypeScript | 5.9.x | 类型支持 |
| Element Plus | 2.11.x | UI组件库 |
| Vite | 6.x | 构建工具 |
| **Json2UI** | 自研 | **JSON Schema动态表单引擎** |
| **VM2** | - | **表单事件沙箱执行** |

### 🤖 AI/LLM技术

| 技术 | 说明 |
|------|------|
| **Skill调度** | 需求命中代码模板时，Skill快速获取模板 |
| **LLM** | GPT-4/Claude/通义千问/DeepSeek |
| **Agent** | LangChain + ReAct 智能体编排 |
| **Code RAG** | 代码知识库 + 向量检索 (Milvus/Pgvector) |

## 项目结构

```
ainocode/
├── docs/                      # 项目文档
│   └── SRS-零代码微服务脚手架平台.md    # 需求规格文档
├── sql/                       # SQL脚本
├── plus-ui-ts/               # 前端参考 (Ui-Nocode基础)
├── RuoYi-Cloud-Plus/         # 后端参考
├── nocode-api-generator/     # 零代码API生成器(参考)
└── RuoYi-Cloud-Nocode/       # 新项目：零代码智能平台(待开发)
│   │   ├── ruoyi-resource/   # 资源模块
│   │   ├── ruoyi-workflow/   # 工作流模块
│   │   ├── ruoyi-gen/        # 代码生成
│   │   └── ruoyi-job/        # 定时任务
│   ├── ruoyi-common/         # 公共模块
│   └── ruoyi-visual/        # 可视化模块
└── README.md                  # 项目说明
```

## 核心功能

### 1. 插件热插拔引擎
- 插件生命周期管理（安装、卸载、启用、停用）
- 类加载器隔离，避免类冲突
- 插件版本管理
- 插件市场（待实现）

### 2. 代码生成引擎
- 表结构自动解析
- Velocity模板引擎
- 多模块Maven项目生成
- Vue3+TypeScript前端代码生成

### 3. 动态表单引擎（核心创新）
- **JSON Schema驱动**：基于JSON配置动态渲染Vue表单组件
- **拖拽式设计器**：可视化拖拽组件构建表单
- **AI辅助生成**：自然语言描述自动生成表单配置
- **事件绑定**：支持JavaScript脚本处理复杂交互逻辑
- **数据源绑定**：组件级/表单级数据源配置，支持API/字典/静态数据

### 4. AI/LLM智能开发
- **LLM集成**：GPT-4/Claude/通义千问/DeepSeek
- **Code RAG**：代码知识库检索增强生成
- **Skill体系**：模板匹配→参数注入→代码组装
- **智能补全**：基于知识库的代码建议

### 5. 代码知识库（Code RAG）
- **模板仓库**：标准化CRUD/API/组件代码模板
- **向量化检索**：语义匹配最佳代码模板
- **Skill调度**：自动匹配→获取→组装→优化
- **降本增效**：降低60-80% Token消耗，代码准确率95%+

### 6. 零代码配置平台
- 可视化表结构设计
- 字段类型配置
- 验证规则配置
- 界面配置（列表、表单、查询）

### 5. 企业级功能
- 用户/角色/部门/岗位管理
- 菜单权限控制
- 工作流引擎（流程设计、任务审批）
- 定时任务调度
- 文件管理（本地+OSS）
- 消息通知（邮件、短信、站内消息）
- 系统日志与审计

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 20+
- PostgreSQL 15+ / MySQL 8.0+
- Redis 7.x

### 部署步骤

1. **数据库初始化**
```bash
# 创建数据库
psql -U postgres -c "CREATE DATABASE aicode;"

# 执行SQL脚本
psql -U postgres -d aicode -f sql/ry_20250523.sql
psql -U postgres -d aicode -f sql/ry_config_20250902.sql
```

2. **后端部署**

```bash
# 克隆项目后，构建后端
cd RuoYi-Cloud-Plus
mvn clean package -DskipTests

# 启动Nacos/Redis等中间件（Docker）
docker-compose up -d

# 启动微服务（按顺序）
java -jar ruoyi-gateway/target/ruoyi-gateway.jar
java -jar ruoyi-auth/target/ruoyi-auth.jar
java -jar ruoyi-modules/ruoyi-system/target/ruoyi-system.jar
```

3. **前端部署**

```bash
cd plus-ui-ts
npm install
npm run dev
```

4. **访问系统**

- 前端地址：http://localhost:80
- 默认账号：admin / 123456

## 文档

- [需求分析规格文档(SRS)](docs/SRS-零代码微服务脚手架平台.md) - 完整的需求规格说明
- [RuoYi-Cloud-Plus参考文档](RuoYi-Cloud-Plus/README.md) - 后端技术参考

## 开发路线图

| 阶段 | 目标 | 状态 |
|------|------|------|
| Phase 1 | 核心框架搭建 - 微服务基座 + 插件引擎 | ✅ 已完成 |
| Phase 2 | 代码生成引擎 - 后端+前端代码生成 | ✅ 已完成 |
| Phase 3 | 零代码配置平台 - 可视化配置界面 | ✅ 已完成 |
| Phase 4 | 企业功能完善 - 权限/工作流/消息 | ✅ 已完成 |
| Phase 5 | 插件市场 - 扩展生态建设 | 📋 规划中 |

## 已完成功能

### 表单设计器 (Form Designer)
- 拖拽式可视化表单构建器
- 12+ 表单组件类型 (Input, Textarea, Number, Select, Radio, Checkbox, Date, DateTime, Switch, Slider, Cascader, Upload)
- 实时属性编辑
- 表单验证规则配置
- 表单发布/取消发布

### 工作流设计器 (Workflow Designer)
- BPMN 2.0 兼容的工作流建模
- 9+ 节点类型:
  - 开始/结束事件
  - 用户任务
  - 服务任务
  - 脚本任务
  - 接收任务
  - 排他网关
  - 并行网关
  - 包容网关
- 可视化连线编辑
- 流程部署/挂起

### 代码生成器 (Code Generator)
- 表到实体自动映射
- 自动生成:
  - JPA Entity
  - Spring Data Repository
  - Service 接口与实现
  - REST Controller
  - MyBatis Mapper
- 字段自定义
- 模板配置

### 平台仪表盘
- 统一管理界面
- 实时统计数据
- 最近使用记录
- 架构图展示

## 项目文件结构

```
ainocode/
├── ruoyi-nocode/                      # 零代码微服务平台
│   ├── ruoyi-nocode-gateway/          # API网关
│   ├── ruoyi-nocode-auth/             # 认证服务
│   ├── ruoyi-nocode-system/           # 系统服务
│   ├── ruoyi-nocode-common/            # 公共模块
│   │   └── ruoyi-nocode-common-core/  # 核心公共模块
│   └── ruoyi-nocode-ui/               # 前端UI
│       └── src/
│           ├── api/nocode/            # API客户端
│           │   ├── form.js            # 表单API
│           │   ├── workflow.js        # 工作流API
│           │   └── codeGenerator.js   # 代码生成API
│           └── views/nocode/          # 视图页面
│               ├── dashboard/         # 仪表盘
│               ├── form/             # 表单设计器
│               ├── workflow/         # 工作流设计器
│               └── codeGenerator/    # 代码生成器
├── nocode-api-generator/              # 代码生成引擎
│   ├── nocode-api-core/              # 核心API
│   ├── nocode-api-admin/             # 管理后端
│   │   └── src/main/java/com/nocode/admin/
│   │       ├── entity/               # 实体类
│   │       │   ├── FormConfigEntity.java
│   │       │   ├── FormComponentEntity.java
│   │       │   ├── WorkflowDefinitionEntity.java
│   │       │   ├── WorkflowInstanceEntity.java
│   │       │   └── CodeGeneratorConfigEntity.java
│   │       ├── repository/           # 数据访问层
│   │       ├── service/             # 业务服务层
│   │       └── controller/         # REST控制器
│   └── nocode-api-starter/          # 启动器
└── auto-dev/                        # 自动开发系统
    ├── scripts/                    # 自动化脚本
    ├── skills/                     # 技能模板
    └── config/                     # 配置文件
```

## 许可证

MIT License

## 致谢

本项目参考了以下开源项目：

- [RuoYi-Cloud-Plus](https://gitee.com/dromara/RuoYi-Cloud-Plus) - 微服务管理系统
- [Spring Cloud](https://spring.io/projects/spring-cloud) - 微服务框架
- [Vue3](https://vuejs.org/) - 渐进式前端框架
- [Element Plus](https://element-plus.org/) - Vue3组件库

---

**Aicode - 让开发更简单**
