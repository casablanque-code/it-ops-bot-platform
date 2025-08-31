# build stage (Gradle inside container)
FROM gradle:8.10.0-jdk21 AS build
WORKDIR /app

# Устанавливаем UTF-8 локаль
ENV LANG=ru_RU.UTF-8 \
    LANGUAGE=ru_RU:ru \
    LC_ALL=ru_RU.UTF-8
RUN apt-get update && apt-get install -y locales && \
    locale-gen ru_RU.UTF-8 && \
    update-locale LANG=ru_RU.UTF-8


COPY settings.gradle.kts build.gradle.kts /app/
COPY src /app/src

# убираем BOM из исходников (на всякий случай)
RUN set -eux; \
  ROOT="/app/bot/src"; \
  [ -d "$ROOT" ] || ROOT="/app/src"; \
  BOM="$(printf '\357\273\277')"; \
  find "$ROOT" -type f \( -name '*.java' -o -name '*.yml' -o -name '*.kts' \) -print0 \
  | xargs -0 -r sed -i "1s/^$BOM//" \
  && gradle clean bootJar -x test --no-daemon



# runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl && adduser -S appuser
USER appuser

COPY --from=build /app/build/libs/*.jar /app/app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=prod

ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"


EXPOSE 8080 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
