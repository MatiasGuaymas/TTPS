package io.github.grupo01.volve_a_casa.controllers.dto;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;

public record PetUpdateDTO(
        String name,
        String description,
        String color,
        String size,
        String race,
        Float weight,
        Pet.Type type,
        Pet.State state,
        Float latitude,
        Float longitude
) { }
