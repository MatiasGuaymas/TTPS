package io.github.grupo01.volve_a_casa.persistence.repositories;

import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SightingRepository extends JpaRepository<Sighting, Long> {

}
