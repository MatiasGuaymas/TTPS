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

            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("name"), filter.getName()));
            }
            if (filter.getColor() != null && !filter.getColor().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("color"), filter.getColor()));
            }
            if (filter.getState() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("state"), filter.getState()));
            }
            if (filter.getType() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("type"), filter.getType()));
            }
            if (filter.getSize() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("size"), filter.getSize()));
            }
            if (filter.getRace() != null && !filter.getRace().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("race"), filter.getRace()));
            }
            if (filter.getFinalLostDate() != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("lostDate"), filter.getFinalLostDate()));
            }
            if (filter.getInitialLostDate() != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("lostDate"), filter.getInitialLostDate()));
            }
            if (filter.getWeightMin() > 0) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("weight"), filter.getWeightMin()));
            }
            if (filter.getWeightMax() > 0) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("weight"), filter.getWeightMax()));
            }

            return predicates;
        };
    }

    public static Specification<User> getUserSpecification(UserFilter user) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("email"), user.getEmail()));
            }
            if (user.getName() != null && !user.getName().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("name"), user.getName()));
            }
            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("lastName"), user.getLastName()));
            }
            if (user.getCity() != null && !user.getCity().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("city"), user.getCity()));
            }
            if (user.getNeighborhood() != null && !user.getNeighborhood().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("neighborhood"), user.getNeighborhood()));
            }
            if (user.getMinPoints() > 0) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("points"), user.getMinPoints()));
            }
            if (user.getMaxPoints() > 0) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("points"), user.getMaxPoints()));
            }
            if (user.getRole() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("role"), user.getRole()));
            }

            return predicates;
        };
    }
}
