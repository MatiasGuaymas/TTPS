package io.github.vicen621.volveacasa.persistence.entities;

import io.github.vicen621.volveacasa.persistence.entities.embeddable.Coordenadas;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String apellidos;

    @Column(unique = true)
    private String email;

    private String contrasena;

    private String telefono;

    private String ciudad;

    private String barrio;

    private int puntos;

    private boolean habilitado;

    @Embedded
    private Coordenadas coordenadas;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    // Como no necesito que cree la medalla (ya está creada) no se agrega CascadeType.PERSIST
    // Tampoco necesito que borre la medalla (no pertenece a un unico usuario) no se agrega CascadeType.REMOVE
    @ManyToMany
    @Cascade({
            org.hibernate.annotations.CascadeType.MERGE,
            org.hibernate.annotations.CascadeType.REFRESH
    })
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
    private List<Medalla> medallas = new ArrayList<>();

    @OneToMany(
            mappedBy = "creador",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Mascota> mascotasCreadas = new ArrayList<>();

    @OneToMany(
            mappedBy = "reportador",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Avistamiento> avistamientos = new ArrayList<>();

    protected Usuario() {}

    // Constructor privado para el builder
    private Usuario(Builder builder) {
        this.nombre = builder.nombre;
        this.apellidos = builder.apellidos;
        this.email = builder.email;
        this.contrasena = builder.contrasena;
        this.telefono = builder.telefono;
        this.ciudad = builder.ciudad;
        this.barrio = builder.barrio;
        this.coordenadas = new Coordenadas(builder.latitud, builder.longitud);
        this.puntos = builder.puntos;
        this.habilitado = builder.habilitado;
        this.rol = builder.rol;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public void actualizarUbicacion(float latitud, float longitud) {
        this.coordenadas = new Coordenadas(latitud, longitud);
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Medalla> getMedallas() {
        return Collections.unmodifiableList(this.medallas);
    }

    public void addMedalla(Medalla medalla) {
        if (medalla != null && !this.medallas.contains(medalla))
            this.medallas.add(medalla);
    }

    public void removeMedalla(Medalla medalla) {
        if (medalla != null)
            this.medallas.remove(medalla);
    }

    public List<Mascota> getMascotasCreadas() {
        return Collections.unmodifiableList(this.mascotasCreadas);
    }

    protected void addMascotaCreada(Mascota mascota) {
        if (mascota != null && !this.mascotasCreadas.contains(mascota))
            this.mascotasCreadas.add(mascota);
    }

    public void removeMascotaCreada(Mascota mascota) {
        if (mascota != null)
            this.mascotasCreadas.remove(mascota);
    }

    public List<Avistamiento> getAvistamientos() {
        return Collections.unmodifiableList(this.avistamientos);
    }

    protected void addAvistamiento(Avistamiento avistamiento) {
        if (avistamiento != null && !this.avistamientos.contains(avistamiento))
            this.avistamientos.add(avistamiento);
    }

    public void removeAvistamiento(Avistamiento avistamiento) {
        if (avistamiento != null) {
            this.avistamientos.remove(avistamiento);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(getId(), usuario.getId()) && Objects.equals(getEmail(), usuario.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail());
    }

    public static Builder builder() {
        return new Builder();
    }

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
        private Rol rol = Rol.USUARIO;

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

        public Builder rol(Rol rol) {
            this.rol = rol;
            return this;
        }

        public Usuario build() {
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

            return new Usuario(this);
        }
    }

    public enum Rol {
        USUARIO,
        ADMIN,
    }
}
