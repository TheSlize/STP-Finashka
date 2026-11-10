package ru.warehouse.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.warehouse.model.Equipment;
import ru.warehouse.model.StockStatus;

import java.util.ArrayList;
import java.util.List;

public final class EquipmentSpecifications {

    private EquipmentSpecifications() {
    }

    public static Specification<Equipment> fromCriteria(EquipmentSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (EquipmentSearchCriteria.hasText(c.getType())) {
                predicates.add(cb.like(cb.lower(root.get("type")), likePattern(c.getType())));
            }
            if (EquipmentSearchCriteria.hasText(c.getGroupName())) {
                predicates.add(cb.like(cb.lower(root.get("groupName")), likePattern(c.getGroupName())));
            }
            if (EquipmentSearchCriteria.hasText(c.getDriverName())) {
                predicates.add(cb.like(cb.lower(root.get("driverName")), likePattern(c.getDriverName())));
            }

            if (c.getArrivalFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("arrivalDate"), c.getArrivalFrom()));
            }
            if (c.getArrivalTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("arrivalDate"), c.getArrivalTo()));
            }

            if (c.getStatus() == StockStatus.SHIPPED) {
                predicates.add(cb.isNotNull(root.get("departureDate")));
            } else if (c.getStatus() == StockStatus.IN_STOCK) {
                predicates.add(cb.isNull(root.get("departureDate")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static String likePattern(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }
}
