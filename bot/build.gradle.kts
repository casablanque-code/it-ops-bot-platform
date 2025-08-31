plugins {
    id("java")
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "com.example"
version = "0.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.telegram:telegrambots-spring-boot-starter:6.9.7.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.telegram:telegrambots-spring-boot-starter:6.9.7.1")

    // Docker Java client
    implementation("com.github.docker-java:docker-java:3.3.6")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.3.6")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType(org.gradle.api.tasks.compile.JavaCompile::class) {
    options.encoding = "UTF-8"
}


