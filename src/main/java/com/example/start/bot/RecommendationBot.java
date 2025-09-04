package com.example.start.bot;

import com.example.start.dto.Recommendation;
import com.example.start.entity.User;
import com.example.start.repository.UserRepository;
import com.example.start.service.RecommendationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.UUID;

@Component
public class RecommendationBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final UserRepository userRepository;
    private final RecommendationService recommendationService;

    public RecommendationBot(@Value("${spring.telegram.bot.token}") String botToken,
                             @Value("${spring.telegram.bot.name}") String botUsername,
                             UserRepository userRepository,
                             RecommendationService recommendationService) {
        super(botToken);
        this.botUsername = botUsername;
        this.userRepository = userRepository;
        this.recommendationService = recommendationService;
        System.out.println("Bot initialized with username: " + botUsername);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Received update: " + update);

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText().trim();
        long chatId = update.getMessage().getChatId();

        if (messageText.equalsIgnoreCase("/start")) {
            sendHelpMessage(chatId);
        } else if (messageText.startsWith("/recommend")) {
            handleRecommendCommand(chatId, messageText);
        } else {
            sendHelpMessage(chatId);
        }
    }

    private void sendHelpMessage(long chatId) {
        String helpText = """
                Привет! Я бот рекомендаций

                Доступные команды:
/start — показать это сообщение.
/recommend <username> — получить рекомендации для пользователя.
                """;

        sendMessage(chatId, helpText);
    }

    private void handleRecommendCommand(long chatId, String messageText) {
        String[] parts = messageText.split("\\s+", 2);

        if (parts.length < 2) {
            sendMessage(chatId, "Укажите username. Пример: /recommend ivan123");
            return;
        }

        String username = parts[1].trim();
        List<User> users = userRepository.findByUsername(username);

        if (users.isEmpty()) {
            sendMessage(chatId, "Пользователь " + username + " не найден.");
            return;
        }

        UUID userId = users.get(0).getId();
        List<Recommendation> recs = recommendationService.getRecommendationsForUser(userId);

        if (recs.isEmpty()) {
            sendMessage(chatId, "Для пользователя " + username + " рекомендаций пока нет.");
            return;
        }

        StringBuilder sb = new StringBuilder("Рекомендации для ").append(username).append(":\n\n");
        for (Recommendation rec : recs) {
            sb.append("⭐ ").append(rec.getName()).append("\n")
                    .append(rec.getText()).append("\n\n");
        }

        sendMessage(chatId, sb.toString());
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(message);
            System.out.println("Sent message to chat: " + chatId);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }
}
