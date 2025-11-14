package io.github.grupo01.volve_a_casa.controllers.dto.sighting;

import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;

import java.time.LocalDate;

public record SightingResponseDTO(
        Long id,
        Long petId,
        Long reporterId,
        Float latitude,
        Float longitude,
        String photoBase64,
        LocalDate date,
        String comment
) {
    public static SightingResponseDTO fromSighting(Sighting sighting) {
        return new SightingResponseDTO(
                sighting.getId(),
                sighting.getPet().getId(),
                sighting.getReporter().getId(),
                sighting.getCoordinates().getLatitude(),
                sighting.getCoordinates().getLongitude(),
                sighting.getPhotoBase64(),
                sighting.getDate(),
                sighting.getComment()
        );
    }
}
