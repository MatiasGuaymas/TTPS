package io.github.grupo01.volve_a_casa.controllers.dto.pet;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;

import java.time.LocalDate;

/**
 * DTO con información detallada de una mascota para el bot de Telegram
 * Incluye la primera foto para enviarla por Telegram
 */
public record PetDetailDTO(
        Long id,
        String name,
        Pet.Size size,
        String description,
        String color,
        String race,
        Float weight,
        Float latitude,
        Float longitude,
        LocalDate lostDate,
        Pet.State state,
        Pet.Type type,
        Long creatorId,
        String photoBase64  // Primera foto de la mascota para Telegram
) {
    public static PetDetailDTO fromPet(Pet pet) {
        // Obtener la primera foto si existe
        String firstPhoto = null;
        if (pet.getPhotosBase64() != null && !pet.getPhotosBase64().isEmpty()) {
            firstPhoto = pet.getPhotosBase64().get(0);
        }
        
        return new PetDetailDTO(
                pet.getId(),
                pet.getName(),
                pet.getSize(),
                pet.getDescription(),
                pet.getColor(),
                pet.getRace(),
                pet.getWeight(),
                pet.getCoordinates().getLatitude(),
                pet.getCoordinates().getLongitude(),
                pet.getLostDate(),
                pet.getState(),
                pet.getType(),
                pet.getCreator().getId(),
                firstPhoto
        );
    }

    /**
     * Formatea la información detallada de la mascota para mostrarse en Telegram
     */
    public String toTelegramFormat() {
        StringBuilder message = new StringBuilder();
        message.append("🐾 *Información Detallada de ").append(name).append("*\n\n");
        message.append("📋 *Datos Básicos:*\n");
        message.append("• ID: ").append(id).append("\n");
        message.append("• Tipo: ").append(formatType(type)).append("\n");
        message.append("• Tamaño: ").append(formatSize(size)).append("\n");
        message.append("• Color: ").append(color).append("\n");
        
        if (race != null && !race.isEmpty()) {
            message.append("• Raza: ").append(race).append("\n");
        }
        
        if (weight != null) {
            message.append("• Peso: ").append(String.format("%.1f kg", weight)).append("\n");
        }
        
        message.append("\n📍 *Ubicación:*\n");
        message.append("• Latitud: ").append(String.format("%.6f", latitude)).append("\n");
        message.append("• Longitud: ").append(String.format("%.6f", longitude)).append("\n");
        
        message.append("\n📅 *Estado:*\n");
        message.append("• Estado actual: ").append(formatState(state)).append("\n");
        
        if (lostDate != null) {
            message.append("• Perdido desde: ").append(lostDate).append("\n");
        }
        
        if (description != null && !description.isEmpty()) {
            message.append("\n📝 *Descripción:*\n");
            message.append(description).append("\n");
        }
        
        message.append("\n👤 *Contacto:*\n");
        message.append("• ID del dueño: ").append(creatorId).append("\n");
        
        return message.toString();
    }

    private String formatType(Pet.Type type) {
        return switch (type) {
            case PERRO -> "🐕 Perro";
            case GATO -> "🐈 Gato";
            case COBAYA -> "🐹 Cobaya";
            case LORO -> "🦜 Loro";
            case CONEJO -> "🐇 Conejo";
            case CABALLO -> "🐴 Caballo";
            case TORTUGA -> "🐢 Tortuga";
        };
    }

    private String formatSize(Pet.Size size) {
        return switch (size) {
            case PEQUENO -> "Pequeño";
            case MEDIANO -> "Mediano";
            case GRANDE -> "Grande";
        };
    }

    private String formatState(Pet.State state) {
        return switch (state) {
            case PERDIDO_PROPIO -> "❌ Perdido (propio)";
            case PERDIDO_AJENO -> "❌ Perdido (ajeno)";
            case RECUPERADO -> "✅ Recuperado";
            case ADOPTADO -> "🏠 Adoptado";
        };
    }
}
