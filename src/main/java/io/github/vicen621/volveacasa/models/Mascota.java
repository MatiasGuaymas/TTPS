package io.github.vicen621.volveacasa.models;

import java.time.LocalDate;
import java.util.List;

public class Mascota {
    private String nombre;
    private String tamano;
    private String descripcion;
    private String color;
    private String raza;
    private float peso;
    private float latitud;
    private float longitud;
    private LocalDate fechaPerdida;
    private Estado estado;
    private Tipo tipo;
    private List<String> fotosBase64;
    private List<Avistamiento> avistamientos;
    private Usuario creador;

    // Campos opcionales para cuando se adopta
    private Usuario adoptante;
    private LocalDate fechaAdopcion;

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
