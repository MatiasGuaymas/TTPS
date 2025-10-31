package io.github.vicen621.volveacasa.persistence.entities;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Entity
@Table(name="medallas")
@Component
public class Medalla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombre;

    private String descripcion;

    private String iconoBase64;

    protected Medalla() {}

    public Medalla(String nombre, String descripcion, String iconoBase64) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.iconoBase64 = iconoBase64;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIconoBase64() {
        return iconoBase64;
    }

    public void setIconoBase64(String iconoBase64) {
        this.iconoBase64 = iconoBase64;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Medalla medalla)) return false;
        return Objects.equals(getId(), medalla.getId()) && Objects.equals(getNombre(), medalla.getNombre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNombre());
    }
}
