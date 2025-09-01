# ---------- BUILD STAGE ----------
    FROM gradle:8.9-jdk21 AS builder
    WORKDIR /app
    COPY . /app
    RUN gradle clean bootJar --no-daemon
    
    
    # ---------- RUNTIME STAGE ----------
    FROM eclipse-temurin:21-jre
    ENV TZ=UTC \
    JAVA_OPTS="" \
    BOT_USERNAME="change_me" \
    BOT_TOKEN="change_me" \
    DOCKER_BIN="docker"
    
    
    # Установим docker-cli, чтобы внутри контейнера можно было выполнять `docker ps`
    RUN apt-get update && apt-get install -y --no-install-recommends docker.io && rm -rf /var/lib/apt/lists/*
    
    
    WORKDIR /app
    COPY --from=builder /app/build/libs/*.jar /app/app.jar
    
    
    # Совет: монтируйте /var/run/docker.sock:ro для команды /docker_status
    # - /var/run/docker.sock:/var/run/docker.sock:ro
    
    
    EXPOSE 8080
    ENTRYPOINT ["sh", "-lc", "java $JAVA_OPTS -jar /app/app.jar"]