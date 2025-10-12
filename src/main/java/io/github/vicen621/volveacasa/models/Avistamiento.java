package io.github.vicen621.volveacasa.models;

import java.awt.*;
import java.time.LocalDate;

// No necesita la mascota porque
// la unica manera de obtener esta clase es a traves de la mascota
public class Avistamiento {
    private Usuario reportador;
    private Coordenada coordenada;
    private Image foto;
    private LocalDate fecha;
    private String comentario;
}
