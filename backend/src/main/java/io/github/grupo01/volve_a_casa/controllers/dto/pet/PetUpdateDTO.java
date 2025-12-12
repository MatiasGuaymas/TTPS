package io.github.grupo01.volve_a_casa.controllers.dto.pet;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record PetUpdateDTO(

        @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
        String name,

        @Size(max = 500, message = "Description must have at most 500 characters")
        String description,

        @Size(max = 50, message = "Color must have at most 50 characters")
        String color,

        @Size(max = 50, message = "Size must have at most 50 characters")
        Pet.Size size,

        @Size(max = 100, message = "Race must have at most 100 characters")
        String race,

        @DecimalMin(value = "0.0", message = "Weight must be >= 0")
        Float weight,

        Pet.Type type,
        Pet.State state,

        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        Float latitude,

        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        Float longitude
) {
}
