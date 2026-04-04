# 阶段1: 构建镜像
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# 复制 POM 文件
COPY pom.xml .
COPY ruoyi-nocode-common/pom.xml ruoyi-nocode-common/
COPY ruoyi-nocode-gateway/pom.xml ruoyi-nocode-gateway/

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码
COPY ruoyi-nocode-common/ ruoyi-nocode-common/
COPY ruoyi-nocode-gateway/ ruoyi-nocode-gateway/

# 构建项目
RUN mvn clean package -DskipTests -B

# 阶段2: 运行镜像
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="ruoyi-nocode"
LABEL description="RuoYi NoCode Gateway Service"

# 安装 curl 用于健康检查
RUN apk add --no-cache curl

WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /build/ruoyi-nocode-gateway/target/*.jar app.jar

# 创建插件目录
RUN mkdir -p /home/nocode-plugins /home/nocode-plugins/temp

# 环境变量
ENV JAVA_OPTS="-Xms256m -Xmx512m -Xmn128m"

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]