package io.github.grupo01.volve_a_casa.telegram;

import io.github.grupo01.volve_a_casa.controllers.dto.auth.AuthResponseDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetDetailDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetSummaryDTO;
import io.github.grupo01.volve_a_casa.integrations.IACliente;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.services.PetService;
import io.github.grupo01.volve_a_casa.services.TelegramNotificationService;
import io.github.grupo01.volve_a_casa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    
    @Autowired
    private UserService userService;

    private final ConcurrentHashMap<Long, UserWindow> userWindows = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ConversationState> conversations = new ConcurrentHashMap<>();

    @Autowired
    public IATelegramBot(IACliente iaCliente) {
        this.iaCliente = iaCliente;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        
        // Verificar si el usuario está en una conversación activa
        ConversationState conversation = conversations.get(chatId);
        
        // Si está en conversación, procesar según el paso actual
        if (conversation != null && conversation.getCurrentStep() != ConversationState.ConversationStep.NONE) {
            processConversationStep(update, chatId, conversation);
            return;
        }
        
        // Procesamiento normal de comandos
        if(!update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText();

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
        
        // Comando /perdida
        if(messageText.equals("/perdida")) {
            handlePerdida(chatId);
            return;
        }
        
        // Comando /cancelar
        if(messageText.equals("/cancelar")) {
            handleCancelar(chatId);
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
    
    private void handlePerdida(long chatId) {
        ConversationState conversation = new ConversationState();
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_EMAIL);
        conversations.put(chatId, conversation);
        
        sendText(chatId, "📝 *Registro de Mascota Perdida*\n\n" +
                "Primero necesito autenticarte.\n" +
                "Puedes cancelar en cualquier momento con /cancelar\n\n" +
                "Por favor, ingresa tu *email* registrado en Volve a Casa:");
    }
    
    private void handleCancelar(long chatId) {
        ConversationState conversation = conversations.get(chatId);
        if (conversation != null && conversation.getCurrentStep() != ConversationState.ConversationStep.NONE) {
            conversation.reset();
            conversations.remove(chatId);
            sendText(chatId, "❌ Registro cancelado. Puedes iniciar uno nuevo con /perdida");
        } else {
            sendText(chatId, "No hay ningún proceso activo para cancelar.");
        }
    }
    
    private void processConversationStep(Update update, long chatId, ConversationState conversation) {
        switch (conversation.getCurrentStep()) {
            case WAITING_EMAIL -> processEmail(update, chatId, conversation);
            case WAITING_PASSWORD -> processPassword(update, chatId, conversation);
            case WAITING_NAME -> processName(update, chatId, conversation);
            case WAITING_SIZE -> processSize(update, chatId, conversation);
            case WAITING_STATE -> processState(update, chatId, conversation);
            case WAITING_DATE -> processDate(update, chatId, conversation);
            case WAITING_COLOR -> processColor(update, chatId, conversation);
            case WAITING_TYPE -> processType(update, chatId, conversation);
            case WAITING_RACE -> processRace(update, chatId, conversation);
            case WAITING_WEIGHT -> processWeight(update, chatId, conversation);
            case WAITING_PHOTO -> processPhoto(update, chatId, conversation);
            case WAITING_LOCATION -> processLocation(update, chatId, conversation);
            case WAITING_DESCRIPTION -> processDescription(update, chatId, conversation);
        }
    }
    
    private void processEmail(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            sendText(chatId, "⚠️ Por favor, envía tu email como texto.");
            return;
        }
        
        String email = update.getMessage().getText().trim();
        if (email.isEmpty() || email.startsWith("/") || !email.contains("@")) {
            sendText(chatId, "⚠️ Por favor, ingresa un email válido.");
            return;
        }
        
        conversation.put("email", email);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_PASSWORD);
        sendText(chatId, "✅ Email: " + email + "\n\n" +
                "Ahora ingresa tu *contraseña*:\n" +
                "⚠️ Por seguridad, elimina este mensaje después de enviarlo.");
    }
    
    private void processPassword(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            sendText(chatId, "⚠️ Por favor, envía tu contraseña como texto.");
            return;
        }
        
        String password = update.getMessage().getText().trim();
        if (password.isEmpty() || password.startsWith("/")) {
            sendText(chatId, "⚠️ Por favor, ingresa una contraseña válida.");
            return;
        }
        
        String email = conversation.getString("email");
        
        // Intentar autenticar
        try {
            AuthResponseDTO authResponse = userService.authenticateUser(email, password);
            conversation.put("userId", authResponse.user().id());
            conversation.put("userName", authResponse.user().name());
            conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_NAME);
            
            sendText(chatId, "✅ *¡Autenticación exitosa!*\n\n" +
                    "Bienvenido/a " + authResponse.user().name() + "!\n\n" +
                    "Ahora vamos a registrar tu mascota perdida.\n" +
                    "Por favor, ingresa el *nombre de la mascota*:");
        } catch (ResponseStatusException e) {
            sendText(chatId, "❌ Credenciales inválidas. El email o contraseña son incorrectos.\n\n" +
                    "Usa /perdida para intentar de nuevo o /cancelar para salir.");
            conversation.reset();
            conversations.remove(chatId);
        }
    }
    
    private void processName(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            sendText(chatId, "⚠️ Por favor, envía el nombre de la mascota como texto.");
            return;
        }
        
        String name = update.getMessage().getText().trim();
        if (name.isEmpty() || name.startsWith("/")) {
            sendText(chatId, "⚠️ Por favor, ingresa un nombre válido.");
            return;
        }
        
        conversation.put("name", name);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_SIZE);
        sendText(chatId, "✅ Nombre registrado: " + name + "\n\n" +
                "Selecciona el *tamaño* de la mascota:\n" +
                "1️⃣ PEQUENO\n" +
                "2️⃣ MEDIANO\n" +
                "3️⃣ GRANDE\n\n" +
                "Responde con el número (1, 2 o 3):");
    }
    
    private void processSize(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            sendText(chatId, "⚠️ Por favor, envía el tamaño como texto.");
            return;
        }
        
        String text = update.getMessage().getText().trim();
        Pet.Size size = null;
        
        switch (text) {
            case "1", "PEQUENO", "pequeño", "pequeno" -> size = Pet.Size.PEQUENO;
            case "2", "MEDIANO", "mediano" -> size = Pet.Size.MEDIANO;
            case "3", "GRANDE", "grande" -> size = Pet.Size.GRANDE;
            default -> {
                sendText(chatId, "⚠️ Opción no válida. Por favor responde con 1, 2 o 3.");
                return;
            }
        }
        
        conversation.put("size", size);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_STATE);
        sendText(chatId, "✅ Tamaño registrado: " + size + "\n\n" +
                "Selecciona el *estado* de la mascota:\n" +
                "1️⃣ PERDIDO_PROPIO (es tu mascota)\n" +
                "2️⃣ PERDIDO_AJENO (viste una mascota perdida)\n" +
                "3️⃣ RECUPERADO\n" +
                "4️⃣ ADOPTADO\n\n" +
                "Responde con el número (1, 2, 3 o 4):");
    }
    
    private void processState(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            sendText(chatId, "⚠️ Por favor, envía el estado como texto.");
            return;
        }
        
        String text = update.getMessage().getText().trim();
        Pet.State state = null;
        
        switch (text) {
            case "1", "PERDIDO_PROPIO" -> state = Pet.State.PERDIDO_PROPIO;
            case "2", "PERDIDO_AJENO" -> state = Pet.State.PERDIDO_AJENO;
            case "3", "RECUPERADO" -> state = Pet.State.RECUPERADO;
            case "4", "ADOPTADO" -> state = Pet.State.ADOPTADO;
            default -> {
                sendText(chatId, "⚠️ Opción no válida. Por favor responde con 1, 2, 3 o 4.");
                return;
            }
        }
        
        conversation.put("state", state);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_DATE);
        sendText(chatId, "✅ Estado registrado: " + state + "\n\n" +
                "Ingresa la *fecha de desaparición* en formato DD/MM/AAAA\n" +
                "Ejemplo: 15/12/2025");
    }
    
    private void processDate(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            sendText(chatId, "⚠️ Por favor, envía la fecha como texto.");
            return;
        }
        
        String text = update.getMessage().getText().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        try {
            LocalDate date = LocalDate.parse(text, formatter);
            if (date.isAfter(LocalDate.now())) {
                sendText(chatId, "⚠️ La fecha no puede ser futura. Por favor ingresa una fecha válida.");
                return;
            }
            
            conversation.put("lostDate", date);
            conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_COLOR);
            sendText(chatId, "✅ Fecha registrada: " + date.format(formatter) + "\n\n" +
                    "Ingresa el *color* de la mascota:");
        } catch (DateTimeParseException e) {
            sendText(chatId, "⚠️ Formato de fecha inválido. Por favor usa el formato DD/MM/AAAA\n" +
                    "Ejemplo: 15/12/2025");
        }
    }
    
    private void processColor(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            sendText(chatId, "⚠️ Por favor, envía el color como texto.");
            return;
        }
        
        String color = update.getMessage().getText().trim();
        if (color.isEmpty() || color.startsWith("/")) {
            sendText(chatId, "⚠️ Por favor, ingresa un color válido.");
            return;
        }
        
        conversation.put("color", color);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_TYPE);
        sendText(chatId, "✅ Color registrado: " + color + "\n\n" +
                "Selecciona el *tipo* de mascota:\n" +
                "1️⃣ PERRO\n" +
                "2️⃣ GATO\n" +
                "3️⃣ COBAYA\n" +
                "4️⃣ LORO\n" +
                "5️⃣ CONEJO\n" +
                "6️⃣ CABALLO\n" +
                "7️⃣ TORTUGA\n\n" +
                "Responde con el número (1-7):");
    }
    
    private void processType(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            sendText(chatId, "⚠️ Por favor, envía el tipo como texto.");
            return;
        }
        
        String text = update.getMessage().getText().trim();
        Pet.Type type = null;
        
        switch (text) {
            case "1", "PERRO", "perro" -> type = Pet.Type.PERRO;
            case "2", "GATO", "gato" -> type = Pet.Type.GATO;
            case "3", "COBAYA", "cobaya" -> type = Pet.Type.COBAYA;
            case "4", "LORO", "loro" -> type = Pet.Type.LORO;
            case "5", "CONEJO", "conejo" -> type = Pet.Type.CONEJO;
            case "6", "CABALLO", "caballo" -> type = Pet.Type.CABALLO;
            case "7", "TORTUGA", "tortuga" -> type = Pet.Type.TORTUGA;
            default -> {
                sendText(chatId, "⚠️ Opción no válida. Por favor responde con un número del 1 al 7.");
                return;
            }
        }
        
        conversation.put("type", type);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_RACE);
        sendText(chatId, "✅ Tipo registrado: " + type + "\n\n" +
                "Ingresa la *raza* de la mascota:");
    }
    
    private void processRace(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            sendText(chatId, "⚠️ Por favor, envía la raza como texto.");
            return;
        }
        
        String race = update.getMessage().getText().trim();
        if (race.isEmpty() || race.startsWith("/")) {
            sendText(chatId, "⚠️ Por favor, ingresa una raza válida.");
            return;
        }
        
        conversation.put("race", race);
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_WEIGHT);
        sendText(chatId, "✅ Raza registrada: " + race + "\n\n" +
                "Ingresa el *peso* de la mascota en kilogramos (ejemplo: 5.5):");
    }
    
    private void processWeight(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            sendText(chatId, "⚠️ Por favor, envía el peso como texto.");
            return;
        }
        
        String text = update.getMessage().getText().trim();
        try {
            float weight = Float.parseFloat(text);
            if (weight <= 0) {
                sendText(chatId, "⚠️ El peso debe ser un número positivo.");
                return;
            }
            
            conversation.put("weight", weight);
            conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_PHOTO);
            sendText(chatId, "✅ Peso registrado: " + weight + " kg\n\n" +
                    "Por favor, *envía una foto* de la mascota:");
        } catch (NumberFormatException e) {
            sendText(chatId, "⚠️ Por favor, ingresa un número válido (ejemplo: 5.5)");
        }
    }
    
    private void processPhoto(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasPhoto()) {
            sendText(chatId, "⚠️ Por favor, envía una foto de la mascota.");
            return;
        }
        
        try {
            // Obtener la foto de mayor resolución
            List<PhotoSize> photos = update.getMessage().getPhoto();
            PhotoSize photo = photos.get(photos.size() - 1);
            
            // Obtener el archivo
            GetFile getFile = new GetFile();
            getFile.setFileId(photo.getFileId());
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            
            // Descargar la imagen
            String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + file.getFilePath();
            URL url = new URL(fileUrl);
            
            try (InputStream inputStream = url.openStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                // Convertir a Base64
                byte[] imageBytes = outputStream.toByteArray();
                String photoBase64 = Base64.getEncoder().encodeToString(imageBytes);
                
                conversation.put("photoBase64", photoBase64);
                conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_LOCATION);
                sendText(chatId, "✅ Foto recibida correctamente\n\n" +
                        "Por favor, *envía tu ubicación actual* usando el botón de adjuntar ubicación de Telegram 📍");
            }
        } catch (Exception e) {
            System.err.println("Error procesando foto: " + e.getMessage());
            sendText(chatId, "❌ Hubo un error al procesar la foto. Por favor, inténtalo de nuevo.");
        }
    }
    
    private void processLocation(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasLocation()) {
            sendText(chatId, "⚠️ Por favor, envía tu ubicación usando el botón de adjuntar ubicación 📍");
            return;
        }
        
        Location location = update.getMessage().getLocation();
        conversation.put("latitude", location.getLatitude().floatValue());
        conversation.put("longitude", location.getLongitude().floatValue());
        conversation.setCurrentStep(ConversationState.ConversationStep.WAITING_DESCRIPTION);
        
        sendText(chatId, "✅ Ubicación registrada\n\n" +
                "Por último, ingresa una *descripción adicional* de la mascota\n" +
                "(características especiales, comportamiento, etc.):");
    }
    
    private void processDescription(Update update, long chatId, ConversationState conversation) {
        if (!update.getMessage().hasText()) {
            sendText(chatId, "⚠️ Por favor, envía la descripción como texto.");
            return;
        }
        
        String description = update.getMessage().getText().trim();
        if (description.isEmpty() || description.startsWith("/")) {
            sendText(chatId, "⚠️ Por favor, ingresa una descripción válida.");
            return;
        }
        
        conversation.put("description", description);
        
        // Guardar la mascota
        savePet(chatId, conversation);
    }
    
    private void savePet(long chatId, ConversationState conversation) {
        try {
            Long userId = conversation.getLong("userId");
            User creator = userService.findById(userId);
            
            // Crear el DTO con todos los datos recopilados
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
            
            // Guardar la mascota
            var petResponse = petService.createPet(creator, petCreateDTO);
            
            // Crear mensaje
            String summary = String.format("""
                    ✅ ¡Mascota registrada exitosamente!
                    
                    📋 Resumen:
                    🆔 ID: %d
                    🐾 Nombre: %s
                    👤 Dueño: %s
                    📏 Tamaño: %s
                    📊 Estado: %s
                    📅 Fecha: %s
                    🎨 Color: %s
                    🐕 Tipo: %s
                    🏷️ Raza: %s
                    ⚖️ Peso: %.1f kg
                    📝 Descripción: %s
                    
                    Tu mascota ha sido registrada en el sistema.
                    Puedes verla en la aplicación web de Volve a Casa.
                    
                    Usa /suscribir %d para recibir notificaciones de avistamientos.
                    """,
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
            
            // Enviar con la foto 
            String photoBase64 = conversation.getString("photoBase64");
            if (photoBase64 != null && !photoBase64.isEmpty()) {
                sendPhotoPlainText(chatId, summary, photoBase64);
            } else {
                sendText(chatId, summary);
            }
            
            // Limpiar la conversación
            conversation.reset();
            conversations.remove(chatId);
            
        } catch (Exception e) {
            System.err.println("Error guardando mascota: " + e.getMessage());
            e.printStackTrace();
            sendText(chatId, "❌ Hubo un error al guardar la mascota. Por favor, inténtalo de nuevo más tarde.\n\nError: " + e.getMessage());
            conversation.reset();
            conversations.remove(chatId);
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
    
    public void sendPhotoPlainText(Long chatId, String message, String photoBase64) {
        if (photoBase64 == null || photoBase64.isEmpty()) {
            sendText(chatId, message);
            return;
        }

        try {
            // Decodificar la imagen Base64
            byte[] imageBytes = Base64.getDecoder().decode(photoBase64);
            ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setPhoto(new InputFile(imageStream, "mascota.jpg"));
            sendPhoto.setCaption(message);

            execute(sendPhoto);
        } catch (TelegramApiException e) {
            System.err.println("Error enviando foto por Telegram: " + e.getMessage());
            // Si falla el envío de la foto, enviar solo el mensaje
            sendText(chatId, message);
        } catch (IllegalArgumentException e) {
            System.err.println("Error decodificando imagen Base64: " + e.getMessage());
            sendText(chatId, message);
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
