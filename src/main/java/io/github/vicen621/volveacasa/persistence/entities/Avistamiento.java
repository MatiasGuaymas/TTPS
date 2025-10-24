package io.github.vicen621.volveacasa.persistence.entities;

import io.github.vicen621.volveacasa.persistence.entities.embeddable.Coordenadas;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name="avistamientos")
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Coordenadas coordenadas;

    private String fotoBase64;

    private LocalDate fecha;

    private String comentario;

    @ManyToOne
    @JoinColumn(name = "reportador_id")
    private Usuario reportador;

    @ManyToOne
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;

    protected Avistamiento() {}

    private Avistamiento(Builder builder) {
        this.mascota = builder.mascota;
        this.coordenadas = new Coordenadas(builder.latitud, builder.longitud);
        this.fotoBase64 = builder.fotoBase64;
        this.comentario = builder.comentario;
        this.fecha = builder.fecha;
        this.reportador = builder.reportador;
    }

    public Long getId() {
        return id;
    }

    public Usuario getReportador() {
        return reportador;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public void actualizarUbicacion(float latitud, float longitud) {
        this.coordenadas = new Coordenadas(latitud, longitud);
    }

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Mascota getMascota() {
        return mascota;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Avistamiento that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Mascota mascota;
        private Usuario reportador;
        private Float latitud;
        private Float longitud;
        private String fotoBase64;
        private LocalDate fecha;

        private String comentario = "";

        private Builder() {}

        public Builder mascota(Mascota mascota) {
            this.mascota = mascota;
            return this;
        }

        public Builder reportador(Usuario reportador) {
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

        public Avistamiento build() {
            Objects.requireNonNull(mascota,    "La mascota es obligatoria");
            Objects.requireNonNull(reportador, "El reportador es obligatorio");
            Objects.requireNonNull(latitud,    "La latitud es obligatoria");
            Objects.requireNonNull(longitud,   "La longitud es obligatoria");
            Objects.requireNonNull(fotoBase64, "La foto es obligatoria");
            Objects.requireNonNull(fecha,      "La fecha es obligatoria");

            Avistamiento avistamiento = new Avistamiento(this);
            reportador.addAvistamiento(avistamiento);
            mascota.addAvistamiento(avistamiento);
            return avistamiento;
        }
    }
}
