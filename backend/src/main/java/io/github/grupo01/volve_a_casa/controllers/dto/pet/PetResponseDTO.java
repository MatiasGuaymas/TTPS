package io.github.grupo01.volve_a_casa.controllers.dto.pet;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;

import java.time.LocalDate;
import java.util.List;

public record PetResponseDTO(
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
        List<String> photosBase64
) {
    public static PetResponseDTO fromPet(Pet pet) {
        return new PetResponseDTO(
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
                pet.getPhotosBase64()
        );
    }
}
