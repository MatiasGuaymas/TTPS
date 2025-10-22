package io.github.vicen621.volveacasa.persistence.dao.filtros;

import io.github.vicen621.volveacasa.entities.Usuario;

public class UsuarioFilter {

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
