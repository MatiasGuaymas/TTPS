package io.github.grupo01.volve_a_casa.controllers.dto.user;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetResponseDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.User;

import java.util.List;

public record UserPublicProfileDTO(
        Long id,
        String name,
        String lastName,
        String phone,
        String city,
        String neighborhood,
        int points,
        List<PetResponseDTO> pets
) {
    public static UserPublicProfileDTO fromUser(User user) {
        return new UserPublicProfileDTO(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getPhone(),
                user.getCity(),
                user.getNeighborhood(),
                user.getPoints(),
                user.getCreatedPets().stream()
                        .map(PetResponseDTO::fromPet)
                        .toList()
        );
    }
}
