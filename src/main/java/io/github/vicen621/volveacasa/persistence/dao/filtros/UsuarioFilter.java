package io.github.vicen621.volveacasa.persistence.dao.filtros;

import io.github.vicen621.volveacasa.persistence.entities.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioFilter implements Filter {

    private String barrio;
    private String ciudad;
    private Usuario.Rol rol;

    private UsuarioFilter(Builder builder) {
        this.barrio = builder.barrio;
        this.ciudad = builder.ciudad;
        this.rol = builder.rol;
    }

    public String getBarrio() {
        return barrio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public Usuario.Rol getRol() {
        return rol;
    }

    @Override
    public QueryComponents buildQueryComponents() {
        List<String> predicates = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();

        if (this.barrio != null) {
            predicates.add("e.barrio = :barrio");
            parameters.put("barrio", this.barrio);
        }

        if (this.ciudad != null) {
            predicates.add("e.ciudad = :ciudad");
            parameters.put("ciudad", this.ciudad);
        }

        if (this.rol != null) {
            predicates.add("e.rol = :rol");
            parameters.put("rol", this.rol);
        }

        return new QueryComponents(predicates, parameters);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String barrio;
        private String ciudad;
        private Usuario.Rol rol;

        private Builder() {}

        public Builder conBarrio(String barrio) {
            this.barrio = barrio;
            return this;
        }

        public Builder conCiudad(String ciudad) {
            this.ciudad = ciudad;
            return this;
        }

        public Builder conRol(Usuario.Rol rol) {
            this.rol = rol;
            return this;
        }

        public UsuarioFilter build() {
            return new UsuarioFilter(this);
        }
    }
}
