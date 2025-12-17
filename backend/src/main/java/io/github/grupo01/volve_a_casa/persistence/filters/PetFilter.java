package io.github.grupo01.volve_a_casa.persistence.filters;

import java.time.LocalDate;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class PetFilter {
    private String name;
    private Pet.State state;
    private Pet.Type type;
    private Pet.Size size;
    private String color;
    private String race;
    private float weightMin;
    private float weightMax;
    private LocalDate initialLostDate;
    private LocalDate finalLostDate;
    //agrego atributos para filtrar por barrio
    private Float userLatitude;
    private Float userLongitude;
    private Float maxDistanceInKm;



}
