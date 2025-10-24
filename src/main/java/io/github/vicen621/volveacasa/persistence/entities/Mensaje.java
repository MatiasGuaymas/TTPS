package io.github.vicen621.volveacasa.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name="mensajes")
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contenido;
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name="destinatario_id")
    private Usuario destinatario;

    @ManyToOne
    @JoinColumn(name="emisor_id")
    private Usuario emisor;

    protected Mensaje() {}

    public Mensaje(String contenido, LocalDate fecha, Usuario destinatario, Usuario emisor) {
        this.contenido = contenido;
        this.fecha = fecha;
        this.destinatario = destinatario;
        this.emisor = emisor;
    }

    public Long getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Usuario destinatario) {
        this.destinatario = destinatario;
    }

    public Usuario getEmisor() {
        return emisor;
    }

    public void setEmisor(Usuario emisor) {
        this.emisor = emisor;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Mensaje mensaje)) return false;
        return Objects.equals(getId(), mensaje.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
