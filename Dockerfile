# アプリケーションのビルド
FROM gradle:jdk21-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar

# アプリケーションの実行
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]