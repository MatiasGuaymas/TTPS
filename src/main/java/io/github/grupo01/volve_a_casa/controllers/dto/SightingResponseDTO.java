package io.github.grupo01.volve_a_casa.controllers.dto;

import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;

import java.time.LocalDate;

public record SightingResponseDTO(
        Long id,
        Long petId,
        String petName,
        Long reporterId,
        String reporterName,
        Float latitude,
        Float longitude,
        String photoBase64,
        LocalDate date,
        String comment
){
    public static SightingResponseDTO fromEntity(Sighting sighting) {
        return new SightingResponseDTO(
                sighting.getId(),
                sighting.getPet().getId(),
                sighting.getPet().getName(),
                sighting.getReporter().getId(),
                sighting.getReporter().getName(),
                sighting.getCoordinates().getLatitude(),
                sighting.getCoordinates().getLongitude(),
                sighting.getPhotoBase64(),
                sighting.getDate(),
                sighting.getComment()
        );
    }
}
