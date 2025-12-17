package io.github.grupo01.volve_a_casa.controllers.dto.user;

import io.github.grupo01.volve_a_casa.persistence.entities.User;

public record UserResponseDTO(
        Long id,
        String name,
        String lastName,
        String email,
        String phone,
        String city,
        String neighborhood,
        Float latitude,
        Float longitude,
        Integer points,
        Boolean enabled,
        User.Role role
) {
    public static UserResponseDTO fromUser(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getCity(),
                user.getNeighborhood(),
                user.getCoordinates().getLatitude(),
                user.getCoordinates().getLongitude(),
                user.getPoints(),
                user.isEnabled(),
                user.getRole()
        );
    }
}
