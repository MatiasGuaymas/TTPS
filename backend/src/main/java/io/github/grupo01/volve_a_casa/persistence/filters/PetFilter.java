package io.github.grupo01.volve_a_casa.persistence.filters;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;

import java.time.LocalDate;

public record PetFilter(
        String name,
        Pet.State state,
        Pet.Type type,
        Pet.Size size,
        String color,
        String race,
        float weightMin,
        float weightMax,
        LocalDate initialLostDate,
        LocalDate finalLostDate
) {
}
