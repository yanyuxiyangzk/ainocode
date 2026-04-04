# Nacos 配置说明

## 配置文件说明

本目录包含 RuoYi-Cloud-Nocode 项目的 Nacos 初始化配置文件。

### 文件列表

| 文件名 | 说明 | 命名空间 |
|--------|------|----------|
| common.yml | 公共配置，被所有服务共享 | DEFAULT_GROUP |
| ruoyi-nocode-gateway.yml | 网关服务配置 | DEFAULT_GROUP |
| ruoyi-nocode-auth.yml | 认证服务配置 | DEFAULT_GROUP |
| ruoyi-nocode-system.yml | 系统服务配置 | DEFAULT_GROUP |

## 导入步骤

### 方式一：通过 Nacos 控制台导入

1. 启动 Nacos 服务（默认地址：http://127.0.0.1:8848）
2. 登录 Nacos 控制台（默认账号：nacos/nacos）
3. 进入「配置管理」→「配置列表」
4. 选择对应的命名空间（默认：public）
5. 点击「导入配置」，依次导入以下文件：
   - `common.yml`
   - `ruoyi-nocode-gateway.yml`
   - `ruoyi-nocode-auth.yml`
   - `ruoyi-nocode-system.yml`

### 方式二：通过 Nacos OpenAPI 创建

```bash
# 创建 common.yml
curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs" \
  -d "dataId=common.yml&group=DEFAULT_GROUP&content=$(cat common.yml | base64)"

# 创建 ruoyi-nocode-gateway.yml
curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs" \
  -d "dataId=ruoyi-nocode-gateway.yml&group=DEFAULT_GROUP&content=$(cat ruoyi-nocode-gateway.yml | base64)"

# 创建 ruoyi-nocode-auth.yml
curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs" \
  -d "dataId=ruoyi-nocode-auth.yml&group=DEFAULT_GROUP&content=$(cat ruoyi-nocode-auth.yml | base64)"

# 创建 ruoyi-nocode-system.yml
curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs" \
  -d "dataId=ruoyi-nocode-system.yml&group=DEFAULT_GROUP&content=$(cat ruoyi-nocode-system.yml | base64)"
```

## 服务端口

| 服务 | 端口 | 描述 |
|------|------|------|
| ruoyi-nocode-gateway | 8080 | API 网关 |
| ruoyi-nocode-auth | 9200 | 认证服务 |
| ruoyi-nocode-system | 9201 | 系统服务 |

## 环境变量

可通过环境变量覆盖默认配置：

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| NACOS_SERVER | 127.0.0.1:8848 | Nacos 服务器地址 |
| NACOS_NAMESPACE | public | Nacos 命名空间 |
| NACOS_GROUP | DEFAULT_GROUP | Nacos 分组 |
| REDIS_HOST | 127.0.0.1 | Redis 主机 |
| REDIS_PORT | 6379 | Redis 端口 |
| REDIS_PASSWORD | (空) | Redis 密码 |
| DB_HOST | 127.0.0.1 | PostgreSQL 主机 |
| DB_PORT | 15432 | PostgreSQL 端口 |
| DB_NAME | cost | 数据库名 |
| DB_USER | ods | 数据库用户名 |
| DB_PASSWORD | Test@123 | 数据库密码 |

## 服务注册检查

服务启动后，可在 Nacos 控制台的「服务管理」→「服务列表」中查看注册的服务：

- `ruoyi-nocode-gateway`
- `ruoyi-nocode-auth`
- `ruoyi-nocode-system`
