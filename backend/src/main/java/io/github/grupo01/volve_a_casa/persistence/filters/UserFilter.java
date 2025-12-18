package io.github.grupo01.volve_a_casa.persistence.filters;

import io.github.grupo01.volve_a_casa.persistence.entities.User;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class UserFilter {
    private final String email;
    private final String name;
    private final String lastName;
    private final String city;
    private final String neighborhood;
    private final Integer minPoints;
    private final Integer maxPoints;
    private final User.Role role;

}
