package io.github.grupo01.volve_a_casa.controllers.dto.auth;

import io.github.grupo01.volve_a_casa.persistence.entities.User;

public record UserAuthDTO(
        Long id,
        String name,
        String email,
        User.Role role
) { }
