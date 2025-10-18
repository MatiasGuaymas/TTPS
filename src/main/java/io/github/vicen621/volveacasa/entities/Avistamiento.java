package io.github.vicen621.volveacasa.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reportador_id")
    private Usuario reportador;

    private float latitud;

    private float longitud;

    private String fotoBase64;

    private LocalDate fecha;

    private String comentario;

    @ManyToOne
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;

    protected Avistamiento() {}
}
