# ビルドステージ
FROM gradle:jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle clean build

# 実行ステージ
FROM openjdk:jdk21-jre-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]