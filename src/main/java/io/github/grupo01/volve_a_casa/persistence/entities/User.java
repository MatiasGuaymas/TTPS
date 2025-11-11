package io.github.grupo01.volve_a_casa.persistence.entities;

import io.github.grupo01.volve_a_casa.controllers.dto.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.embeddable.Coordinates;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    // Constructor privado para el builder
    private User(Builder builder) {
        this.name = builder.nombre;
        this.lastName = builder.apellidos;
        this.email = builder.email;
        this.password = new BCryptPasswordEncoder().encode(builder.contrasena);
        this.phone = builder.telefono;
        this.city = builder.ciudad;
        this.neighborhood = builder.barrio;
        this.coordinates = new Coordinates(builder.latitud, builder.longitud);
        this.points = builder.puntos;
        this.enabled = builder.habilitado;
        this.role = builder.role;
    }

    public void actualizarUbicacion(float latitud, float longitud) {
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

    public boolean checkPassword(String password) {
        return this.password.equals(new BCryptPasswordEncoder().encode(password));
    }

    public void updateFromDTO(UserUpdateDTO dto) {
        if (dto.name() != null) this.name = dto.name();
        if (dto.lastName() != null) this.lastName = dto.lastName();
        if (dto.phoneNumber() != null) this.phone = dto.phoneNumber();
        if (dto.city() != null) this.city = dto.city();
        if (dto.neighborhood() != null) this.neighborhood = dto.neighborhood();
        if (dto.latitude() != 0 && dto.longitude() != 0) this.actualizarUbicacion(dto.latitude(), dto.longitude());
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

    public static Builder builder() {
        return new Builder();
    }

    // TODO: Borrar
    public static class Builder {
        private String nombre;
        private String apellidos;
        private String email;
        private String contrasena;
        private String telefono;
        private String ciudad;
        private String barrio;
        private Float latitud;
        private Float longitud;
        private int puntos = 0;
        private boolean habilitado = true;
        private Role role = Role.USER;

        private Builder() {}

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder apellidos(String apellidos) {
            this.apellidos = apellidos;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder contrasena(String contrasena) {
            this.contrasena = contrasena;
            return this;
        }

        public Builder telefono(String telefono) {
            this.telefono = telefono;
            return this;
        }

        public Builder ciudad(String ciudad) {
            this.ciudad = ciudad;
            return this;
        }

        public Builder barrio(String barrio) {
            this.barrio = barrio;
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

        public Builder puntos(int puntos) {
            this.puntos = puntos;
            return this;
        }

        public Builder habilitado(boolean habilitado) {
            this.habilitado = habilitado;
            return this;
        }

        public Builder rol(Role role) {
            this.role = role;
            return this;
        }

        public User build() {
            // Valido que los campos obligatorios no sean null
            Objects.requireNonNull(nombre,     "El nombre es obligatorio");
            Objects.requireNonNull(apellidos,  "Los apellidos son obligatorios");
            Objects.requireNonNull(email,      "El email es obligatorio");
            Objects.requireNonNull(contrasena, "La contraseña es obligatoria");
            Objects.requireNonNull(telefono,   "El teléfono es obligatorio");
            Objects.requireNonNull(ciudad,     "La ciudad es obligatoria");
            Objects.requireNonNull(barrio,     "El barrio es obligatorio");
            Objects.requireNonNull(latitud,    "La latitud es obligatoria");
            Objects.requireNonNull(longitud,   "La longitud es obligatoria");

            return new User(this);
        }
    }

    public enum Role {
        USER,
        ADMIN,
    }
}
