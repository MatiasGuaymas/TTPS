package io.github.grupo01.volve_a_casa.telegram;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetDetailDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetSummaryDTO;
import io.github.grupo01.volve_a_casa.integrations.IACliente;
import io.github.grupo01.volve_a_casa.services.PetService;
import io.github.grupo01.volve_a_casa.services.TelegramNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IATelegramBot extends TelegramLongPollingBot {
    private static final int MAX_REQUESTS_PER_MIN = 5;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final IACliente iaCliente;
    
    @Autowired
    private TelegramNotificationService notificationService;

    @Autowired
    private PetService petService;

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
            sendText(chatId, "¡Hola! 👋 Soy el bot de Volve a Casa.\n\nUsa /comandos para ver todas las funcionalidades disponibles.");
            return;
        }

        // Comando /comandos
        if(messageText.equals("/comandos")) {
            String commandsHelp = """
                    📋 *Comandos disponibles:*
                    
                    🐾 /mascotas
                    Lista todas las mascotas perdidas con información resumida.
                    
                    🔍 /mascota <id>
                    Muestra información detallada de una mascota específica.
                    Ejemplo: /mascota 123
                    
                    🤖 /preguntar <pregunta>
                    Realiza cualquier pregunta sobre mascotas perdidas o la aplicación.
                    Ejemplo: /preguntar ¿Cómo reportar una mascota?
                    
                    🔔 /suscribir <id_mascota>
                    Suscríbete para recibir notificaciones de avistamientos de una mascota específica.
                    Ejemplo: /suscribir 123
                    
                    🔕 /desuscribir <id_mascota>
                    Deja de recibir notificaciones de avistamientos de una mascota.
                    Ejemplo: /desuscribir 123
                    
                    📋 /comandos
                    Muestra este mensaje de ayuda.
                    """;
            sendMarkdownText(chatId, commandsHelp);
            return;
        }

        // Comando /mascotas
        if(messageText.equals("/mascotas")) {
            handleMascotas(chatId);
            return;
        }

        // Comando /mascota
        if(messageText.startsWith("/mascota")) {
            handleMascota(chatId, messageText);
            return;
        }

        // Comando /suscribir
        if(messageText.startsWith("/suscribir")) {
            handleSuscribir(chatId, messageText);
            return;
        }

        // Comando /desuscribir
        if(messageText.startsWith("/desuscribir")) {
            handleDesuscribir(chatId, messageText);
            return;
        }

        // Comando /preguntar
        if(messageText.startsWith("/preguntar")) {
            handlePreguntar(chatId, messageText);
            return;
        }

        // Comando no reconocido
        sendText(chatId, "⚠️ Comando no reconocido. Usa /comandos para ver la lista de comandos disponibles.");
    }

    private void handleSuscribir(long chatId, String messageText) {
        String[] parts = messageText.split("\\s+");
        
        if(parts.length < 2) {
            sendText(chatId, "⚠️ Formato incorrecto. Usa: /suscribir <id_mascota>\n\nEjemplo: /suscribir 123");
            return;
        }

        try {
            Long petId = Long.parseLong(parts[1]);
            String response = notificationService.suscribir(chatId, petId);
            sendText(chatId, response);
        } catch (NumberFormatException e) {
            sendText(chatId, "❌ El ID de mascota debe ser un número válido.\n\nEjemplo: /suscribir 123");
        }
    }

    private void handleDesuscribir(long chatId, String messageText) {
        String[] parts = messageText.split("\\s+");
        
        if(parts.length < 2) {
            sendText(chatId, "⚠️ Formato incorrecto. Usa: /desuscribir <id_mascota>\n\nEjemplo: /desuscribir 123");
            return;
        }

        try {
            Long petId = Long.parseLong(parts[1]);
            String response = notificationService.desuscribir(chatId, petId);
            sendText(chatId, response);
        } catch (NumberFormatException e) {
            sendText(chatId, "❌ El ID de mascota debe ser un número válido.\n\nEjemplo: /desuscribir 123");
        }
    }

    private void handlePreguntar(long chatId, String messageText) {
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

    private void handleMascotas(long chatId) {
        try {
            List<PetSummaryDTO> pets = petService.getAllLostPetsSummary();
            
            if (pets.isEmpty()) {
                sendText(chatId, "✅ ¡Buenas noticias! No hay mascotas perdidas en este momento.");
                return;
            }

            StringBuilder message = new StringBuilder();
            message.append("🐾 *Mascotas Perdidas* (").append(pets.size()).append(" encontradas)\n\n");
            
            // Enviar en lotes para evitar mensajes muy largos
            int batchSize = 5;
            for (int i = 0; i < pets.size(); i += batchSize) {
                StringBuilder batch = new StringBuilder();
                if (i == 0) {
                    batch.append(message);
                }
                
                int end = Math.min(i + batchSize, pets.size());
                for (int j = i; j < end; j++) {
                    batch.append(pets.get(j).toTelegramFormat());
                    if (j < end - 1) {
                        batch.append("\n---\n\n");
                    }
                }
                
                sendMarkdownText(chatId, batch.toString());
            }
            
            sendText(chatId, "💡 Usa /mascota <id> para ver más detalles de una mascota específica.");
            
        } catch (Exception e) {
            System.err.println("Error al obtener listado de mascotas: " + e.getMessage());
            sendText(chatId, "❌ Lo siento, hubo un error al obtener el listado de mascotas. Intenta de nuevo más tarde.");
        }
    }

    private void handleMascota(long chatId, String messageText) {
        String[] parts = messageText.split("\\s+");
        
        if(parts.length < 2) {
            sendText(chatId, "⚠️ Formato incorrecto. Usa: /mascota <id>\n\nEjemplo: /mascota 123");
            return;
        }

        try {
            Long petId = Long.parseLong(parts[1]);
            PetDetailDTO pet = petService.getPetDetailForTelegram(petId);
            
            // Enviar con foto si está disponible
            if (pet.photoBase64() != null && !pet.photoBase64().isEmpty()) {
                sendPhotoNotification(chatId, pet.toTelegramFormat(), pet.photoBase64());
            } else {
                sendMarkdownText(chatId, pet.toTelegramFormat());
            }
        } catch (NumberFormatException e) {
            sendText(chatId, "❌ El ID de mascota debe ser un número válido.\n\nEjemplo: /mascota 123");
        } catch (Exception e) {
            System.err.println("Error al obtener detalle de mascota: " + e.getMessage());
            sendText(chatId, "❌ No se encontró una mascota con ese ID o hubo un error al obtener la información.");
        }
    }

    public void sendNotification(Long chatId, String message) {
        sendMarkdownText(chatId, message);
    }

    public void sendPhotoNotification(Long chatId, String message, String photoBase64) {
        if (photoBase64 == null || photoBase64.isEmpty()) {
            sendMarkdownText(chatId, message);
            return;
        }

        try {
            // Decodificar la imagen Base64
            byte[] imageBytes = Base64.getDecoder().decode(photoBase64);
            ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setPhoto(new InputFile(imageStream, "avistamiento.jpg"));
            sendPhoto.setCaption(message);
            sendPhoto.setParseMode(ParseMode.MARKDOWN);

            execute(sendPhoto);
        } catch (TelegramApiException e) {
            System.err.println("Error enviando foto por Telegram: " + e.getMessage());
            // Si falla el envío de la foto, enviar solo el mensaje
            sendMarkdownText(chatId, message);
        } catch (IllegalArgumentException e) {
            System.err.println("Error decodificando imagen Base64: " + e.getMessage());
            sendMarkdownText(chatId, message);
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

    private void sendMarkdownText(long chatId, String text) {
        SendMessage msg = new SendMessage(String.valueOf(chatId), text);
        msg.setParseMode(ParseMode.MARKDOWN);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            System.err.println("Error sending markdown message: " + e.getMessage());
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
