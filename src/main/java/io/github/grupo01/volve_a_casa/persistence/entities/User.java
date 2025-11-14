package io.github.grupo01.volve_a_casa.persistence.entities;

import io.github.grupo01.volve_a_casa.controllers.dto.user.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.embeddable.Coordinates;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "usuarios")
@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//TODO: Cambiar todos los new BCryptPasswordEncoder() por un bean singleton
public class User {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;

    private String phone;

    private String city;

    private String neighborhood;

    private int points;

    private boolean enabled;

    @Embedded
    @Setter(AccessLevel.NONE)
    private Coordinates coordinates;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Como no necesito que cree la medalla (ya está creada) no se agrega CascadeType.PERSIST
    // Tampoco necesito que borre la medalla (no pertenece a un unico usuario) no se agrega CascadeType.REMOVE
    @Setter(AccessLevel.NONE)
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinTable(
            name = "usuario_medallas",
            joinColumns = @JoinColumn(
                    name = "usuario_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "medalla_id",
                    referencedColumnName = "id"
            )
    )
    private List<Medal> medals = new ArrayList<>();

    @OneToMany(
            mappedBy = "creator",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Setter(AccessLevel.NONE)
    private List<Pet> createdPets = new ArrayList<>();

    @OneToMany(
            mappedBy = "reporter",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Setter(AccessLevel.NONE)
    private List<Sighting> sightings = new ArrayList<>();

    public User(String name, String lastName, String email, String password, String phone, String city, String neighborhood, Float latitude, Float longitude) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.city = city;
        this.neighborhood = neighborhood;
        this.actualizarUbicacion(latitude, longitude);
        this.points = 0;
        this.enabled = true;
        this.role = Role.USER;
    }

    public void actualizarUbicacion(Float latitud, Float longitud) {
        this.coordinates = new Coordinates(latitud, longitud);
    }

    public List<Medal> getMedals() {
        return Collections.unmodifiableList(this.medals);
    }

    public void addMedalla(Medal medal) {
        if (medal != null && !this.medals.contains(medal))
            this.medals.add(medal);
    }

    public void removeMedalla(Medal medal) {
        if (medal != null)
            this.medals.remove(medal);
    }

    public List<Pet> getCreatedPets() {
        return Collections.unmodifiableList(this.createdPets);
    }

    protected void addPetCreada(Pet pet) {
        if (pet != null && !this.createdPets.contains(pet))
            this.createdPets.add(pet);
    }

    public void removePetCreada(Pet pet) {
        if (pet != null)
            this.createdPets.remove(pet);
    }

    public List<Sighting> getSightings() {
        return Collections.unmodifiableList(this.sightings);
    }

    protected void addAvistamiento(Sighting sighting) {
        if (sighting != null && !this.sightings.contains(sighting))
            this.sightings.add(sighting);
    }

    public void removeAvistamiento(Sighting sighting) {
        if (sighting != null) {
            this.sightings.remove(sighting);
        }
    }

    public void updateFromDTO(UserUpdateDTO dto) {
        if (dto.name() != null) this.name = dto.name();
        if (dto.lastName() != null) this.lastName = dto.lastName();
        if (dto.phoneNumber() != null) this.phone = dto.phoneNumber();
        if (dto.city() != null) this.city = dto.city();
        if (dto.neighborhood() != null) this.neighborhood = dto.neighborhood();
        if (dto.latitude() != null && dto.longitude() != null)
            this.actualizarUbicacion(dto.latitude(), dto.longitude());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getEmail(), user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail());
    }

    public enum Role {
        USER,
        ADMIN,
    }
}
