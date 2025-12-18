package io.github.grupo01.volve_a_casa.telegram;

import io.github.grupo01.volve_a_casa.integrations.IACliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IATelegramBot extends TelegramLongPollingBot {
    private static final int MAX_REQUESTS_PER_MIN = 5;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final IACliente iaCliente;

    private final ConcurrentHashMap<Long, UserWindow> userWindows = new ConcurrentHashMap<>();

    @Autowired
    public IATelegramBot(IACliente iaCliente) {
        this.iaCliente = iaCliente;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        // Manejar comando /start o saludos
        if(messageText.equals("/start") || messageText.equalsIgnoreCase("hola") || messageText.equalsIgnoreCase("hello")) {
            sendText(chatId, "¡Hola! 👋 Soy el bot de Volve a Casa.\n\nEscribe /preguntar seguido de tu pregunta para que pueda ayudarte.\n\nEjemplo: /preguntar ¿Cómo reportar una mascota perdida?");
            return;
        }

        // Verificar que empiece con /preguntar
        if(!messageText.startsWith("/preguntar")) {
            sendText(chatId, "Por favor usa el comando /preguntar seguido de tu pregunta.\n\nEjemplo: /preguntar ¿Cómo funciona la app?");
            return;
        }

        // Verificar límite de rate
        if(isRateLimited(chatId)) {
            sendText(chatId, "⏱️ Has alcanzado el límite de preguntas. Por favor espera un minuto.");
            return;
        }

        // Extraer la pregunta
        String prompt = messageText.substring("/preguntar".length()).trim();
        if (prompt.isEmpty()) {
            sendText(chatId, "Por favor escribe una pregunta después del comando.\n\nEjemplo: /preguntar ¿Qué es Volve a Casa?");
            return;
        }

        // Mostrar que está escribiendo
        sendTypingAction(chatId);

        try {
            // Agregar contexto en español al prompt
            String promptWithContext = "Responde en español de manera clara y concisa. " + prompt;
            String answer = iaCliente.ask(promptWithContext);
            sendText(chatId, answer);
        } catch (IOException e) {
            System.err.println("API callback failed: " + e.getMessage());
            sendText(chatId, "❌ Lo siento, hubo un error al procesar tu pregunta. Intenta de nuevo más tarde.");
        }
    }

    @Override
    public String getBotUsername() {
        return "Volve a Casa BOT";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void sendTypingAction(long chatId) {
        SendChatAction action = new SendChatAction();
        action.setChatId(chatId);

        action.setAction(ActionType.TYPING);

        try {
            execute(action);
        } catch (TelegramApiException e) {
            System.err.println("Error sending typing action: " + e.getMessage());
        }
    }

    private void sendText(long chatId, String text) {
        SendMessage msg = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    private boolean isRateLimited(long chatId) {
        Instant now = Instant.now();
        userWindows.compute(chatId, (id, window) -> {
            if (window == null || Duration.between(window.windowStart, now).toMinutes() >= 1) {
                return new UserWindow(now, 1);
            }
            return new UserWindow(window.windowStart, window.counter+1);
        });
        return userWindows.get(chatId).counter > MAX_REQUESTS_PER_MIN;
    }

    private record UserWindow(Instant windowStart, int counter) {

    }
}
