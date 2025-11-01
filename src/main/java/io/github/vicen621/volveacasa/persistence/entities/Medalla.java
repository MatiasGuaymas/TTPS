package io.github.vicen621.volveacasa.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Entity
@Component
@Data
@Table(name="medallas")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Medalla {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombre;

    private String descripcion;

    private String iconoBase64;

    public Medalla(String nombre, String descripcion, String iconoBase64) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.iconoBase64 = iconoBase64;
    }
}
