package io.github.vicen621.volveacasa.models;

import java.awt.*;
import java.time.LocalDate;

// No necesita la mascota porque
// la unica manera de obtener esta clase es a traves de la mascota
public class Avistamiento {
    private Usuario reportador;
    private float latitud;
    private float longitud;
    private String fotoBase64;
    private LocalDate fecha;
    private String comentario;
}
