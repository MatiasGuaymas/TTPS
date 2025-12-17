package io.github.grupo01.volve_a_casa.controllers.dto.sighting;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record SightingCreateDTO(

        @NotNull(message = "Pet ID is required")
        Long petId,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        Float latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        Float longitude,

        // FIXME: Descomentar cuando se implemente la carga de imagenes
        // @NotBlank(message = "Photo is required")
        String photoBase64,

        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date,

        @Size(max = 200, message = "Comment must have at most 200 characters")
        String comment
) {
}
