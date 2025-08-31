# build stage
FROM gradle:8.10.0-jdk21 AS build
WORKDIR /app

# (опц.) локаль — можно оставить
ENV LANG=ru_RU.UTF-8 LANGUAGE=ru_RU:ru LC_ALL=ru_RU.UTF-8
RUN apt-get update && apt-get install -y --no-install-recommends locales \
 && locale-gen ru_RU.UTF-8 && update-locale LANG=ru_RU.UTF-8 \
 && rm -rf /var/lib/apt/lists/*

# Для лучшего кэша — сначала только Gradle файлы
COPY settings.gradle.kts /app/
COPY build.gradle.kts /app/          # корневой
COPY gradle /app/gradle               # если используешь wrapper
COPY gradlew /app/                    # если есть
COPY gradlew.bat /app/                # если есть
COPY bot/build.gradle.kts /app/bot/   # модуль bot

# Теперь исходники бота
COPY bot/src /app/bot/src

# Сборка конкретного модуля :bot
RUN set -eux; \
  if [ -x ./gradlew ]; then \
    ./gradlew :bot:clean :bot:bootJar -x test --no-daemon --stacktrace; \
  else \
    gradle :bot:clean :bot:bootJar -x test --no-daemon --stacktrace; \
  fi


# runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# если нужен curl для healthcheck — оставь
RUN apk add --no-cache curl

# ВНИМАНИЕ: если бот должен читать /var/run/docker.sock, у пользователя могут быть права.
# Проще запускать от root (комментируем adduser/USER).
# adduser -S appuser && USER appuser

# Кладём собранный jar
COPY --from=build /app/bot/build/libs/*.jar /app/app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

EXPOSE 8080 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
