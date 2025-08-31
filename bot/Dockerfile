# build stage
FROM gradle:8.10.0-jdk21 AS build
WORKDIR /app

# Gradle файлы модуля
COPY settings.gradle.kts /app/
COPY build.gradle.kts /app/

# исходники
COPY src /app/src

# сборка bootJar (без тестов)
RUN gradle clean bootJar -x test --no-daemon --stacktrace

# runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC" \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

EXPOSE 8080 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
