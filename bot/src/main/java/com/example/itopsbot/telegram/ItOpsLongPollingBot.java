package com.example.itopsbot.telegram;

import com.example.itopsbot.config.BotProperties;
import com.example.itopsbot.docker.DockerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class ItOpsLongPollingBot extends TelegramLongPollingBot {

    private final BotProperties props;
    private final DockerService dockerService;
    private final Instant start = Instant.now();
    private volatile boolean commandsSet = false;

    public ItOpsLongPollingBot(BotProperties props, DockerService dockerService) {
        super(props.getToken());
        this.props = props;
        this.dockerService = dockerService;
    }

    @Override
    public String getBotUsername() {
        return props.getUsername();
    }

    // Ленивая регистрация команд (без @PostConstruct)
    private void initCommandsSafe() {
        try {
            execute(new SetMyCommands(
                    List.of(
                            new BotCommand("status", "Проверка доступности бота"),
                            new BotCommand("docker_status", "Список контейнеров и их состояние"),
                            new BotCommand("help", "Список доступных команд")
                    ),
                    new BotCommandScopeDefault(),
                    null
            ));
        } catch (TelegramApiException e) {
            System.err.println("Не удалось выставить команды: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() == null || update.getMessage().getText() == null) return;
        String text = update.getMessage().getText().trim();
        long chatId = update.getMessage().getChatId();

        if (!commandsSet) {
            initCommandsSafe();
            commandsSet = true;
        }

        switch (text) {
            case "/status" -> reply(chatId, statusText());
            case "/help" -> reply(chatId, helpText());
            case "/docker_status" -> reply(chatId, dockerService.getDockerStatus());
            default -> {
                if (text.startsWith("/")) {
                    reply(chatId, "Неизвестная команда. Наберите /help");
                }
            }
        }
    }

    private String helpText() {
        return String.join("\n",
                "Доступные команды:",
                "/status — проверить доступность бота",
                "/docker_status — показать список контейнеров и их статус",
                "/help — эта справка"
        );
    }

    private String statusText() {
        Duration up = Duration.between(start, Instant.now());
        long upDays = up.toDaysPart();
        int upHours = up.toHoursPart();
        int upMinutes = up.toMinutesPart();
        long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        long total = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        String jvm = ManagementFactory.getRuntimeMXBean().getVmName() + " " + System.getProperty("java.version");

        return String.format(
                "OK\nUptime: %dd %dh %dm\nMemory: %d MiB / %d MiB\nJVM: %s",
                upDays, upHours, upMinutes, used, total, jvm
        );
    }

    private void reply(long chatId, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }
}
