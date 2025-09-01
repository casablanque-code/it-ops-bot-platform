package com.example.itopsbot.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "bot")
public class BotProperties {
private String username;
private String token;


public String getUsername() { return username; }
public void setUsername(String username) { this.username = username; }


public String getToken() { return token; }
public void setToken(String token) { this.token = token; }
}