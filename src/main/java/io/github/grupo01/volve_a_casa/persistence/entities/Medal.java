package io.github.grupo01.volve_a_casa.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Component
@Data
@Table(name="medals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Medal {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    private String iconBase64;

    public Medal(String name, String description, String iconBase64) {
        this.name = name;
        this.description = description;
        this.iconBase64 = iconBase64;
    }
}
