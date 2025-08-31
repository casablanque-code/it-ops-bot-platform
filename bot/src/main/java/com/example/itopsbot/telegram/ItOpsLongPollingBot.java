package com.example.itopsbot.telegram;

import com.example.itopsbot.config.BotProperties;
import com.example.itopsbot.docker.DockerService;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ItOpsLongPollingBot extends TelegramLongPollingBot {

    private final BotProperties props;
    private final Counter commandsCounter;
    private final DockerService dockerService;

    public ItOpsLongPollingBot(BotProperties props,
                               Counter commandsCounter,
                               DockerService dockerService) {
        this.props = props;
        this.commandsCounter = commandsCounter;
        this.dockerService = dockerService;
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

        // Берем первое слово как команду
        String cmd = text.split(" ")[0];

        switch (cmd) {
            case "/start" -> reply(chatId, "Привет! Я CottBot! Команды: /status, /docker, /help");
            case "/status" -> reply(chatId, "ОК: бот жив, метрики на :8081/actuator/prometheus");
            case "/help" -> reply(chatId,
                    "Доступные команды:\n" +
                    "/status — проверить состояние бота\n" +
                    "/docker — показать запущенные контейнеры\n" +
                    "/docker -a — показать все контейнеры (включая остановленные)\n" +
                    "/help — помощь");

            case "/docker", "/containers", "/dc" -> {
                boolean showAll = text.matches("(?i).*(\\s-?a\\b|\\s--all\\b).*");
                String result;
                try {
                    result = dockerService.listContainersPretty(showAll);
                } catch (Exception e) {
                    result = "Не удалось получить статусы контейнеров: " + e.getMessage();
                }
                replyMarkdown(chatId, result);
            }

            default -> reply(chatId, "Привет! Я CottBot! Команды: /status, /docker, /help");
        }
    }

    private void reply(long chatId, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void replyMarkdown(long chatId, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .parseMode("Markdown")
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
