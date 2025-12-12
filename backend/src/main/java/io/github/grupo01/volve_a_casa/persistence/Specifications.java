package io.github.grupo01.volve_a_casa.persistence;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.filters.PetFilter;
import io.github.grupo01.volve_a_casa.persistence.filters.UserFilter;
import org.springframework.data.jpa.domain.Specification;

public class Specifications {
    public static Specification<Pet> getPetSpecification(PetFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter.name() != null && !filter.name().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("name"), filter.name()));
            }
            if (filter.color() != null && !filter.color().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("color"), filter.color()));
            }
            if (filter.state() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("state"), filter.state()));
            }
            if (filter.type() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("type"), filter.type()));
            }
            if (filter.size() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("size"), filter.size()));
            }
            if (filter.race() != null && !filter.race().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("race"), filter.race()));
            }
            if (filter.finalLostDate() != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("lostDate"), filter.finalLostDate()));
            }
            if (filter.initialLostDate() != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("lostDate"), filter.initialLostDate()));
            }
            if (filter.weightMin() > 0) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("weight"), filter.weightMin()));
            }
            if (filter.weightMax() > 0) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("weight"), filter.weightMax()));
            }

            return predicates;
        };
    }

    public static Specification<User> getUserSpecification(UserFilter user) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (user.email() != null && !user.email().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("email"), user.email()));
            }
            if (user.name() != null && !user.name().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("name"), user.name()));
            }
            if (user.lastName() != null && !user.lastName().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("lastName"), user.lastName()));
            }
            if (user.city() != null && !user.city().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("city"), user.city()));
            }
            if (user.neighborhood() != null && !user.neighborhood().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("neighborhood"), user.neighborhood()));
            }
            if (user.minPoints() > 0) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("points"), user.minPoints()));
            }
            if (user.maxPoints() > 0) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("points"), user.maxPoints()));
            }
            if (user.role() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("role"), user.role()));
            }

            return predicates;
        };
    }
}
