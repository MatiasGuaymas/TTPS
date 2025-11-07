package io.github.grupo01.volve_a_casa.persistence.repositories;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
