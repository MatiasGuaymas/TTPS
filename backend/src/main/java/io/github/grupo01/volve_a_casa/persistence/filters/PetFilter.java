package io.github.grupo01.volve_a_casa.persistence.filters;

import java.time.LocalDate;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class PetFilter {
    private final String name;
    private final Pet.State state;
    private final Pet.Type type;
    private final Pet.Size size;
    private final String color;
    private final String race;
    private final float weightMin;
    private final float weightMax;
    private final LocalDate initialLostDate;
    private final LocalDate finalLostDate;

    //agrego atributos para filtrar por barrio
    private final Float userLatitude;
    private final Float userLongitude;
    private final Float maxDistanceInKm;



}
