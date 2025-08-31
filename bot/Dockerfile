# build stage
FROM gradle:8.10.0-jdk21 AS build
WORKDIR /app

# сначала файлы Gradle (лучший кеш)
COPY settings.gradle.kts /app/
COPY build.gradle.kts /app/
COPY gradle /app/gradle
COPY gradlew /app/
COPY gradlew.bat /app/

# исходники модуля
COPY src /app/src

# сборка bootJar этого модуля
RUN set -eux; \
  if [ -x ./gradlew ]; then \
    ./gradlew clean bootJar -x test --no-daemon --stacktrace; \
  else \
    gradle clean bootJar -x test --no-daemon --stacktrace; \
  fi

# runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC" \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

EXPOSE 8080 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
