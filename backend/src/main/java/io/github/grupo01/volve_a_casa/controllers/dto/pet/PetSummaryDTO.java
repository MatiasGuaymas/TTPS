package io.github.grupo01.volve_a_casa.controllers.dto.pet;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;

import java.time.LocalDate;

/**
 * DTO con información resumida de una mascota para el listado del bot de Telegram
 */
public record PetSummaryDTO(
        Long id,
        String name,
        Pet.Type type,
        Pet.Size size,
        Pet.State state,
        String color,
        LocalDate lostDate
) {
    public static PetSummaryDTO fromPet(Pet pet) {
        return new PetSummaryDTO(
                pet.getId(),
                pet.getName(),
                pet.getType(),
                pet.getSize(),
                pet.getState(),
                pet.getColor(),
                pet.getLostDate()
        );
    }

    /**
     * Formatea la información de la mascota para mostrarse en Telegram
     */
    public String toTelegramFormat() {
        return String.format("""
                🐾 *%s* (ID: %d)
                • Tipo: %s
                • Tamaño: %s
                • Estado: %s
                • Color: %s
                • Perdido desde: %s
                """,
                name,
                id,
                formatType(type),
                formatSize(size),
                formatState(state),
                color,
                lostDate != null ? lostDate.toString() : "No especificado"
        );
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
