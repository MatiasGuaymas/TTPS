package io.github.grupo01.volve_a_casa.persistence.entities;

import io.github.grupo01.volve_a_casa.controllers.dto.pet.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.embeddable.Coordinates;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "pets")
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
    @CollectionTable(name = "mascota_fotos", joinColumns = @JoinColumn(name = "mascota_id"))
    // Crea una tabla "mascota_fotos"
    @Column(name = "foto_base64", columnDefinition = "TEXT")
    private List<String> photosBase64 = new ArrayList<>();

    @OneToMany(
            mappedBy = "pet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Sighting> sightings = new ArrayList<>();

    public Pet(String name, String size, String description, String color, String race, float weight, float latitude, float longitude, Pet.Type type, User creator, String fotoDefaultBase64) {
        this.name = name;
        this.size = size;
        this.description = description;
        this.color = color;
        this.race = race;
        this.weight = weight;
        this.type = type;
        this.creator = creator;
        this.lostDate = LocalDate.now();
        this.actualizarUbicacion(latitude, longitude);
        this.addFotoBase64(fotoDefaultBase64);

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

    public void updateFromDTO(PetUpdateDTO dto) {
        if (dto.name() != null) this.name = dto.name();
        if (dto.description() != null) this.description = dto.description();
        if (dto.color() != null) this.color = dto.color();
        if (dto.size() != null) this.size = dto.size();
        if (dto.race() != null) this.race = dto.race();
        if (dto.weight() != null && dto.weight() > 0) this.weight = dto.weight();
        if (dto.type() != null) this.type = dto.type();
        if (dto.state() != null) this.state = dto.state();
        if (dto.latitude() != null && dto.longitude() != null)
            this.actualizarUbicacion(dto.latitude(), dto.longitude());
    }

    /**
     * Devuelve los avistamientos de la mascota.
     *
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
