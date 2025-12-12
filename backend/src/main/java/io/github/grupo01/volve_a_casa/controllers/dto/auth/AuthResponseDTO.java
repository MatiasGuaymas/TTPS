package io.github.grupo01.volve_a_casa.controllers.dto.auth;

public record AuthResponseDTO(
        String token,
        UserAuthDTO user
) {
}
