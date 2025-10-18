package io.github.vicen621.volveacasa.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
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
}
