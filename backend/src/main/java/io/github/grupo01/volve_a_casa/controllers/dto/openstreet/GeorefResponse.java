package io.github.grupo01.volve_a_casa.controllers.dto.openstreet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeorefResponse(
    Ubicacion ubicacion
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Ubicacion(
        Entidad departamento,
        Entidad municipio, // Puede ser null en zonas rurales
        Entidad provincia,
        double lat,
        double lon
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Entidad(
        String id,
        String nombre
    ) {}
}