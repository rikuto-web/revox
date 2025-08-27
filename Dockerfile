# アプリケーションのビルド
FROM gradle:jdk21-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar

# アプリケーションの実行
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# Shell 経由で起動 + プロファイル指定
ENTRYPOINT ["sh", "-c", "sleep 30 && java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]