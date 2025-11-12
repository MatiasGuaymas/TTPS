package io.github.grupo01.volve_a_casa.persistence.entities;

import io.github.grupo01.volve_a_casa.persistence.entities.embeddable.Coordinates;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="pets")
@Component
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String size;

    private String description;

    private String color;

    private String race;

    private float weight;

    @Embedded
    @Setter(AccessLevel.NONE)
    private Coordinates coordinates;

    private LocalDate lostDate;

    @Enumerated(EnumType.STRING)
    private State state;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "creador_id")
    private User creator;

    @ElementCollection
    @Setter(AccessLevel.NONE)
    @CollectionTable(name="mascota_fotos", joinColumns=@JoinColumn(name="mascota_id")) // Crea una tabla "mascota_fotos"
    @Column(name="foto_base64", columnDefinition = "TEXT")
    private List<String> photosBase64 = new ArrayList<>();

    @OneToMany(
            mappedBy = "pet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Sighting> sightings = new ArrayList<>();

    private Pet(Builder builder) {
        this.name = builder.nombre;
        this.size = builder.tamano;
        this.color = builder.color;
        this.race = builder.raza;
        this.description = builder.descripcion;
        this.weight = builder.peso;
        this.coordinates = new Coordinates(builder.latitud, builder.longitud);
        this.lostDate = builder.fechaPerdida;
        this.state = builder.state;
        this.type = builder.type;
        this.photosBase64 = builder.fotosBase64;
        this.creator = builder.creador;
    }

    public void actualizarUbicacion(float latitud, float longitud) {
        this.coordinates = new Coordinates(latitud, longitud);
    }

    public List<String> getPhotosBase64() {
        return Collections.unmodifiableList(this.photosBase64);
    }

    public void addFotoBase64(String fotoBase64) {
        if (fotoBase64 != null && !this.photosBase64.contains(fotoBase64))
            this.photosBase64.add(fotoBase64);
    }

    public void removeFotoBase64(String fotoBase64) {
        if (fotoBase64 != null)
            this.photosBase64.remove(fotoBase64);
    }

    /**
     * Devuelve los avistamientos de la mascota.
     * @return Lista de avistamientos inmodificable.
     */
    public List<Sighting> getSightings() {
        return Collections.unmodifiableList(this.sightings);
    }

    public void addAvistamiento(Sighting sighting) {
        if (sighting != null && !this.sightings.contains(sighting))
            this.sightings.add(sighting);
    }

    public void removeAvistamiento(Sighting sighting) {
        if (sighting != null) {
            this.sightings.remove(sighting);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // TODO: Borrar
    public static class Builder {
        private String nombre;
        private String tamano;
        private String descripcion;
        private String color;
        private String raza;
        private Float peso;
        private Float latitud;
        private Float longitud;
        private LocalDate fechaPerdida;
        private State state;
        private Type type;
        private User creador;
        private List<String> fotosBase64 = new ArrayList<>();

        private Builder() {}

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder tamano(String tamano) {
            this.tamano = tamano;
            return this;
        }

        public Builder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder raza(String raza) {
            this.raza = raza;
            return this;
        }

        public Builder peso(Float peso) {
            this.peso = peso;
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

        public Builder fechaPerdida(LocalDate fechaPerdida) {
            this.fechaPerdida = fechaPerdida;
            return this;
        }

        public Builder estado(State state) {
            this.state = state;
            return this;
        }

        public Builder tipo(Type type) {
            this.type = type;
            return this;
        }

        public Builder agregarFoto(String fotoBase64) {
            if (this.fotosBase64 != null && !this.fotosBase64.contains(fotoBase64) && !fotoBase64.isEmpty()) {
                this.fotosBase64.add(fotoBase64);
            }
            return this;
        }

        public Builder creador(User creador) {
            this.creador = creador;
            return this;
        }

        public Pet build() {
            Objects.requireNonNull(nombre,       "El nombre es obligatorio");
            Objects.requireNonNull(tamano,       "El tamano es obligatorio");
            Objects.requireNonNull(descripcion,  "La descripcion es obligatoria");
            Objects.requireNonNull(color,        "El color es obligatorio");
            Objects.requireNonNull(raza,         "La raza es obligatoria");
            Objects.requireNonNull(peso,         "El peso es obligatorio");
            Objects.requireNonNull(latitud,      "La latitud es obligatoria");
            Objects.requireNonNull(longitud,     "La longitud es obligatoria");
            Objects.requireNonNull(fechaPerdida, "La fecha de perdida es obligatoria");
            Objects.requireNonNull(state,       "El estado es obligatorio");
            Objects.requireNonNull(type,         "El tipo es obligatorio");
            Objects.requireNonNull(creador,      "El creador es obligatorio");
            if (fotosBase64.isEmpty())
                throw new IllegalArgumentException("Debe agregar al menos una foto");

            return new Pet(this);
        }
    }

    public enum State {
        PERDIDO_PROPIO,
        PERDIDO_AJENO,
        RECUPERADO,
        ADOPTADO
    }

    public enum Type {
        PERRO,
        GATO,
        COBAYA,
        LORO,
        CONEJO,
        CABALLO,
        TORTUGA,
    }
}
