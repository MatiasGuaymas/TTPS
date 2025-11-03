package io.github.vicen621.volveacasa.persistence.entities;

import io.github.vicen621.volveacasa.persistence.entities.embeddable.Coordenadas;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="mascotas")
@Component
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mascota {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String tamano;

    private String descripcion;

    private String color;

    private String raza;

    private float peso;

    @Embedded
    @Setter(AccessLevel.NONE)
    private Coordenadas coordenadas;

    private LocalDate fechaPerdida;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "creador_id")
    private Usuario creador;

    @ElementCollection
    @Setter(AccessLevel.NONE)
    @CollectionTable(name="mascota_fotos", joinColumns=@JoinColumn(name="mascota_id")) // Crea una tabla "mascota_fotos"
    @Column(name="foto_base64", columnDefinition = "TEXT")
    private List<String> fotosBase64 = new ArrayList<>();

    @OneToMany(
            mappedBy = "mascota",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Avistamiento> avistamientos = new ArrayList<>();

    private Mascota(Builder builder) {
        this.nombre = builder.nombre;
        this.tamano = builder.tamano;
        this.color = builder.color;
        this.raza = builder.raza;
        this.descripcion = builder.descripcion;
        this.peso = builder.peso;
        this.coordenadas = new Coordenadas(builder.latitud, builder.longitud);
        this.fechaPerdida = builder.fechaPerdida;
        this.estado = builder.estado;
        this.tipo = builder.tipo;
        this.fotosBase64 = builder.fotosBase64;
        this.creador = builder.creador;
    }

    public void actualizarUbicacion(float latitud, float longitud) {
        this.coordenadas = new Coordenadas(latitud, longitud);
    }

    public List<String> getFotosBase64() {
        return Collections.unmodifiableList(this.fotosBase64);
    }

    public void addFotoBase64(String fotoBase64) {
        if (fotoBase64 != null && !this.fotosBase64.contains(fotoBase64))
            this.fotosBase64.add(fotoBase64);
    }

    public void removeFotoBase64(String fotoBase64) {
        if (fotoBase64 != null)
            this.fotosBase64.remove(fotoBase64);
    }

    /**
     * Devuelve los avistamientos de la mascota.
     * @return Lista de avistamientos inmodificable.
     */
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
        private Estado estado;
        private Tipo tipo;
        private Usuario creador;
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

        public Builder estado(Estado estado) {
            this.estado = estado;
            return this;
        }

        public Builder tipo(Tipo tipo) {
            this.tipo = tipo;
            return this;
        }

        public Builder agregarFoto(String fotoBase64) {
            if (this.fotosBase64 != null && !this.fotosBase64.contains(fotoBase64) && !fotoBase64.isEmpty()) {
                this.fotosBase64.add(fotoBase64);
            }
            return this;
        }

        public Builder creador(Usuario creador) {
            this.creador = creador;
            return this;
        }

        public Mascota build() {
            Objects.requireNonNull(nombre,       "El nombre es obligatorio");
            Objects.requireNonNull(tamano,       "El tamano es obligatorio");
            Objects.requireNonNull(descripcion,  "La descripcion es obligatoria");
            Objects.requireNonNull(color,        "El color es obligatorio");
            Objects.requireNonNull(raza,         "La raza es obligatoria");
            Objects.requireNonNull(peso,         "El peso es obligatorio");
            Objects.requireNonNull(latitud,      "La latitud es obligatoria");
            Objects.requireNonNull(longitud,     "La longitud es obligatoria");
            Objects.requireNonNull(fechaPerdida, "La fecha de perdida es obligatoria");
            Objects.requireNonNull(estado,       "El estado es obligatorio");
            Objects.requireNonNull(tipo,         "El tipo es obligatorio");
            Objects.requireNonNull(creador,      "El creador es obligatorio");
            if (fotosBase64.isEmpty())
                throw new IllegalArgumentException("Debe agregar al menos una foto");

            return new Mascota(this);
        }
    }

    public enum Estado {
        PERDIDO_PROPIO,
        PERDIDO_AJENO,
        RECUPERADO,
        ADOPTADO
    }

    public enum Tipo {
        PERRO,
        GATO,
        COBAYA,
        LORO,
        CONEJO,
        CABALLO,
        TORTUGA,
    }
}
