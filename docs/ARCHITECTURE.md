# RuoYi-NoCode 平台架构设计

> 版本: v2.0
> 更新: 2026-04-03
> 状态: 进行中

---

## 一、系统架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           前端 (Vue 3 + Element Plus)                        │
│         ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│         │  表单设计器   │  │ 工作流设计器 │  │ 代码生成器  │                  │
│         └─────────────┘  └─────────────┘  └─────────────┘                  │
└─────────────────────────────────┬───────────────────────────────────────────┘
                                  │ HTTP/REST
                                  ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      API Gateway (Spring Cloud Gateway)                       │
│                         Nacos + Sentinel + JWT                               │
└─────────────────────────────────┬───────────────────────────────────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        ▼                         ▼                         ▼
┌───────────────┐         ┌───────────────┐         ┌───────────────┐
│  Auth Service │         │ System Service │         │ Admin Service │
│   (认证服务)   │         │   (系统服务)   │         │  (业务服务)   │
└───────────────┘         └───────────────┘         └───────────────┘
        │                         │                         │
        │                         │                         ├─ 表单管理
        │                         │                         ├─ 工作流管理
        │                         │                         └─ 代码生成
        │                         │
        └─────────────────────────┼─────────────────────────┘
                                  ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Data Layer (MySQL + Redis)                            │
│   ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐     │
│   │sys_user  │  │sys_role  │  │nocode_   │  │nocode_   │  │nocode_   │     │
│   │          │  │          │  │form_*    │  │workflow_*│  │gen_*     │     │
│   └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘     │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、核心模块设计

### 2.1 表单设计器模块

**实体类**:
- `FormConfigEntity` - 表单配置
- `FormComponentEntity` - 表单组件

**表结构**:
```sql
CREATE TABLE nocode_form_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '表单名称',
    description VARCHAR(500) COMMENT '描述',
    form_key VARCHAR(100) UNIQUE COMMENT '表单标识',
    form_config TEXT COMMENT 'JSON配置',
    version INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'DRAFT',
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE nocode_form_component (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    form_id BIGINT COMMENT '所属表单',
    component_type VARCHAR(50) NOT NULL COMMENT '组件类型',
    label VARCHAR(100) NOT NULL COMMENT '标签',
    field_name VARCHAR(100) COMMENT '字段名',
    placeholder VARCHAR(200) COMMENT '占位符',
    default_value VARCHAR(500) COMMENT '默认值',
    required BOOLEAN DEFAULT FALSE,
    validation_rules TEXT COMMENT '验证规则JSON',
    component_props TEXT COMMENT '组件属性JSON',
    sort INT DEFAULT 0,
    create_time DATETIME
);
```

**组件类型**:
| 类型 | 说明 | 组件 |
|------|------|------|
| input | 单行文本 | el-input |
| textarea | 多行文本 | el-input |
| number | 数字输入 | el-input-number |
| select | 下拉选择 | el-select |
| radio | 单选框 | el-radio-group |
| checkbox | 多选框 | el-checkbox-group |
| date | 日期选择 | el-date-picker |
| datetime | 日期时间 | el-date-picker |
| switch | 开关 | el-switch |
| slider | 滑块 | el-slider |
| cascader | 级联选择 | el-cascader |
| upload | 文件上传 | el-upload |

### 2.2 工作流模块

**实体类**:
- `WorkflowDefinitionEntity` - 流程定义
- `WorkflowInstanceEntity` - 流程实例
- `WorkflowTaskEntity` - 流程任务

**表结构**:
```sql
CREATE TABLE nocode_workflow_definition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '流程名称',
    description VARCHAR(500) COMMENT '描述',
    process_key VARCHAR(100) UNIQUE NOT NULL COMMENT '流程标识',
    version INT DEFAULT 1,
    diagram_json TEXT COMMENT '流程图JSON',
    node_definition TEXT COMMENT '节点定义JSON',
    sequence_flow TEXT COMMENT '流转定义JSON',
    status VARCHAR(20) DEFAULT 'DRAFT',
    suspended BOOLEAN DEFAULT FALSE,
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE nocode_workflow_instance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    definition_id BIGINT NOT NULL COMMENT '流程定义ID',
    business_key VARCHAR(100) COMMENT '业务key',
    applicant VARCHAR(64) COMMENT '申请人',
    current_node_id VARCHAR(100) COMMENT '当前节点ID',
    current_node_name VARCHAR(100) COMMENT '当前节点名称',
    instance_status VARCHAR(20) DEFAULT 'RUNNING',
    create_time DATETIME,
    end_time DATETIME
);

CREATE TABLE nocode_workflow_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    instance_id BIGINT NOT NULL COMMENT '实例ID',
    node_id VARCHAR(100) NOT NULL COMMENT '节点ID',
    node_name VARCHAR(100) COMMENT '节点名称',
    assignee VARCHAR(64) COMMENT '办理人',
    task_status VARCHAR(20) DEFAULT 'PENDING',
    create_time DATETIME,
    complete_time DATETIME
);
```

**节点类型**:
| 类型 | 说明 | 图标 |
|------|------|------|
| startEvent | 开始事件 | ⭕ |
| endEvent | 结束事件 | 🔴 |
| userTask | 用户任务 | 👤 |
| serviceTask | 服务任务 | ⚙️ |
| scriptTask | 脚本任务 | 📜 |
| receiveTask | 接收任务 | 📬 |
| exclusiveGateway | 排他网关 | ◇ |
| parallelGateway | 并行网关 | ✕ |
| inclusiveGateway | 包容网关 | ◎ |

### 2.3 代码生成器模块

**实体类**:
- `CodeGeneratorConfigEntity` - 生成配置

**表结构**:
```sql
CREATE TABLE nocode_code_generator_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '配置名称',
    table_name VARCHAR(100) COMMENT '表名',
    entity_name VARCHAR(100) COMMENT '实体名',
    package_name VARCHAR(200) COMMENT '包名',
    module_name VARCHAR(100) COMMENT '模块名',
    generate_type VARCHAR(20) DEFAULT 'TEMPLATE',
    template_config TEXT COMMENT '模板配置JSON',
    field_config TEXT COMMENT '字段配置JSON',
    status VARCHAR(20) DEFAULT 'ENABLED',
    create_time DATETIME,
    update_time DATETIME
);
```

---

## 三、技术选型

### 3.1 后端技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.x | 基础框架 |
| Spring Cloud | 2023.x | 微服务 |
| Spring Data JPA | 3.2.x | ORM |
| MySQL | 8.0 | 主数据库 |
| Redis | 7.x | 缓存 |
| Nacos | 2.x | 注册/配置中心 |
| Sentinel | 1.8.x | 流控 |
| Sa-Token | 1.44.x | 认证 |

### 3.2 前端技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.x | 框架 |
| TypeScript | 5.9.x | 类型 |
| Element Plus | 2.11.x | UI |
| Vite | 6.x | 构建 |
| Vue Router | 4.x | 路由 |
| Pinia | 2.x | 状态 |

---

## 四、项目结构

```
ruoyi-nocode/
├── ruoyi-nocode-gateway/        # API网关
├── ruoyi-nocode-auth/          # 认证服务
├── ruoyi-nocode-system/        # 系统服务
├── ruoyi-nocode-common/        # 公共模块
│   └── ruoyi-nocode-common-core/
├── nocode-api-generator/        # 零代码生成器
│   ├── nocode-api-core/         # 核心API
│   ├── nocode-api-admin/        # 管理后端
│   │   └── src/main/java/com/nocode/admin/
│   │       ├── entity/          # 实体
│   │       │   ├── FormConfigEntity.java
│   │       │   ├── FormComponentEntity.java
│   │       │   ├── WorkflowDefinitionEntity.java
│   │       │   ├── WorkflowInstanceEntity.java
│   │       │   └── CodeGeneratorConfigEntity.java
│   │       ├── repository/      # 数据访问
│   │       ├── service/         # 业务逻辑
│   │       └── controller/       # REST接口
│   └── nocode-api-starter/      # 启动器
└── ruoyi-nocode-ui/            # 前端
    └── src/
        ├── api/nocode/          # API客户端
        ├── views/nocode/       # 页面
        │   ├── dashboard/       # 仪表盘
        │   ├── form/           # 表单设计器
        │   ├── workflow/        # 工作流设计器
        │   └── codeGenerator/  # 代码生成器
        └── router/             # 路由
```

---

## 五、API接口设计

### 5.1 表单API
```
POST   /api/form              # 创建表单
PUT    /api/form/{id}         # 更新表单
GET    /api/form/{id}         # 获取表单
GET    /api/form/list         # 列表查询
POST   /api/form/{id}/publish # 发布表单
DELETE /api/form/{id}         # 删除表单
GET    /api/form/search       # 搜索表单

POST   /api/form-component    # 创建组件
PUT    /api/form-component/{id}
GET    /api/form-component/{id}
GET    /api/form-component/list
GET    /api/form-component/list/form/{formId}
DELETE /api/form-component/{id}
```

### 5.2 工作流API
```
POST   /api/workflow/definition              # 创建流程
PUT    /api/workflow/definition/{id}          # 更新流程
GET    /api/workflow/definition/{id}         # 获取流程
GET    /api/workflow/definition/list         # 列表查询
POST   /api/workflow/definition/{id}/deploy # 部署
POST   /api/workflow/definition/{id}/suspend # 挂起
POST   /api/workflow/definition/{id}/start   # 启动实例
POST   /api/workflow/instance/{id}/complete  # 完成任务
DELETE /api/workflow/definition/{id}          # 删除

GET    /api/workflow/instance/{id}            # 获取实例
GET    /api/workflow/instance/list            # 实例列表
GET    /api/workflow/task/list                # 任务列表
POST   /api/workflow/task/{id}/claim          # 签收任务
POST   /api/workflow/task/{id}/approve         # 审批任务
POST   /api/workflow/task/{id}/reject          # 驳回任务
```

### 5.3 代码生成API
```
POST   /api/code-generator/config           # 创建配置
PUT    /api/code-generator/config/{id}       # 更新配置
GET    /api/code-generator/config/{id}       # 获取配置
GET    /api/code-generator/config/list      # 配置列表
POST   /api/code-generator/generate/{id}     # 生成代码
DELETE /api/code-generator/config/{id}       # 删除配置
```

---

## 六、部署架构

### 6.1 开发环境
```
┌─────────────┐
│  MySQL 8.0  │  localhost:3306
└─────────────┘
┌─────────────┐
│   Redis    │  localhost:6379
└─────────────┘
┌─────────────┐
│   Nacos    │  localhost:8848
└─────────────┘
```

### 6.2 Docker Compose
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
  redis:
    image: redis:7
    ports:
      - "6379:6379"
  nacos:
    image: nacos/nacos-server:v2.2.0
    ports:
      - "8848:8848"
```

---

## 七、开发进度

| 模块 | 状态 | 完成度 |
|------|------|--------|
| 架构设计 | ✅ | 100% |
| 表单设计器后端 | ✅ | 90% |
| 工作流设计器后端 | 🔄 | 70% |
| 代码生成器后端 | ✅ | 80% |
| 前端Vue组件 | ✅ | 80% |
| 单元测试 | 📋 | 0% |
| CI/CD配置 | 📋 | 0% |

---

最后更新: 2026-04-03
