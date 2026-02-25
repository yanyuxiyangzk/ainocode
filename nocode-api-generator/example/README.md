# NoCode API 示例项目

## 环境要求

- Java 8+
- MySQL 5.7+ / 8.0+
- Maven 3.6+

## 快速开始

### 1. 初始化数据库

```bash
# 创建数据库并导入示例数据
mysql -u root -p < sql/init.sql
```

### 2. 修改配置文件

编辑 `src/main/resources/application.yml`，修改数据库连接配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/example
    username: your_username
    password: your_password

nocode:
  api:
    datasources:
      - name: business
        jdbc-url: jdbc:mysql://localhost:3306/business_db
        username: your_username
        password: your_password
```

### 3. 编译运行

```bash
# 编译
mvn clean package -DskipTests

# 运行
java -jar target/nocode-api-example-1.0.0.jar
```

### 4. 访问应用

- **Web管理界面**: http://localhost:8081/nocode-admin
- **生成的API**: http://localhost:8080/api/business/user

## API测试示例

### 查询用户列表

```bash
curl "http://localhost:8080/api/business/user?page=1&size=10"
```

### 查询单个用户

```bash
curl "http://localhost:8080/api/business/user/1"
```

### 新增用户

```bash
curl -X POST "http://localhost:8080/api/business/user" \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"123456","email":"new@example.com"}'
```

### 更新用户

```bash
curl -X PUT "http://localhost:8080/api/business/user/1" \
  -H "Content-Type: application/json" \
  -d '{"username":"updated","email":"updated@example.com"}'
```

### 删除用户

```bash
curl -X DELETE "http://localhost:8080/api/business/user/1"
```

## 目录结构

```
example/
├── pom.xml                           # Maven配置
├── README.md                         # 说明文档
├── sql/
│   └── init.sql                      # 数据库初始化脚本
└── src/
    └── main/
        ├── java/
        │   └── com/nocode/example/
        │       └── ExampleApplication.java  # 应用入口
        └── resources/
            └── application.yml              # 应用配置
```
