package io.github.vicen621.volveacasa.models;

import java.util.List;

public class User {
    private String name;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    private String city;
    private int points;
    private boolean enabled;
    private Role role;
    private List<Medalla> medallas;
    private Barrio barrio;

    public enum Role {
        USER,
        ADMIN,
    }
}
