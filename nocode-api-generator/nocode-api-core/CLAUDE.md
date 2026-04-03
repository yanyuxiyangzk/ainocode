`../../` > [`nocode-api-generator`](../../CLAUDE.md) > **nocode-api-core**

# nocode-api-core 模块

## 模块职责

核心业务逻辑模块，负责：
- 多数据源管理（DatasourceRegistry）
- 数据库表结构解析（TableParser 系列）
- SQL 执行与结果转换（SqlExecutor, QueryBuilder）
- 类型转换编排（ConversionOrchestrator, SkillRegistry）
- 元数据缓存（MetadataCache）
- 动态 API 路由（ApiRouter）

## 入口与启动

**入口**: `ApiRouter.java` (`com.nocode.core.router.ApiRouter`)

该类是 REST API 的核心控制器，提供以下端点：

```java
@RestController
@RequestMapping("/api")
public class ApiRouter {
    // GET    /api/{datasource}/{table}           - 查询列表（分页）
    // GET    /api/{datasource}/{table}/{id}      - 查询单条
    // POST   /api/{datasource}/{table}           - 新增
    // PUT    /api/{datasource}/{table}/{id}      - 更新
    // DELETE /api/{datasource}/{table}/{id}     - 删除
    // DELETE /api/{datasource}/{table}          - 批量删除
    // POST   /api/{datasource}/{table}/batch    - 批量新增
    // POST   /api/{datasource}/{table}/query    - 自定义查询
    // POST   /api/{datasource}/execute          - 执行SQL
    // GET    /api/{datasource}/{table}/schema    - 表结构
    // GET    /api/{datasource}/tables            - 所有表
    // GET    /api/datasources                    - 所有数据源
    // GET    /api/health                         - 健康检查
}
```

## 对外接口

### 核心组件

| 类 | 路径 | 职责 |
|----|------|------|
| `ApiRouter` | `router/ApiRouter.java` | REST API 路由入口 |
| `DatasourceRegistry` | `datasource/DatasourceRegistry.java` | 数据源注册中心，支持多数据源管理 |
| `SqlExecutor` | `executor/SqlExecutor.java` | SQL 执行器，集成 Skill+Agent 编排 |
| `QueryBuilder` | `executor/QueryBuilder.java` | SQL 构建器 |
| `MetadataCache` | `metadata/MetadataCache.java` | 元数据缓存 |
| `ConversionOrchestrator` | `orchestrator/ConversionOrchestrator.java` | 类型转换编排器 |
| `SkillRegistry` | `skill/SkillRegistry.java` | Skill 注册器 |
| `TableParserFactory` | `parser/TableParserFactory.java` | 表解析器工厂 |

### 数据库支持

- MySQL (`MySqlTableParser`)
- PostgreSQL (`PostgreSqlTableParser`)
- Oracle (`OracleTableParser`)
- SQL Server (`SqlServerTableParser`)

## 关键依赖与配置

### Maven 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

### 配置属性

```yaml
nocode:
  api:
    enabled: true
    datasources: []  # 数据源配置列表
    defaultPageSize: 10
    maxPageSize: 1000
  agent:
    enabled: false  # 是否启用 LLM Agent
```

## 数据模型

### 核心实体

| 类 | 路径 | 描述 |
|----|------|------|
| `ApiResult` | `entity/ApiResult.java` | API 统一响应结构 |
| `ApiQueryParam` | `entity/ApiQueryParam.java` | 查询参数（分页、排序、过滤） |
| `TableInfo` | `entity/TableInfo.java` | 表结构信息 |
| `ColumnInfo` | `entity/ColumnInfo.java` | 列信息 |
| `DatasourceConfig` | `entity/DatasourceConfig.java` | 数据源配置 |
| `ForeignKeyInfo` | `entity/ForeignKeyInfo.java` | 外键信息 |
| `DatabaseType` | `entity/DatabaseType.java` | 数据库类型枚举 |

### ApiResult 结构

```java
{
    "success": true,
    "message": "success",
    "data": {...},
    "total": 100,
    "page": 1,
    "size": 10,
    "metadata": {...}
}
```

## Skill 系统

### 内置 Skills

| Skill | 路径 | 描述 |
|-------|------|------|
| `PrimaryKeySkill` | `skill/skills/PrimaryKeySkill.java` | 主键处理 |
| `AutoIncrementSkill` | `skill/skills/AutoIncrementSkill.java` | 自增主键处理 |
| `BooleanSkill` | `skill/skills/BooleanSkill.java` | 布尔类型转换 |
| `NumericSkill` | `skill/skills/NumericSkill.java` | 数值类型转换 |
| `StringSkill` | `skill/skills/StringSkill.java` | 字符串处理 |
| `UUIDSkill` | `skill/skills/UUIDSkill.java` | UUID 处理 |
| `DateTimeSkill` | `skill/skills/DateTimeSkill.java` | 日期时间处理 |

### 类型转换编排流程

```
输入数据 -> SkillRegistry 查找匹配 Skill
    |
    v
Skill.tryConvert() -> SUCCESS/SKIP/NEED_AGENT/FAILED
    |
    v
Agent 处理（可选，需配置 nocode.agent.enabled=true）
    |
    v
ConversionOrchestrator 返回结果
```

## 测试与质量

**测试状态**: 当前模块无测试目录

**建议补充**:
- `src/test/java` 目录
- `SqlExecutorTest` - SQL 执行器单元测试
- `ConversionOrchestratorTest` - 类型转换编排测试
- `ApiRouterTest` - 路由控制器测试

## 常见问题 (FAQ)

**Q: 如何添加新的数据库支持？**
A: 继承 `AbstractTableParser` 实现 `TableParser` 接口，在 `TableParserFactory` 中注册。

**Q: 如何扩展类型转换？**
A: 实现 `TypeConversionSkill` 接口并添加 `@Component` 注解，框架会自动注册。

**Q: 如何启用 LLM Agent？**
A: 配置 `nocode.agent.enabled=true`，实现 `TypeConversionAgent` 接口。

## 相关文件清单

### 核心文件

```
nocode-api-core/src/main/java/com/nocode/core/
├── router/
│   └── ApiRouter.java                    # REST API 入口
├── datasource/
│   ├── DatasourceRegistry.java           # 数据源注册中心
│   └── DynamicDataSource.java            # 动态数据源
├── executor/
│   ├── SqlExecutor.java                  # SQL 执行器
│   └── QueryBuilder.java                 # SQL 构建器
├── orchestrator/
│   ├── ConversionOrchestrator.java       # 类型转换编排器
│   ├── ConversionResult.java             # 转换结果
│   └── ConversionException.java         # 转换异常
├── parser/
│   ├── TableParser.java                  # 表解析器接口
│   ├── AbstractTableParser.java          # 抽象基类
│   ├── TableParserFactory.java           # 工厂类
│   ├── MySqlTableParser.java             # MySQL 解析器
│   ├── PostgreSqlTableParser.java        # PostgreSQL 解析器
│   ├── OracleTableParser.java            # Oracle 解析器
│   └── SqlServerTableParser.java         # SQL Server 解析器
├── skill/
│   ├── SkillRegistry.java                # Skill 注册器
│   ├── TypeConversionSkill.java          # Skill 接口
│   └── skills/
│       ├── PrimaryKeySkill.java
│       ├── AutoIncrementSkill.java
│       ├── BooleanSkill.java
│       ├── NumericSkill.java
│       ├── StringSkill.java
│       ├── UUIDSkill.java
│       └── DateTimeSkill.java
├── metadata/
│   └── MetadataCache.java                # 元数据缓存
├── agent/
│   ├── AgentContext.java                 # Agent 上下文
│   ├── TypeConversionAgent.java          # Agent 接口
│   └── TypeConversionAgentImpl.java      # Agent 实现
├── entity/
│   ├── ApiResult.java
│   ├── ApiQueryParam.java
│   ├── TableInfo.java
│   ├── ColumnInfo.java
│   ├── DatasourceConfig.java
│   ├── ForeignKeyInfo.java
│   └── DatabaseType.java
└── config/
    ├── NocodeApiProperties.java          # 配置属性
    └── AgentConfig.java
```

## 变更记录 (Changelog)

### 2026-03-24 - 文档初始化

- 创建 nocode-api-core 模块文档
- 梳理核心组件与职责
- 整理类型转换编排流程
