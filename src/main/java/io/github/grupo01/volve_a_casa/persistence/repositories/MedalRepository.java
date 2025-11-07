package io.github.grupo01.volve_a_casa.persistence.repositories;

import io.github.grupo01.volve_a_casa.persistence.entities.Medal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedalRepository extends JpaRepository<Medal, Long> {
    Optional<Medal> findByNombre(String nombre);
}
