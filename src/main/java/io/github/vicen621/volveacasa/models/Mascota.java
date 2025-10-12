package io.github.vicen621.volveacasa.models;


import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class Pet {
    private String nombre;
    private String tamano;
    private String descripcion;
    private String color;
    private LocalDate lostDate;
    private List<Image> images;
    private Point last_coordinate;
    private State state;

    public enum State {
        PERDIDO_PROPIO,
        PERDIDO_AJENO,
        RECUPERADO,
        ADOPTADO
    }
}
