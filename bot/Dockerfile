# bot/Dockerfile
FROM eclipse-temurin:21-jre

ENV TZ=UTC JAVA_OPTS="" DOCKER_BIN="docker"
WORKDIR /app

# docker-cli (для /docker_status)
RUN apt-get update && apt-get install -y --no-install-recommends docker.io && rm -rf /var/lib/apt/lists/*

# Кладём уже собранный JAR из CI
COPY build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh","-lc","java $JAVA_OPTS -jar /app/app.jar"]
