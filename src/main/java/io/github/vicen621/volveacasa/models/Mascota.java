package io.github.vicen621.volveacasa.models;


import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class Mascota {
    private String nombre;
    private String tamano;
    private String descripcion;
    private String color;
    private LocalDate fechaPerdida;
    private Coordenada ultimaCoordenada;
    private Estado estado;
    private List<Image> imagenes;
    private List<Avistamiento> avistamientos;

    public enum Estado {
        PERDIDO_PROPIO,
        PERDIDO_AJENO,
        RECUPERADO,
        ADOPTADO
    }
}
