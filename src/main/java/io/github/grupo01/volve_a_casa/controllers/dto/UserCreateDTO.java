package io.github.grupo01.volve_a_casa.controllers.dto;

public record UserCreateDTO(
        String email,
        String password,
        String name,
        String lastName,
        String phoneNumber,
        String city,
        String neighborhood,
        float latitude,
        float longitude
) {
    public boolean isValid() {
        return email != null &&
                password != null &&
                name != null &&
                lastName != null &&
                phoneNumber != null &&
                city != null &&
                neighborhood != null;
    }
}
