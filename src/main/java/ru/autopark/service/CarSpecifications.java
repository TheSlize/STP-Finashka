package ru.autopark.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.autopark.model.Car;

import java.util.ArrayList;
import java.util.List;

public final class CarSpecifications {

    private CarSpecifications() {
    }

    public static Specification<Car> fromCriteria(CarSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (CarSearchCriteria.hasText(c.getModel())) {
                predicates.add(cb.like(cb.lower(root.get("model")), likePattern(c.getModel())));
            }
            if (CarSearchCriteria.hasText(c.getOwnerName())) {
                predicates.add(cb.like(cb.lower(root.get("ownerName")), likePattern(c.getOwnerName())));
            }

            if (c.getYearFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("manufactureYear"), c.getYearFrom()));
            }
            if (c.getYearTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("manufactureYear"), c.getYearTo()));
            }

            if (c.getRegisteredFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("registrationDate"), c.getRegisteredFrom()));
            }
            if (c.getRegisteredTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("registrationDate"), c.getRegisteredTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static String likePattern(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }
}
