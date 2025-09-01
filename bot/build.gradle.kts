plugins {
id("org.springframework.boot") version "3.5.3"
id("io.spring.dependency-management") version "1.1.6"
id("java")
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
implementation("org.springframework.boot:spring-boot-starter")
implementation("org.springframework.boot:spring-boot-starter-web")


// Telegram Bots Spring Boot Starter
//implementation("org.telegram:telegrambots-spring-boot-starter:6.9.7.1")


//testImplementation("org.springframework.boot:spring-boot-starter-test")

implementation("org.telegram:telegrambots:6.9.7.1")
//implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
//implementation("org.springframework.boot:spring-boot-starter-validation")


}


tasks.withType<JavaCompile> {
options.encoding = "UTF-8"
}


tasks.test {
useJUnitPlatform()
}