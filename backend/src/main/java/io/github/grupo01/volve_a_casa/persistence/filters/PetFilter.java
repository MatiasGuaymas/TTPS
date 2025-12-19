package io.github.grupo01.volve_a_casa.persistence.filters;

import java.time.LocalDate;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetFilter {
    private String name;
    private Pet.State state;
    private Pet.Type type;
    private Pet.Size petSize;
    private String color;
    private String race;
    private Float weightMin;
    private Float weightMax;
    private LocalDate initialLostDate;
    private LocalDate finalLostDate;
}
