package io.github.grupo01.volve_a_casa.controllers.dto;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.embeddable.Coordinates;

public record PetCreateDTO(
        String name,
        String size,
        String description,
        String color,
        String race,
        float weight,
        float latitude,
        float longitude,
        Pet.Type type
) {
    public boolean isValid() {
        return name != null &&
                size != null &&
                description != null &&
                color != null &&
                race != null &&
                weight > 0 &&
                type != null;
    }

    public Coordinates toCoordinates() {
        return new Coordinates(latitude, longitude);
    }
}
