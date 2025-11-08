package io.github.grupo01.volve_a_casa.controllers.dto;

public record UserUpdateDTO(
        String name,
        String lastName,
        String phoneNumber,
        String city,
        String neighborhood,
        float latitude,
        float longitude
) {

}
