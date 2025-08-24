# Gradle を使って JAR ファイルをビルド
FROM gradle:jdk21-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar

# アプリケーションを実行
FROM eclipse-temurin:21-jre-alpine
COPY --from=builder /app/build/libs/revox-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]