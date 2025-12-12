package io.github.grupo01.volve_a_casa.persistence.filters;

import io.github.grupo01.volve_a_casa.persistence.entities.User;

public record UserFilter(
        String email,
        String name,
        String lastName,
        String city,
        String neighborhood,
        int minPoints,
        int maxPoints,
        User.Role role
) {

}
