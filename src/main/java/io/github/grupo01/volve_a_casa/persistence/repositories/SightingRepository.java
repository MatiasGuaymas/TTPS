package io.github.grupo01.volve_a_casa.persistence.repositories;

import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SightingRepository extends JpaRepository<Sighting, Long> {
    List<Sighting> findByPetIdOrderByDateDesc(Long petId);
}
