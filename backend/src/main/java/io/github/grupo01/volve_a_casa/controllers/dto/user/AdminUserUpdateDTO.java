package io.github.grupo01.volve_a_casa.controllers.dto.user;

import io.github.grupo01.volve_a_casa.persistence.entities.User;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AdminUserUpdateDTO(
        String name,

        String lastName,

        @Pattern(
                regexp = "^[0-9+ -]{6,20}$",
                message = "Phone number is not valid"
        )
        String phoneNumber,

        String city,

        String neighborhood,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        Float latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        Float longitude,

        User.Role role
) {
}
