package io.github.grupo01.volve_a_casa.controllers.dto;

import java.time.LocalDate;

public record SightingCreateDTO (
        Long petId,
        Float latitude,
        Float longitude,
        String photoBase64,
        LocalDate date,
        String comment
) {
    public boolean isValid() {
        return petId != null &&
                latitude != null &&
                longitude != null &&
                photoBase64 != null && !photoBase64.isEmpty() &&
                date != null;
    }
}