package io.github.grupo01.volve_a_casa.persistence.entities;

import io.github.grupo01.volve_a_casa.persistence.entities.embeddable.Coordinates;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Entity
@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Table(name="avistamientos")
public class Sighting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Coordinates coordinates;

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

    private Sighting(Builder builder) {
        this.pet = builder.pet;
        this.coordinates = new Coordinates(builder.latitud, builder.longitud);
        this.photoBase64 = builder.fotoBase64;
        this.comment = builder.comentario;
        this.date = builder.fecha;
        this.reporter = builder.reportador;
    }

    public void actualizarUbicacion(float latitud, float longitud) {
        this.coordinates = new Coordinates(latitud, longitud);
    }

    public static Builder builder() {
        return new Builder();
    }

    // TODO: Borrar
    public static class Builder {
        private Pet pet;
        private User reportador;
        private Float latitud;
        private Float longitud;
        private String fotoBase64;
        private LocalDate fecha;

        private String comentario = "";

        private Builder() {}

        public Builder mascota(Pet pet) {
            this.pet = pet;
            return this;
        }

        public Builder reportador(User reportador) {
            this.reportador = reportador;
            return this;
        }

        public Builder latitud(Float latitud) {
            this.latitud = latitud;
            return this;
        }

        public Builder longitud(Float longitud) {
            this.longitud = longitud;
            return this;
        }

        public Builder fotoBase64(String fotoBase64) {
            this.fotoBase64 = fotoBase64;
            return this;
        }

        public Builder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder comentario(String comentario) {
            this.comentario = comentario;
            return this;
        }

        public Sighting build() {
            Objects.requireNonNull(pet,    "La mascota es obligatoria");
            Objects.requireNonNull(reportador, "El reportador es obligatorio");
            Objects.requireNonNull(latitud,    "La latitud es obligatoria");
            Objects.requireNonNull(longitud,   "La longitud es obligatoria");
            Objects.requireNonNull(fotoBase64, "La foto es obligatoria");
            Objects.requireNonNull(fecha,      "La fecha es obligatoria");

            Sighting sighting = new Sighting(this);
            reportador.addAvistamiento(sighting);
            pet.addAvistamiento(sighting);
            return sighting;
        }
    }
}
