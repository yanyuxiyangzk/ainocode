# NoCode API Generator

一个可以根据数据源表自动生成RESTful API接口的组件包，支持作为Spring Boot Starter集成到其他项目中。

## 功能特性

- **零代码生成API**：无需编写Controller、Service、Mapper，自动根据数据表生成完整CRUD API
- **多数据源支持**：MySQL、PostgreSQL、Oracle、SQL Server
- **RESTful风格**：标准RESTful API设计
- **高级查询功能**：分页、过滤、排序、关联查询
- **Web管理界面**：可视化配置数据源、查看API文档、在线测试
- **组件化设计**：可作为Spring Boot Starter集成

## 快速开始

### 1. 添加Maven依赖

```xml
<dependency>
    <groupId>com.nocode</groupId>
    <artifactId>nocode-api-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 启用组件

```java
@SpringBootApplication
@EnableNocodeApi
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. 配置数据源

在 `application.yml` 中添加配置：

```yaml
nocode:
  api:
    enabled: true
    admin:
      enabled: true      # 启用Web管理界面
      port: 8081        # 管理界面端口
    datasources:
      - name: primary
        jdbc-url: jdbc:mysql://localhost:3306/mydb
        username: root
        password: your_password
        driver-class-name: com.mysql.cj.jdbc.Driver
```

### 4. 访问API

启动后即可访问生成的API：

```bash
# 查询列表（分页）
GET /api/{datasource}/{table}?page=1&size=10

# 查询单条
GET /api/{datasource}/{table}/{id}

# 新增记录
POST /api/{datasource}/{table}
Content-Type: application/json

{"name": "test", "value": 123}

# 更新记录
PUT /api/{datasource}/{table}/{id}
Content-Type: application/json

{"name": "updated", "value": 456}

# 删除记录
DELETE /api/{datasource}/{table}/{id}
```

## API参考

### 查询列表

```http
GET /api/{datasource}/{table}
```

**参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| orderBy | string | 否 | 排序字段 |
| orderDirection | string | 否 | 排序方向：asc/desc |
| where | string | 否 | 查询条件（SQL片段） |

**响应示例：**

```json
{
  "success": true,
  "message": "success",
  "data": [...],
  "total": 100,
  "page": 1,
  "size": 10
}
```

### 查询单条

```http
GET /api/{datasource}/{table}/{id}
```

### 新增记录

```http
POST /api/{datasource}/{table}
Content-Type: application/json

{"field1": "value1", "field2": 123}
```

### 更新记录

```http
PUT /api/{datasource}/{table}/{id}
Content-Type: application/json

{"field1": "updated"}
```

### 删除记录

```http
DELETE /api/{datasource}/{table}/{id}
```

### 批量删除

```http
DELETE /api/{datasource}/{table}
Content-Type: application/json

[1, 2, 3, 4]
```

### 批量新增

```http
POST /api/{datasource}/{table}/batch
Content-Type: application/json

[{"name": "test1"}, {"name": "test2"}]
```

### 自定义查询

```http
POST /api/{datasource}/{table}/query
Content-Type: application/json

{
  "sql": "SELECT * FROM table WHERE status = ? AND type = ?",
  "params": ["active", "premium"]
}
```

### 获取表结构

```http
GET /api/{datasource}/{table}/schema
```

## Web管理界面

启动应用后访问：`http://localhost:8081/nocode-admin`

功能：
- **首页**：系统概览、API使用示例
- **数据源管理**：添加、删除、刷新数据源
- **表列表**：查看所有数据表及其结构
- **API文档**：查看生成的API接口文档
- **API测试**：在线测试API接口

## 配置说明

### 数据源配置

```yaml
nocode:
  api:
    datasources:
      - name: mydb                    # 数据源名称（唯一标识）
        jdbc-url: jdbc:mysql://localhost:3306/mydb
        username: root
        password: password
        driver-class-name: com.mysql.cj.jdbc.Driver
        initialSize: 5                # 初始连接数
        minIdle: 5                    # 最小空闲连接
        maxActive: 20                 # 最大连接数
        maxWait: 60000                # 获取连接最大等待时间(ms)
        enabled: true                 # 是否启用
```

### 完整配置项

```yaml
nocode:
  api:
    enabled: true                     # 是否启用组件
    adminEnabled: true                # 是否启用管理界面
    adminPort: 8081                   # 管理界面端口
    apiPrefix: /api                   # API前缀
    adminPath: /nocode-admin          # 管理界面路径
    defaultPageSize: 10               # 默认每页大小
    maxPageSize: 1000                 # 最大每页大小
    corsEnabled: true                 # 是否允许跨域
    datasources: []                   # 数据源配置列表
```

## 支持的数据库

| 数据库 | 驱动类 | JDBC URL示例 |
|--------|--------|--------------|
| MySQL | com.mysql.cj.jdbc.Driver | jdbc:mysql://host:port/db |
| PostgreSQL | org.postgresql.Driver | jdbc:postgresql://host:port/db |
| Oracle | oracle.jdbc.OracleDriver | jdbc:oracle:thin:@host:port:db |
| SQL Server | com.microsoft.sqlserver.jdbc.SQLServerDriver | jdbc:sqlserver://host:port;databaseName=db |



## 技术栈

- **后端**：Java 8 + Spring Boot 2.7 + MyBatis + Druid
- **前端**：Vue 2 + ElementUI + Axios
- **打包**：Maven多模块

## License

MIT License

Code Crock（代码专属结构化交互范式）和 Skill（Claude 的技能 / 功能模块）是完全不同维度的概念，一个是用户和 Claude 的交互方法论，一个是Claude 内置的功能扩展能力，核心区别体现在「定义、归属、使用方式、作用目标」四个核心维度，下面用清晰对比 + 通俗解释讲透二者的区别和配合用法，同时纠正易混淆的认知。
一、核心定义与核心区别（一张表讲清）

一、核心定义与核心区别（一张表讲清）
对比维度	Code Crock 模式	Claude Skill（技能）
本质定义	代码场景的结构化提问方法论，是用户向 AI 发指令的「格式 / 规则」	Claude 内置的功能扩展模块，是 AI 自身的「能力插件」
归属主体	属于用户，由用户主动遵循和使用（社区总结的最佳实践）	属于Claude，由 Anthropic 官方开发 / 第三方适配，AI 自带
使用方式	无需开启 / 安装，用户按模板写指令即可触发效果	需要手动开启 / 调用（指令调用 / 插件启用），AI 才会激活对应能力
作用目标	让 AI 的输出更结构化、更贴合代码需求（比如代码可运行、带注释、有测试用例）	让 AI新增 / 强化特定功能（比如代码调试、SQL 生成、文档解析）
存在形式	无实体，是「无形的指令逻辑」	有实体，是「有形的功能模块 / 插件」
是否可定制	完全可定制，用户可根据需求调整模板要素	部分可定制（官方 Skill 固定，第三方可开发），普通用户仅能调用

二、通俗解释：用 “做饭” 类比理解
Code Crock 模式 = 「标准化的做饭需求清单」
你想让厨师做一道 “番茄炒蛋”，不是说 “帮我做番茄炒蛋”，而是按固定格式写清：
食材：普通番茄、土鸡蛋、玉米油；
要求：少盐、微甜、鸡蛋嫩，不加水；
输出：做好的菜 + 装盘方式 + 试吃标准；
这个标准化清单的格式就是 Code Crock 模式，厨师（Claude）本身不会变，但会因为你的清单更清晰，做出更贴合你需求的菜。
Claude Skill = 「厨师的专属厨具 / 技能」
厨师本身会基本的炒菜技巧，但如果给他配了「不粘锅（Skill1）」「自动打蛋机（Skill2）」，他能把番茄炒蛋做得更快、更好；如果厨师解锁了「米其林番茄炒蛋（Skill3）」，还能做出进阶版的口味。
这些厨具 / 专属技能就是 Claude 的 Skill，能强化厨师的能力，但如果你的需求清单写得乱七八糟，就算厨师有再好的厨具，也可能做偏口味。
三、各自的核心使用场景
1. Code Crock 模式：所有代码场景的基础交互方式
   只要你让 Claude 处理代码相关需求（写代码、调 Bug、写命令、重构代码），都可以用 Code Crock 模式，核心解决「指令模糊导致 AI 输出质量低」的问题，比如：
   让 Claude 写 Python 爬虫代码；
   让 Claude 生成 Docker 部署 MySQL 的命令；
   让 Claude 重构一段 Java 接口代码；
   让 Claude 调试一段报错的 JavaScript 代码。
   核心价值：减少和 AI 的反复沟通，一次指令就得到高质量、可运行的代码 / 命令。
2. Claude Skill：特定场景的功能强化工具
   当你需要 Claude 处理有明确功能诉求的特定场景时，调用对应的 Skill，核心解决「AI 原生能力不足 / 处理特定任务效率低」的问题，Claude 官方常用的代码相关 Skill 包括：
   Code Debugger（代码调试）：自动定位代码报错原因并给出修复方案；
   Code Refactor（代码重构）：按行业规范优化代码结构、命名、性能；
   SQL Generator（SQL 生成）：根据自然语言生成符合指定数据库的 SQL；
   Docker File Generator（Dockerfile 生成）：根据需求自动生成 Dockerfile 和 docker-compose.yml；
   Unit Test Generator（单元测试生成）：为已有代码生成适配的单元测试用例。
   核心价值：让 AI 在特定代码任务上更专业，无需用户写复杂指令，一键调用即可。
   四、关键配合用法：Code Crock + Skill = 代码能力最大化
   二者不是对立的，而是互补的，结合使用能让 Claude 的代码能力发挥到极致，核心配合逻辑有两种：
   用法 1：先开 Skill，再用 Code Crock 发指令（推荐）
   针对特定代码任务，先调用 Claude 的对应 Skill，再按 Code Crock 模式发结构化指令，让 AI 在「强化功能」的基础上，输出「更贴合需求」的结果，比如：
   第一步：向 Claude 发送指令调用代码调试 Skill：启用 Code Debugger 技能；
   第二步：按 Code Crock 模式发指令：
   【场景 / 目标】调试一段 Python 爬虫代码，解决爬取时的 403 Forbidden 错误
   【技术栈】Python 3.11 + requests 2.31.0
   【约束条件】1. 保留原代码逻辑；2. 给出具体的修复代码和原因；3. 加注释说明修改点
   【输出格式】1. 报错原因分析；2. 修复后的完整代码；3. 防反爬的优化建议
   【验证标准】修复后代码能正常爬取数据，无 403 错误。
   用法 2：用 Code Crock 指令指定调用某个 Skill
   直接在 Code Crock 模板的「约束条件」中，要求 Claude 调用特定 Skill，一步到位，比如：
   【场景 / 目标】为一段 Java SpringBoot 接口代码生成单元测试
   【技术栈】Java 17 + SpringBoot 3.1 + JUnit 5
   【约束条件】1. 启用 Claude 的 Unit Test Generator 技能；2. 测试用例覆盖所有接口方法；3. 包含正向和异常测试场景
   【输出格式】1. 完整的单元测试代码；2. 测试用例说明；3. 运行测试的命令
   【验证标准】测试用例能正常运行，覆盖率达到 80% 以上。
   五、易混淆点澄清
   误区：Code Crock 是 Claude 的一个代码 Skill？
   → 错误！Code Crock 无实体，是用户的交互方法，不是 Claude 的内置 Skill，二者无归属关系；
   误区：用了 Skill 就不用 Code Crock 了？
   → 错误！Skill 只是强化 AI 能力，若指令模糊，AI 仍会输出不符合预期的结果，Code Crock 能保证指令的清晰度，二者结合效果最佳；
   误区：只有 Code 场景能用 Skill？
   → 错误！Claude 的 Skill 覆盖全场景，不仅有代码类，还有文档类、数据分析类、写作类等，而 Code Crock仅适用于代码场景。
   六、总结
   核心区别：Code Crock 是用户的指令方法论（怎么向 AI 提要求），Skill 是Claude 的功能模块（AI 有什么特殊能力）；
   核心关系：二者互补，无先后但结合使用效果最优；
   核心用法：在 VS Code 中使用 Claude 时，选 Code 模式 + Claude 3 Sonnet/Opus 模型，再结合「Code Crock 结构化指令 + 代码相关 Skill」，就能得到最高质量的代码输出。
   简单来说，Code Crock 决定了 AI “能不能听懂你的需求”，Skill 决定了 AI “能不能把这件事做得更好”。