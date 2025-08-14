package com.example.itopsbot;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ItOpsBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItOpsBotApplication.class, args);
    }

    @Bean
    public Counter commandsCounter(MeterRegistry registry) {
        return Counter.builder("bot_commands_total")
                .description("Total commands handled by Telegram bot")
                .register(registry);
    }
}
