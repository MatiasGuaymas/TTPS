package io.github.vicen621.volveacasa.models;

import java.util.List;

public class Usuario {
    private String nombre;
    private String apellidos;
    private String email;
    private String contrasena;
    private String telefono;
    private String ciudad;
    private int puntos;
    private boolean habilitado;
    private Rol rol;
    private List<Medalla> medallas;
    private Barrio barrio;

    public enum Rol {
        USUARIO,
        ADMIN,
    }
}
