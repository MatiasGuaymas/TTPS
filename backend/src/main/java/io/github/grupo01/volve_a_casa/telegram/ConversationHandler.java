package io.github.grupo01.volve_a_casa.telegram;

import io.github.grupo01.volve_a_casa.controllers.dto.auth.AuthResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetCreateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.services.PetService;
import io.github.grupo01.volve_a_casa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase responsable de manejar el flujo de conversación para registrar mascotas perdidas.
 * Gestiona el estado de cada usuario y procesa cada paso del registro.
 */
@Component
public class ConversationHandler {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private TelegramMessages messages;

    private final ConcurrentHashMap<Long, ConversationState> conversations = new ConcurrentHashMap<>();

    /**
     * Inicia el proceso de registro de una mascota perdida.
     */
    public void startRegistration(TelegramLongPollingBot bot, long chatId) {
        ConversationState conversation = new ConversationState();
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_EMAIL);
        conversations.put(chatId, conversation);

        messageSender.sendText(bot, chatId, messages.get("register.start"));
    }

    /**
     * Cancela el proceso de registro activo.
     */
    public void cancelRegistration(TelegramLongPollingBot bot, long chatId) {
        ConversationState conversation = conversations.get(chatId);
        if (conversation != null && conversation.getCurrentStep() != ConversationState.ConversationStep.NONE) {
            conversation.reset();
            conversations.remove(chatId);
            messageSender.sendText(bot, chatId, messages.get("register.cancel.success"));
        } else {
            messageSender.sendText(bot, chatId, messages.get("register.cancel.no_active"));
        }
    }

    /**
     * Verifica si el usuario tiene una conversación activa.
     */
    public boolean hasActiveConversation(long chatId) {
        ConversationState conversation = conversations.get(chatId);
        return conversation != null && conversation.getCurrentStep() != ConversationState.ConversationStep.NONE;
    }

    /**
     * Procesa el mensaje del usuario según el paso actual de la conversación.
     */
    public void processStep(TelegramLongPollingBot bot, Update update, long chatId) {
        ConversationState conversation = conversations.get(chatId);
        if (conversation == null) return;

        switch (conversation.getCurrentStep()) {
            case WAITING_EMAIL -> processEmail(bot, update, chatId, conversation);
            case WAITING_PASSWORD -> processPassword(bot, update, chatId, conversation);
            case WAITING_NAME -> processName(bot, update, chatId, conversation);
            case WAITING_SIZE -> processSize(bot, update, chatId, conversation);
            case WAITING_STATE -> processState(bot, update, chatId, conversation);
            case WAITING_DATE -> processDate(bot, update, chatId, conversation);
            case WAITING_COLOR -> processColor(bot, update, chatId, conversation);
            case WAITING_TYPE -> processType(bot, update, chatId, conversation);
            case WAITING_RACE -> processRace(bot, update, chatId, conversation);
            case WAITING_WEIGHT -> processWeight(bot, update, chatId, conversation);
            case WAITING_PHOTO -> processPhoto(bot, update, chatId, conversation);
            case WAITING_LOCATION -> processLocation(bot, update, chatId, conversation);
            case WAITING_DESCRIPTION -> processDescription(bot, update, chatId, conversation);
        }
    }

    private void processEmail(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            messageSender.sendText(bot, chatId, messages.get("register.email.text_required"));
            return;
        }

        String email = update.getMessage().getText().trim();
        if (email.isEmpty() || email.startsWith("/") || !email.contains("@")) {
            messageSender.sendText(bot, chatId, messages.get("register.email.invalid"));
            return;
        }

        conversation.put("email", email);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_PASSWORD);
        messageSender.sendText(bot, chatId, messages.get("register.email.success", email));
    }

    private void processPassword(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            messageSender.sendText(bot, chatId, messages.get("register.password.text_required"));
            return;
        }

        String password = update.getMessage().getText().trim();
        if (password.isEmpty() || password.startsWith("/")) {
            messageSender.sendText(bot, chatId, messages.get("register.password.invalid"));
            return;
        }

        String email = conversation.getString("email");

        try {
            AuthResponseDTO authResponse = userService.authenticateUser(email, password);
            conversation.put("userId", authResponse.user().id());
            conversation.put("userName", authResponse.user().name());
            conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_NAME);

            messageSender.sendText(bot, chatId, messages.get("register.password.auth.success", authResponse.user().name()));
        } catch (ResponseStatusException e) {
            messageSender.sendText(bot, chatId, messages.get("register.password.auth.error"));
            conversation.reset();
            conversations.remove(chatId);
        }
    }

    private void processName(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            messageSender.sendText(bot, chatId, messages.get("register.name.text_required"));
            return;
        }

        String name = update.getMessage().getText().trim();
        if (name.isEmpty() || name.startsWith("/")) {
            messageSender.sendText(bot, chatId, messages.get("register.name.invalid"));
            return;
        }

        conversation.put("name", name);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_SIZE);
        messageSender.sendText(bot, chatId, messages.get("register.name.success", name));
    }

    private void processSize(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            messageSender.sendText(bot, chatId, messages.get("register.size.text_required"));
            return;
        }

        String text = update.getMessage().getText().trim();
        Pet.Size size = switch (text) {
            case "1", "PEQUENO", "pequeño", "pequeno" -> Pet.Size.PEQUENO;
            case "2", "MEDIANO", "mediano" -> Pet.Size.MEDIANO;
            case "3", "GRANDE", "grande" -> Pet.Size.GRANDE;
            default -> null;
        };

        if (size == null) {
            messageSender.sendText(bot, chatId, messages.get("register.size.invalid"));
            return;
        }

        conversation.put("size", size);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_STATE);
        messageSender.sendText(bot, chatId, messages.get("register.size.success", size));
    }

    private void processState(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            messageSender.sendText(bot, chatId, messages.get("register.state.text_required"));
            return;
        }

        String text = update.getMessage().getText().trim();
        Pet.State state = switch (text) {
            case "1", "PERDIDO_PROPIO" -> Pet.State.PERDIDO_PROPIO;
            case "2", "PERDIDO_AJENO" -> Pet.State.PERDIDO_AJENO;
            case "3", "RECUPERADO" -> Pet.State.RECUPERADO;
            case "4", "ADOPTADO" -> Pet.State.ADOPTADO;
            default -> null;
        };

        if (state == null) {
            messageSender.sendText(bot, chatId, messages.get("register.state.invalid"));
            return;
        }

        conversation.put("state", state);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_DATE);
        messageSender.sendText(bot, chatId, messages.get("register.state.success", state));
    }

    private void processDate(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            messageSender.sendText(bot, chatId, messages.get("register.date.text_required"));
            return;
        }

        String text = update.getMessage().getText().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate date = LocalDate.parse(text, formatter);
            if (date.isAfter(LocalDate.now())) {
                messageSender.sendText(bot, chatId, messages.get("register.date.future"));
                return;
            }

            conversation.put("lostDate", date);
            conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_COLOR);
            messageSender.sendText(bot, chatId, messages.get("register.date.success", date.format(formatter)));
        } catch (DateTimeParseException e) {
            messageSender.sendText(bot, chatId, messages.get("register.date.invalid"));
        }
    }

    private void processColor(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            messageSender.sendText(bot, chatId, messages.get("register.color.text_required"));
            return;
        }

        String color = update.getMessage().getText().trim();
        if (color.isEmpty() || color.startsWith("/")) {
            messageSender.sendText(bot, chatId, messages.get("register.color.invalid"));
            return;
        }

        conversation.put("color", color);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_TYPE);
        messageSender.sendText(bot, chatId, messages.get("register.color.success", color));
    }

    private void processType(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            messageSender.sendText(bot, chatId, messages.get("register.type.text_required"));
            return;
        }

        String text = update.getMessage().getText().trim();
        Pet.Type type = switch (text) {
            case "1", "PERRO", "perro" -> Pet.Type.PERRO;
            case "2", "GATO", "gato" -> Pet.Type.GATO;
            case "3", "COBAYA", "cobaya" -> Pet.Type.COBAYA;
            case "4", "LORO", "loro" -> Pet.Type.LORO;
            case "5", "CONEJO", "conejo" -> Pet.Type.CONEJO;
            case "6", "CABALLO", "caballo" -> Pet.Type.CABALLO;
            case "7", "TORTUGA", "tortuga" -> Pet.Type.TORTUGA;
            default -> null;
        };

        if (type == null) {
            messageSender.sendText(bot, chatId, messages.get("register.type.invalid"));
            return;
        }

        conversation.put("type", type);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_RACE);
        messageSender.sendText(bot, chatId, messages.get("register.type.success", type));
    }

    private void processRace(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            messageSender.sendText(bot, chatId, messages.get("register.race.text_required"));
            return;
        }

        String race = update.getMessage().getText().trim();
        if (race.isEmpty() || race.startsWith("/")) {
            messageSender.sendText(bot, chatId, messages.get("register.race.invalid"));
            return;
        }

        conversation.put("race", race);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_WEIGHT);
        messageSender.sendText(bot, chatId, messages.get("register.race.success", race));
    }

    private void processWeight(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            messageSender.sendText(bot, chatId, messages.get("register.weight.text_required"));
            return;
        }

        String text = update.getMessage().getText().trim();
        try {
            float weight = Float.parseFloat(text);
            if (weight <= 0) {
                messageSender.sendText(bot, chatId, messages.get("register.weight.positive"));
                return;
            }

            conversation.put("weight", weight);
            conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_PHOTO);
            messageSender.sendText(bot, chatId, messages.get("register.weight.success", weight));
        } catch (NumberFormatException e) {
            messageSender.sendText(bot, chatId, messages.get("register.weight.invalid"));
        }
    }

    private void processPhoto(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasPhoto()) {
            messageSender.sendText(bot, chatId, messages.get("register.photo.required"));
            return;
        }

        try {
            List<PhotoSize> photos = update.getMessage().getPhoto();
            PhotoSize photo = photos.get(photos.size() - 1);

            GetFile getFile = new GetFile();
            getFile.setFileId(photo.getFileId());
            org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);

            String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + file.getFilePath();
            URL url = new URL(fileUrl);

            try (InputStream inputStream = url.openStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                byte[] imageBytes = outputStream.toByteArray();
                String photoBase64 = Base64.getEncoder().encodeToString(imageBytes);

                conversation.put("photoBase64", photoBase64);
                conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_LOCATION);
                messageSender.sendText(bot, chatId, messages.get("register.photo.success"));
            }
        } catch (Exception e) {
            System.err.println("Error procesando foto: " + e.getMessage());
            messageSender.sendText(bot, chatId, messages.get("register.photo.error"));
        }
    }

    private void processLocation(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasLocation()) {
            messageSender.sendText(bot, chatId, messages.get("register.location.required"));
            return;
        }

        Location location = update.getMessage().getLocation();
        conversation.put("latitude", location.getLatitude().floatValue());
        conversation.put("longitude", location.getLongitude().floatValue());
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_DESCRIPTION);

        messageSender.sendText(bot, chatId, messages.get("register.location.success"));
    }

    private void processDescription(TelegramLongPollingBot bot, Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            messageSender.sendText(bot, chatId, messages.get("register.description.text_required"));
            return;
        }

        String description = update.getMessage().getText().trim();
        if (description.isEmpty() || description.startsWith("/")) {
            messageSender.sendText(bot, chatId, messages.get("register.description.invalid"));
            return;
        }

        conversation.put("description", description);
        savePet(bot, chatId, conversation);
    }

    private void savePet(TelegramLongPollingBot bot, long chatId, ConversationState conversation) {
        try {
            Long userId = conversation.getLong("userId");
            User creator = userService.findById(userId);

            PetCreateDTO petCreateDTO = new PetCreateDTO(
                    conversation.getString("name"),
                    conversation.getSize("size"),
                    conversation.getString("description"),
                    conversation.getString("color"),
                    conversation.getString("race"),
                    conversation.getFloat("weight"),
                    conversation.getFloat("latitude"),
                    conversation.getFloat("longitude"),
                    conversation.getState("state"),
                    conversation.getType("type"),
                    conversation.getString("photoBase64")
            );

            var petResponse = petService.createPet(creator, petCreateDTO);

            String summary = messages.get("register.save.success",
                    petResponse.id(),
                    conversation.getString("name"),
                    conversation.getString("userName"),
                    conversation.get("size"),
                    conversation.get("state"),
                    conversation.get("lostDate"),
                    conversation.getString("color"),
                    conversation.get("type"),
                    conversation.getString("race"),
                    conversation.getFloat("weight"),
                    conversation.getString("description"),
                    petResponse.id()
            );

            String photoBase64 = conversation.getString("photoBase64");
            if (photoBase64 != null && !photoBase64.isEmpty()) {
                messageSender.sendPhotoPlainText(bot, chatId, summary, photoBase64);
            } else {
                messageSender.sendText(bot, chatId, summary);
            }

            conversation.reset();
            conversations.remove(chatId);

        } catch (Exception e) {
            System.err.println("Error guardando mascota: " + e.getMessage());
            e.printStackTrace();
            messageSender.sendText(bot, chatId, messages.get("register.save.error", e.getMessage()));
            conversation.reset();
            conversations.remove(chatId);
        }
    }
}
