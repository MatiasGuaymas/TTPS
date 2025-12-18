package io.github.grupo01.volve_a_casa.persistence.repositories;

import io.github.grupo01.volve_a_casa.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, QueryByExampleExecutor<User> {
    Optional<User> findByEmail(String email);

    List<User> getAllByName(String name);

    boolean existsByEmail(String email);
}
