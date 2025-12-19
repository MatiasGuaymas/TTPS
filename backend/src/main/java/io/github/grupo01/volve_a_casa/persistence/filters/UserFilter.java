package io.github.grupo01.volve_a_casa.persistence.filters;

import io.github.grupo01.volve_a_casa.persistence.entities.User;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilter {
    private String email;
    private String name;
    private String lastName;
    private String city;
    private String neighborhood;
    private Integer minPoints;
    private Integer maxPoints;
    private User.Role role;

}
