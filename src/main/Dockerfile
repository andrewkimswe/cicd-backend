# 빌드 단계
FROM gradle:7.5.1-jdk11 as build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# 실행 단계
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
