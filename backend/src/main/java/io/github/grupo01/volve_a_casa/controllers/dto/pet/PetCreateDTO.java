package io.github.grupo01.volve_a_casa.controllers.dto.pet;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PetCreateDTO(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Size is required")
        String size,

        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description must have at most 500 characters")
        String description,

        @NotBlank(message = "Color is required")
        String color,

        @NotBlank(message = "Race is required")
        String race,

        @Positive(message = "Weight must be greater than 0")
        float weight,

        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        float latitude,

        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        float longitude,

        @NotNull(message = "Type is required")
        Pet.Type type,

        //agrego esto para poder subir una foto de la mascota
        @NotBlank(message = "Photo Base64 is required")
        String photoBase64
) {
}

