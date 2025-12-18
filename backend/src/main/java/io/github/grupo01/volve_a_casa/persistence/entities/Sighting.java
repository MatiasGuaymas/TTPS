package io.github.grupo01.volve_a_casa.persistence.entities;

import io.github.grupo01.volve_a_casa.persistence.entities.embeddable.Coordinates;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Getter
@Entity
@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Table(name = "avistamientos")
public class Sighting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Coordinates coordinates;

    @Column(columnDefinition = "TEXT")
    private String photoBase64;

    @Setter
    private LocalDate date;

    @Setter
    private String comment;

    @ManyToOne
    @JoinColumn(name = "reportador_id")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "mascota_id")
    private Pet pet;

    public Sighting(User user, Pet pet, float latitude, float longitude, String photoBase64, String comment, LocalDate date) {
        this.reporter = user;
        this.pet = pet;
        this.photoBase64 = photoBase64;
        this.comment = comment;
        this.date = date;
        this.actualizarUbicacion(latitude, longitude);
    }

    public void actualizarUbicacion(float latitud, float longitud) {
        this.coordinates = new Coordinates(latitud, longitud);
    }
}