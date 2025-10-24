package io.github.vicen621.volveacasa.persistence.entities.embeddable;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Coordenadas {
    private float latitud;
    private float longitud;

    protected Coordenadas() {}

    public Coordenadas(float latitud, float longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public float getLatitud() {
        return latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordenadas that)) return false;
        return Float.compare(getLatitud(), that.getLatitud()) == 0 &&
                Float.compare(getLongitud(), that.getLongitud()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLatitud(), getLongitud());
    }
}
