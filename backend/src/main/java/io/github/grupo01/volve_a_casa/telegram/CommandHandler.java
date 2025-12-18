package io.github.grupo01.volve_a_casa.telegram;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetDetailDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetSummaryDTO;
import io.github.grupo01.volve_a_casa.integrations.IACliente;
import io.github.grupo01.volve_a_casa.services.PetService;
import io.github.grupo01.volve_a_casa.services.TelegramNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase responsable de procesar los comandos del bot de Telegram.
 * Maneja todos los comandos disponibles: /mascotas, /mascota, /suscribir, etc.
 */
@Component
public class CommandHandler {

    private static final int MAX_REQUESTS_PER_MIN = 5;

    @Autowired
    private PetService petService;

    @Autowired
    private TelegramNotificationService notificationService;

    @Autowired
    private IACliente iaCliente;

    @Autowired
    private MessageSender messageSender;

    private final ConcurrentHashMap<Long, UserWindow> userWindows = new ConcurrentHashMap<>();

    /**
     * Maneja el comando /comandos - muestra la ayuda.
     */
    public void handleComandos(TelegramLongPollingBot bot, long chatId) {
        String commandsHelp = """
                📋 *Comandos disponibles:*
                
                🐾 /mascotas
                Lista todas las mascotas perdidas con información resumida.
                
                🔍 /mascota <id>
                Muestra información detallada de una mascota específica.
                Ejemplo: /mascota 123
                
                📝 /perdida
                Registra una nueva mascota perdida de forma interactiva.
                
                🤖 /preguntar <pregunta>
                Realiza cualquier pregunta sobre mascotas perdidas o la aplicación.
                Ejemplo: /preguntar ¿Cómo reportar una mascota?
                
                🔔 /suscribir <id_mascota>
                Suscríbete para recibir notificaciones de avistamientos de una mascota específica.
                Ejemplo: /suscribir 123
                
                🔕 /desuscribir <id_mascota>
                Deja de recibir notificaciones de avistamientos de una mascota.
                Ejemplo: /desuscribir 123
                
                ❌ /cancelar
                Cancela el proceso de registro actual.
                
                📋 /comandos
                Muestra este mensaje de ayuda.
                """;
        messageSender.sendMarkdownText(bot, chatId, commandsHelp);
    }

    /**
     * Maneja el comando /mascotas - lista todas las mascotas perdidas.
     */
    public void handleMascotas(TelegramLongPollingBot bot, long chatId) {
        try {
            List<PetSummaryDTO> pets = petService.getAllLostPetsSummary();

            if (pets.isEmpty()) {
                messageSender.sendText(bot, chatId, "✅ ¡Buenas noticias! No hay mascotas perdidas en este momento.");
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

                messageSender.sendMarkdownText(bot, chatId, batch.toString());
            }

            messageSender.sendText(bot, chatId, "💡 Usa /mascota <id> para ver más detalles de una mascota específica.");

        } catch (Exception e) {
            System.err.println("Error al obtener listado de mascotas: " + e.getMessage());
            messageSender.sendText(bot, chatId, "❌ Lo siento, hubo un error al obtener el listado de mascotas. Intenta de nuevo más tarde.");
        }
    }

    /**
     * Maneja el comando /mascota <id> - muestra detalles de una mascota específica.
     */
    public void handleMascota(TelegramLongPollingBot bot, long chatId, String messageText) {
        String[] parts = messageText.split("\\s+");

        if (parts.length < 2) {
            messageSender.sendText(bot, chatId, "⚠️ Formato incorrecto. Usa: /mascota <id>\n\nEjemplo: /mascota 123");
            return;
        }

        try {
            Long petId = Long.parseLong(parts[1]);
            PetDetailDTO pet = petService.getPetDetailForTelegram(petId);

            if (pet.photoBase64() != null && !pet.photoBase64().isEmpty()) {
                messageSender.sendPhotoNotification(bot, chatId, pet.toTelegramFormat(), pet.photoBase64());
            } else {
                messageSender.sendMarkdownText(bot, chatId, pet.toTelegramFormat());
            }
        } catch (NumberFormatException e) {
            messageSender.sendText(bot, chatId, "❌ El ID de mascota debe ser un número válido.\n\nEjemplo: /mascota 123");
        } catch (Exception e) {
            System.err.println("Error al obtener detalle de mascota: " + e.getMessage());
            messageSender.sendText(bot, chatId, "❌ No se encontró una mascota con ese ID o hubo un error al obtener la información.");
        }
    }

    /**
     * Maneja el comando /suscribir <id> - suscribe al usuario a notificaciones.
     */
    public void handleSuscribir(TelegramLongPollingBot bot, long chatId, String messageText) {
        String[] parts = messageText.split("\\s+");

        if (parts.length < 2) {
            messageSender.sendText(bot, chatId, "⚠️ Formato incorrecto. Usa: /suscribir <id_mascota>\n\nEjemplo: /suscribir 123");
            return;
        }

        try {
            Long petId = Long.parseLong(parts[1]);
            String response = notificationService.suscribir(chatId, petId);
            messageSender.sendText(bot, chatId, response);
        } catch (NumberFormatException e) {
            messageSender.sendText(bot, chatId, "❌ El ID de mascota debe ser un número válido.\n\nEjemplo: /suscribir 123");
        }
    }

    /**
     * Maneja el comando /desuscribir <id> - desuscribe al usuario de notificaciones.
     */
    public void handleDesuscribir(TelegramLongPollingBot bot, long chatId, String messageText) {
        String[] parts = messageText.split("\\s+");

        if (parts.length < 2) {
            messageSender.sendText(bot, chatId, "⚠️ Formato incorrecto. Usa: /desuscribir <id_mascota>\n\nEjemplo: /desuscribir 123");
            return;
        }

        try {
            Long petId = Long.parseLong(parts[1]);
            String response = notificationService.desuscribir(chatId, petId);
            messageSender.sendText(bot, chatId, response);
        } catch (NumberFormatException e) {
            messageSender.sendText(bot, chatId, "❌ El ID de mascota debe ser un número válido.\n\nEjemplo: /desuscribir 123");
        }
    }

    /**
     * Maneja el comando /preguntar <pregunta> - usa IA para responder preguntas.
     */
    public void handlePreguntar(TelegramLongPollingBot bot, long chatId, String messageText) {
        if (isRateLimited(chatId)) {
            messageSender.sendText(bot, chatId, "⏱️ Has alcanzado el límite de preguntas. Por favor espera un minuto.");
            return;
        }

        String prompt = messageText.substring("/preguntar".length()).trim();
        if (prompt.isEmpty()) {
            messageSender.sendText(bot, chatId, "Por favor escribe una pregunta después del comando.\n\nEjemplo: /preguntar ¿Qué es Volve a Casa?");
            return;
        }

        messageSender.sendTypingAction(bot, chatId);

        try {
            String promptWithContext = "Responde en español de manera clara y concisa. " + prompt;
            String answer = iaCliente.ask(promptWithContext);
            messageSender.sendText(bot, chatId, answer);
        } catch (IOException e) {
            System.err.println("API callback failed: " + e.getMessage());
            messageSender.sendText(bot, chatId, "❌ Lo siento, hubo un error al procesar tu pregunta. Intenta de nuevo más tarde.");
        }
    }

    private boolean isRateLimited(long chatId) {
        Instant now = Instant.now();
        userWindows.compute(chatId, (id, window) -> {
            if (window == null || Duration.between(window.windowStart, now).toMinutes() >= 1) {
                return new UserWindow(now, 1);
            }
            return new UserWindow(window.windowStart, window.counter + 1);
        });
        return userWindows.get(chatId).counter > MAX_REQUESTS_PER_MIN;
    }

    private record UserWindow(Instant windowStart, int counter) {
    }
}
