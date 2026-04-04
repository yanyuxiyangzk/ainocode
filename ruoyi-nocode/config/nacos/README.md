# Nacos Configuration Center

## 目录结构

```
config/nacos/
├── README.md
├── namespaces.yml                    # 命名空间和分组配置
├── nacos-config-tool.sh             # 配置管理脚本
├── shared/
│   └── common.yml                   # 公共配置
└── services/
    ├── ruoyi-nocode-gateway-dev.yml
    ├── ruoyi-nocode-auth-dev.yml
    └── ruoyi-nocode-system-dev.yml

docker/nacos/
├── docker-compose.yml               # Nacos 集群部署
└── nginx.conf                      # Nginx 负载均衡配置
```

## 命名空间

| Namespace ID | 名称 | 用途 |
|-------------|------|------|
| public | Public | 默认命名空间 |
| dev | Development | 开发环境 |
| test | Testing | 测试环境 |
| pre | Pre-Production | 预发布环境 |
| prod | Production | 生产环境 |

## 分组

| Group | 用途 |
|-------|------|
| DEFAULT_GROUP | 默认分组 |
| MICROSERVICE_GROUP | 微服务配置 |
| GATEWAY_GROUP | 网关配置 |
| INFRA_GROUP | 基础设施配置 |
| BUSINESS_GROUP | 业务配置 |

## 服务配置映射

| 服务 | Group | DataId |
|------|-------|--------|
| Gateway | GATEWAY_GROUP | ruoyi-nocode-gateway-{profile} |
| Auth | MICROSERVICE_GROUP | ruoyi-nocode-auth-{profile} |
| System | MICROSERVICE_GROUP | ruoyi-nocode-system-{profile} |
| Common | DEFAULT_GROUP | common.yml |

## 高可用部署

### 启动 Nacos 集群

```bash
cd docker/nacos
docker-compose up -d
```

访问地址: http://localhost:8848/nacos

### 默认账户

- 用户名: nacos
- 密码: nacos

## 配置管理

### 使用配置管理脚本

```bash
# 导出所有配置
./nacos-config-tool.sh export-all <namespace> <group> <output>

# 导入配置
./nacos-config-tool.sh import <dataId> <group> <namespace> <file>

# 备份所有配置
./nacos-config-tool.sh backup

# 监听配置变化
./nacos-config-tool.sh watch <dataId> <group> <namespace>

# 发布配置
./nacos-config-tool.sh publish <dataId> <group> <namespace> <content>
```

### 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| NACOS_HOST | 127.0.0.1 | Nacos 服务地址 |
| NACOS_PORT | 8848 | Nacos 服务端口 |
| NACOS_USERNAME | nacos | 用户名 |
| NACOS_PASSWORD | nacos | 密码 |

## 热更新机制

Nacos 配置支持热更新，无需重启服务：

1. 在 Nacos 控制台修改配置
2. 客户端自动感知配置变化
3. Spring `@RefreshScope` 自动刷新 Bean

### 监听配置变化

```java
@RefreshScope
@Configuration
public class MyConfig {
    @Value("${my.property}")
    private String property;
}
```

## 配置版本管理

Nacos 内置配置历史版本管理：

1. 每次配置发布都会创建新版本
2. 支持回滚到历史版本
3. 支持配置变更追踪

### 回滚配置

```bash
# 查看历史版本
curl -u nacos:nacos \
  "http://localhost:8848/nacos/v1/cs/history/configs?dataId=xxx&group=DEFAULT_GROUP"

# 回滚到指定版本
curl -u nacos:nacos -X POST \
  "http://localhost:8848/nacos/v1/cs/configs" \
  -d "dataId=xxx&group=DEFAULT_GROUP&content=OLD_CONTENT&type=yaml"
```

## 导入初始配置

```bash
# 创建命名空间
curl -X POST -u nacos:nacos \
  "http://localhost:8848/nacos/v1/console/namespaces" \
  -d "customNamespaceId=dev&namespaceName=Development"

# 导入服务配置
for f in config/nacos/services/*.yml; do
  dataId=$(basename $f)
  ./nacos-config-tool.sh import $dataId GATEWAY_GROUP dev $f
done
```
