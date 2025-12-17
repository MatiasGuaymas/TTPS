package io.github.grupo01.volve_a_casa.controllers.dto.pet;

import java.time.LocalDate;
import java.util.List;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;

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
        List<String> allPhotos = pet.getPhotosBase64();
        
        List<String> limitedPhotos = (allPhotos != null && !allPhotos.isEmpty()) 
                ? List.of(allPhotos.get(0)) 
                : List.of();

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
                limitedPhotos 
        );
    }
}
