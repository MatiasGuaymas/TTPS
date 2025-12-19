package io.github.grupo01.volve_a_casa.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.ByteArrayInputStream;
import java.util.Base64;

/**
 * Clase responsable del envío de mensajes y fotos por Telegram.
 */
@Component
public class MessageSender {

    /**
     * Envía un mensaje de texto plano.
     */
    public void sendText(TelegramLongPollingBot bot, long chatId, String text) {
        SendMessage msg = new SendMessage(String.valueOf(chatId), text);
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    /**
     * Envía un mensaje con formato Markdown.
     */
    public void sendMarkdownText(TelegramLongPollingBot bot, long chatId, String text) {
        SendMessage msg = new SendMessage(String.valueOf(chatId), text);
        msg.setParseMode(ParseMode.MARKDOWN);
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            System.err.println("Error sending markdown message: " + e.getMessage());
        }
    }

    /**
     * Envía una acción de "escribiendo..." para mostrar que el bot está procesando.
     */
    public void sendTypingAction(TelegramLongPollingBot bot, long chatId) {
        SendChatAction action = new SendChatAction();
        action.setChatId(chatId);
        action.setAction(ActionType.TYPING);

        try {
            bot.execute(action);
        } catch (TelegramApiException e) {
            System.err.println("Error sending typing action: " + e.getMessage());
        }
    }

    /**
     * Envía una foto con caption en formato Markdown.
     * Usado para notificaciones de avistamientos.
     */
    public void sendPhotoNotification(TelegramLongPollingBot bot, Long chatId, String message, String photoBase64) {
        if (photoBase64 == null || photoBase64.isEmpty()) {
            sendMarkdownText(bot, chatId, message);
            return;
        }

        try {
            byte[] imageBytes = Base64.getDecoder().decode(photoBase64);
            ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setPhoto(new InputFile(imageStream, "avistamiento.jpg"));
            sendPhoto.setCaption(message);
            sendPhoto.setParseMode(ParseMode.MARKDOWN);

            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            System.err.println("Error enviando foto por Telegram: " + e.getMessage());
            sendMarkdownText(bot, chatId, message);
        } catch (IllegalArgumentException e) {
            System.err.println("Error decodificando imagen Base64: " + e.getMessage());
            sendMarkdownText(bot, chatId, message);
        }
    }

    /**
     * Envía una foto con caption en texto plano (sin formato).
     * Usado para el registro de mascotas perdidas.
     */
    public void sendPhotoPlainText(TelegramLongPollingBot bot, Long chatId, String message, String photoBase64) {
        if (photoBase64 == null || photoBase64.isEmpty()) {
            sendText(bot, chatId, message);
            return;
        }

        try {
            byte[] imageBytes = Base64.getDecoder().decode(photoBase64);
            ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setPhoto(new InputFile(imageStream, "mascota.jpg"));
            sendPhoto.setCaption(message);

            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            System.err.println("Error enviando foto por Telegram: " + e.getMessage());
            sendText(bot, chatId, message);
        } catch (IllegalArgumentException e) {
            System.err.println("Error decodificando imagen Base64: " + e.getMessage());
            sendText(bot, chatId, message);
        }
    }
}
