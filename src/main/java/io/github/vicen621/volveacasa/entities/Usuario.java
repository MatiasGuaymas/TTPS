package io.github.vicen621.volveacasa.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String apellidos;

    @Column(unique = true)
    private String email;

    private String contrasena;

    private String telefono;

    private String ciudad;

    private String barrio;

    private int puntos;

    private boolean habilitado;

    private float latitud;

    private float longitud;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @ManyToMany
    @JoinTable(
            name = "usuario_medallas",
            joinColumns = @JoinColumn(
                    name = "usuario_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "medalla_id",
                    referencedColumnName = "id"
            )
    )
    private List<Medalla> medallas;

    @OneToMany(mappedBy = "creador")
    List<Mascota> mascotas;
    @OneToMany(mappedBy = "reportador")
    List<Avistamiento> avistamientos;

    public enum Rol {
        USUARIO,
        ADMIN,
    }
}
