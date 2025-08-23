# ビルドステージ
FROM gradle:jdk21-alpine as build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle bootJar --no-daemon

# 実行ステージ
FROM eclipse-temurin:21-jre-ubi10-minimal as final
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar /app/app.jar
RUN microdnf update && microdnf install -y nmap-ncat
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
