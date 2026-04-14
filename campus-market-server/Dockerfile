FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 复制 jar 包
COPY target/campus-market-server-1.0.0-SNAPSHOT.jar app.jar

# 创建上传目录
RUN mkdir -p /app/uploads

# 暴露端口
EXPOSE 8080

# 环境变量（可通过 docker-compose 覆盖）
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
