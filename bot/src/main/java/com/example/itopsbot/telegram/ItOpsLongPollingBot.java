package com.example.itopsbot.telegram;

import com.example.itopsbot.config.BotProperties;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ItOpsLongPollingBot extends TelegramLongPollingBot {

    private final BotProperties props;
    private final Counter commandsCounter;

    public ItOpsLongPollingBot(BotProperties props, Counter commandsCounter) {
        this.props = props;
        this.commandsCounter = commandsCounter;
    }

    @Override
    public String getBotUsername() {
        return props.getUsername();
    }

    @Override
    public String getBotToken() {
        return props.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;
        Message msg = update.getMessage();
        if (!msg.hasText()) return;

        String text = msg.getText().trim();
        long chatId = msg.getChatId();

        if (text.startsWith("/")) {
            commandsCounter.increment();
        }

        switch (text.split(" ")[0]) {
            case "/start" -> reply(chatId, "Привет! Я CottBot! Команды: /status, /help");
            case "/status" -> reply(chatId, "ОК: бот жив, метрики на :8081/actuator/prometheus");
            case "/help" -> reply(chatId, "Доступные команды:\n/status — проверить состояние бота\n/help — помощь");

            default -> reply(chatId, "Привет! Я CottBot! Команды: /status, /help");
        }
    }

    private void reply(long chatId, String text) {
        try {
            execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
