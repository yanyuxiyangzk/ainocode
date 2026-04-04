# RuoYi NoCode 数据库结构

## 目录结构

```
sql/
├── init/
│   └── init.sql          # 完整初始化脚本（一次性执行）
├── migration/
│   ├── V1__initial_schema.sql      # 基础表结构
│   ├── V2__system_config_tables.sql # 系统配置表
│   └── V3__plugin_tables.sql       # 插件表
├── seed/
│   └── V1__base_seed_data.sql      # 基础种子数据
└── README.md
```

## 使用方式

### 方式一: 直接执行 init.sql（推荐首次部署）

```bash
# 连接 PostgreSQL 数据库
psql -h localhost -U ods -d cost -f sql/init/init.sql
```

### 方式二: 使用 Flyway 迁移（推荐开发环境）

1. 在 Spring Boot 的 application.yml 中添加 Flyway 配置：

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    sql-migration-prefix: V
    sql-migration-separator: __
    sql-migration-suffixes: .sql
  datasource:
    # 你的数据源配置
```

2. 将 migration 和 seed 目录下的文件复制到项目的 resources/db 目录：

```bash
mkdir -p src/main/resources/db/migration
mkdir -p src/main/resources/db/seed
cp sql/migration/*.sql src/main/resources/db/migration/
cp sql/seed/*.sql src/main/resources/db/seed/
```

3. 引入 Flyway 依赖（在 pom.xml 中）：

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>9.22.3</version>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
    <version>9.22.3</version>
</dependency>
```

## 表结构说明

| 表名 | 说明 |
|------|------|
| sys_dept | 部门表 |
| sys_user | 用户表 |
| sys_role | 角色表 |
| sys_menu | 菜单权限表 |
| sys_user_role | 用户和角色关联表 |
| sys_role_menu | 角色和菜单关联表 |
| sys_role_dept | 角色和部门关联表 |
| sys_oper_log | 操作日志表 |
| sys_dict_type | 字典类型表 |
| sys_dict_data | 字典数据表 |
| sys_config | 参数配置表 |
| sys_notice | 通知公告表 |
| gen_table | 代码生成表 |
| gen_table_column | 代码生成明细表 |
| sys_plugin | 插件表 |

## 默认账户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| user01 | admin123 | 系统管理员 |
| user02 | admin123 | 普通角色 |

## 版本历史

- V1: 基础表结构（部门、用户、角色、菜单）
- V2: 系统配置表（日志、字典、参数、公告）
- V3: 插件系统表（代码生成、插件管理）
