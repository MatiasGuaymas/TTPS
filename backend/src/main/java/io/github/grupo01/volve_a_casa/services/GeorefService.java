package io.github.grupo01.volve_a_casa.services;

import io.github.grupo01.volve_a_casa.controllers.dto.openstreet.GeorefResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeorefService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "https://apis.datos.gob.ar/georef/api/ubicacion?lat={lat}&lon={lon}";

    public GeorefResponse getUbication(double lat, double lon) {
        try {
            return restTemplate.getForObject(API_URL, GeorefResponse.class, lat, lon);
        } catch (Exception e) {
            // Manejo básico de errores: podrías devolver null o lanzar una excepción propia
            System.err.println("Error conectando con GeoRef: " + e.getMessage());
            return null;
        }
    }
}
