package io.github.grupo01.volve_a_casa.telegram;

import io.github.grupo01.volve_a_casa.integrations.IACliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Clase principal del bot de Telegram.
 * Coordina el procesamiento de mensajes delegando a handlers específicos.
 */
@Component
public class IATelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private CommandHandler commandHandler;

    @Autowired
    private ConversationHandler conversationHandler;

    @Autowired
    private TelegramMessages messages;

    private final IACliente iaCliente;

    @Autowired
    public IATelegramBot(IACliente iaCliente) {
        this.iaCliente = iaCliente;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        long chatId = update.getMessage().getChatId();

        // Si el usuario está en una conversación activa, procesarla primero
        if (conversationHandler.hasActiveConversation(chatId)) {
            conversationHandler.processStep(this, update, chatId);
            return;
        }

        // Procesar comandos normales
        if (!update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText();

        // Comando /start o saludos
        if (messageText.equals("/start") || messageText.equalsIgnoreCase("hola") || messageText.equalsIgnoreCase("hello")) {
            messageSender.sendText(this, chatId, messages.welcome());
            return;
        }

        // Comando /comandos
        if (messageText.equals("/comandos")) {
            commandHandler.handleComandos(this, chatId);
            return;
        }

        // Comando /mascotas
        if (messageText.equals("/mascotas")) {
            commandHandler.handleMascotas(this, chatId);
            return;
        }

        // Comando /mascota <id>
        if (messageText.startsWith("/mascota")) {
            commandHandler.handleMascota(this, chatId, messageText);
            return;
        }

        // Comando /suscribir <id>
        if (messageText.startsWith("/suscribir")) {
            commandHandler.handleSuscribir(this, chatId, messageText);
            return;
        }

        // Comando /desuscribir <id>
        if (messageText.startsWith("/desuscribir")) {
            commandHandler.handleDesuscribir(this, chatId, messageText);
            return;
        }

        // Comando /preguntar <pregunta>
        if (messageText.startsWith("/preguntar")) {
            commandHandler.handlePreguntar(this, chatId, messageText);
            return;
        }

        // Comando /perdida - inicia el registro de mascota
        if (messageText.equals("/perdida")) {
            conversationHandler.startRegistration(this, chatId);
            return;
        }

        // Comando /cancelar - cancela el registro activo
        if (messageText.equals("/cancelar")) {
            conversationHandler.cancelRegistration(this, chatId);
            return;
        }

        // Comando no reconocido
        messageSender.sendText(this, chatId, messages.commandUnknown());
    }

    /**
     * Método público para enviar notificaciones desde otros servicios.
     */
    public void sendNotification(Long chatId, String message) {
        messageSender.sendMarkdownText(this, chatId, message);
    }

    /**
     * Método público para enviar notificaciones con foto desde otros servicios.
     */
    public void sendPhotoNotification(Long chatId, String message, String photoBase64) {
        messageSender.sendPhotoNotification(this, chatId, message, photoBase64);
    }

    @Override
    public String getBotUsername() {
        return "Volve a Casa BOT";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
