package io.github.vicen621.volveacasa.entities;

import jakarta.persistence.*;

@Entity
public class Medalla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombre;

    private String descripcion;

    private String iconoBase64;

    //FIXME: Ver si necesita lista de usuarios
    protected Medalla() {}
}
