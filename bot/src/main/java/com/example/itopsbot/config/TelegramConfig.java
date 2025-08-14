package com.example.itopsbot.config;

import com.example.itopsbot.telegram.ItOpsLongPollingBot;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {
    private static final Logger log = LoggerFactory.getLogger(TelegramConfig.class);

    private final BotProperties props;
    private final ItOpsLongPollingBot bot;

    public TelegramConfig(BotProperties props, ItOpsLongPollingBot bot) {
        this.props = props;
        this.bot = bot;
    }

    @PostConstruct
    public void register() {
        try {
            if (props.getToken() == null || props.getToken().isBlank()) {
                log.error("TELEGRAM_BOT_TOKEN пуст. Проверь переменные окружения.");
                return;
            }
            log.info("Регистрация бота username='{}' (token starts with: {}...)",
                    props.getUsername(),
                    props.getToken().substring(0, Math.min(10, props.getToken().length())));
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(bot);
            log.info("Бот успешно зарегистрирован в TelegramBotsApi.");
        } catch (Exception e) {
            log.error("Ошибка регистрации бота: {}", e.getMessage(), e);
        }
    }
}
