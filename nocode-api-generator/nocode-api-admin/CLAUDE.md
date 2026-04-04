`../../` > [`nocode-api-generator`](../../CLAUDE.md) > **nocode-api-admin**

# nocode-api-admin 模块

## 模块职责

Web 管理界面模块，提供：
- 数据源配置管理（增删改查）
- API 文档自动生成
- API 在线测试
- ER 图可视化
- Vue.js 前端 + Spring Boot 后端

## 入口与启动

**入口**: `NocodeApiAdminApplication.java` (`com.nocode.admin.NocodeApiAdminApplication`)

```java
@SpringBootApplication(scanBasePackages = {"com.nocode.admin", "com.nocode.core", "com.nocode.starter"})
@EnableNocodeApi(enableAdmin = true)
@RestController
public class NocodeApiAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(NocodeApiAdminApplication.class, args);
    }
}
```

**独立运行**:

```bash
mvn clean package -Pstandalone
java -jar target/nocode-api-admin-1.0.0.jar
# 访问 http://localhost:8080/nocode-admin
```

## 对外接口

### 后端控制器

| 类 | 路径 | 职责 |
|----|------|------|
| `AdminController` | `controller/AdminController.java` | 管理接口：数据源、API文档、ER图 |
| `ApiTestController` | `controller/ApiTestController.java` | API 测试接口 |

### AdminController 接口

```
GET    /api/admin/system/info                        - 系统信息
GET    /api/admin/datasources                        - 数据源列表
POST   /api/admin/datasources                        - 添加数据源
DELETE /api/admin/datasources/{name}                 - 删除数据源
GET    /api/admin/datasources/{name}/tables          - 表列表
GET    /api/admin/datasources/{name}/schemas         - 模式列表
GET    /api/admin/datasources/{name}/tables/{table}  - 表结构
POST   /api/admin/datasources/{name}/execute         - 执行SQL
GET    /api/admin/datasources/{name}/api-doc         - API文档
GET    /api/admin/datasources/{name}/er-diagram      - ER图数据
POST   /api/admin/datasources/{name}/diagram-layout  - 保存布局
GET    /api/admin/datasources/{name}/diagram-layout  - 获取布局
DELETE /api/admin/datasources/{name}/diagram-layout  - 删除布局
```

### 前端视图

| 视图 | 路径 | 描述 |
|------|------|------|
| `Home.vue` | `views/Home.vue` | 首页 |
| `Datasources.vue` | `views/Datasources.vue` | 数据源管理 |
| `Tables.vue` | `views/Tables.vue` | 表列表 |
| `ApiDoc.vue` | `views/ApiDoc.vue` | API 文档 |
| `ApiTest.vue` | `views/ApiTest.vue` | API 测试 |
| `ErDiagram.vue` | `views/ErDiagram.vue` | ER 图 |
| `CreateTable.vue` | `views/CreateTable.vue` | 创建表 |

### 前端组件

| 组件 | 路径 | 描述 |
|------|------|------|
| `Layout.vue` | `components/Layout.vue` | 布局组件 |
| `ApiRequest.vue` | `components/ApiRequest.vue` | API 请求组件 |
| `JsonViewer.vue` | `components/JsonViewer.vue` | JSON 展示组件 |

## 关键依赖与配置

### Maven 依赖

```xml
<dependency>
    <groupId>com.nocode</groupId>
    <artifactId>nocode-api-core</artifactId>
</dependency>
<dependency>
    <groupId>com.nocode</groupId>
    <artifactId>nocode-api-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-boot-starter</artifactId>
</dependency>
```

### 前端依赖

```json
{
    "vue": "^2.7.16",
    "element-ui": "^2.15.14",
    "axios": "^1.6.2",
    "vue-router": "^3.6.5",
    "@antv/g6": "^4.8.24"
}
```

### 配置文件

```yaml
# application.yml
server:
  servlet:
    context-path: /nocode-admin

nocode:
  api:
    enabled: true
    admin:
      enabled: true
      path: /nocode-admin
    datasources:
      - name: postgres_primary
        jdbc-url: jdbc:postgresql://192.168.10.67:5432/hrp-cloud
        username: postgres
        password: postgres
```

## 数据模型

### JPA 实体

| 类 | 路径 | 描述 |
|----|------|------|
| `DatasourceConfigEntity` | `entity/DatasourceConfigEntity.java` | 数据源配置实体 |
| `DiagramLayoutEntity` | `entity/DiagramLayoutEntity.java` | ER图布局实体 |

### 服务层

| 类 | 路径 | 描述 |
|----|------|------|
| `DatasourceConfigService` | `service/DatasourceConfigService.java` | 数据源配置服务 |
| `DiagramLayoutService` | `service/DiagramLayoutService.java` | 布局保存服务 |

### 仓储层

| 类 | 路径 |
|----|------|
| `DatasourceConfigRepository` | `repository/DatasourceConfigRepository.java` |
| `DiagramLayoutRepository` | `repository/DiagramLayoutRepository.java` |

## 测试与质量

**测试状态**: 当前模块无测试目录

## 常见问题 (FAQ)

**Q: 前端如何代理到后端？**
A: Vite 配置了 `/nocode-admin` 路径代理到 `http://localhost:8080`

**Q: H2 数据库用途？**
A: 存储数据源配置和 ER 图布局信息

**Q: 如何自定义前端？**
```bash
cd src/main/frontend
npm install --legacy-peer-deps
npm run dev  # 开发模式
npm run build  # 生产构建
```

## 相关文件清单

```
nocode-api-admin/
├── src/main/java/com/nocode/admin/
│   ├── NocodeApiAdminApplication.java
│   ├── config/
│   │   ├── DatasourceInitializer.java
│   │   ├── NocodeApiAdminAutoConfiguration.java
│   │   └── NocodeWebMvcConfig.java
│   ├── controller/
│   │   ├── AdminController.java
│   │   └── ApiTestController.java
│   ├── entity/
│   │   ├── DatasourceConfigEntity.java
│   │   └── DiagramLayoutEntity.java
│   ├── repository/
│   │   ├── DatasourceConfigRepository.java
│   │   └── DiagramLayoutRepository.java
│   └── service/
│       ├── DatasourceConfigService.java
│       └── DiagramLayoutService.java
├── src/main/frontend/
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/
│       │   └── index.js
│       ├── views/
│       │   ├── Home.vue
│       │   ├── Datasources.vue
│       │   ├── Tables.vue
│       │   ├── ApiDoc.vue
│       │   ├── ApiTest.vue
│       │   ├── ErDiagram.vue
│       │   └── CreateTable.vue
│       └── components/
│           ├── Layout.vue
│           ├── ApiRequest.vue
│           └── JsonViewer.vue
└── src/main/resources/
    └── application.yml
```

## 变更记录 (Changelog)

### 2026-03-24 - 文档初始化

- 创建 nocode-api-admin 模块文档
- 梳理前后端接口
- 整理目录结构
