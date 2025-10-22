package io.github.vicen621.volveacasa.persistence.dao.filtros;

import io.github.vicen621.volveacasa.entities.Mascota;

public class MascotaFilter {
    private Mascota.Tipo tipo;
    private Mascota.Estado estado;
    private String raza;
    private String color;

    private MascotaFilter(Builder builder) {
        this.tipo = builder.tipo;
        this.estado = builder.estado;
        this.raza = builder.raza;
        this.color = builder.color;
    }

    public Mascota.Tipo getTipo() {
        return tipo;
    }

    public Mascota.Estado getEstado() {
        return estado;
    }

    public String getRaza() {
        return raza;
    }

    public String getColor() {
        return color;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Mascota.Tipo tipo;
        private Mascota.Estado estado;
        private String raza;
        private String color;

        private Builder() {}

        public Builder conTipo(Mascota.Tipo tipo) {
            this.tipo = tipo;
            return this;
        }

        public Builder conEstado(Mascota.Estado estado) {
            this.estado = estado;
            return this;
        }

        public Builder conRaza(String raza) {
            this.raza = raza;
            return this;
        }

        public Builder conColor(String color) {
            this.color = color;
            return this;
        }

        public MascotaFilter build() {
            return new MascotaFilter(this);
        }
    }
}
