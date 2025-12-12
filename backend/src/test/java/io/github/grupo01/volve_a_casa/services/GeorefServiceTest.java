package io.github.grupo01.volve_a_casa.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeorefServiceTest {
    private final GeorefService service = new GeorefService();

    @Test
    public void testGetUbication() {
        double lat = -34.900;
        double lon = -58.015;
        var response = service.getUbication(lat, lon);
        assertEquals("La Plata", response.ubicacion().municipio().nombre());
    }
}
