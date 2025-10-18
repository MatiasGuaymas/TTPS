package io.github.vicen621.volveacasa.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String tamano;

    private String descripcion;

    private String color;

    private String raza;

    private float peso;

    private float latitud;

    private float longitud;

    private LocalDate fechaPerdida;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private List<String> fotosBase64;

    @OneToMany(mappedBy = "mascota")
    private List<Avistamiento> avistamientos;

    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;

    protected Mascota() {}

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
