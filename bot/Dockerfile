# build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Копируем только нужное для лучшего кеша
COPY settings.gradle.kts /app/
COPY build.gradle.kts /app/
COPY gradle /app/gradle
COPY gradlew /app/
COPY gradlew.bat /app/
COPY src /app/src

# Диагностика: покажем, что реально попало в образ
RUN set -eux; \
    echo "=== LS /app ==="; ls -la /app; \
    echo "=== LS /app/src ==="; ls -la /app/src || true; \
    echo "=== Gradle version via wrapper ==="; ./gradlew --version

# Сборка bootJar с подробным логом
RUN set -eux; \
    ./gradlew clean bootJar -x test --no-daemon --stacktrace --info

# runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC" \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
EXPOSE 8080 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
