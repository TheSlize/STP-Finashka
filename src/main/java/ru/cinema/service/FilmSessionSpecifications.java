package ru.cinema.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.cinema.model.FilmSession;

import java.util.ArrayList;
import java.util.List;

public final class FilmSessionSpecifications {

    private FilmSessionSpecifications() {
    }

    public static Specification<FilmSession> fromCriteria(FilmSessionSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (FilmSessionSearchCriteria.hasText(c.getFilmTitle())) {
                predicates.add(cb.like(cb.lower(root.get("filmTitle")), likePattern(c.getFilmTitle())));
            }
            if (FilmSessionSearchCriteria.hasText(c.getFilmStudio())) {
                predicates.add(cb.like(cb.lower(root.get("filmStudio")), likePattern(c.getFilmStudio())));
            }

            // в поиске даты без времени, поэтому "по" включает весь день целиком
            if (c.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("sessionTime"), c.getDateFrom().atStartOfDay()));
            }
            if (c.getDateTo() != null) {
                predicates.add(cb.lessThan(root.get("sessionTime"), c.getDateTo().plusDays(1).atStartOfDay()));
            }

            if (c.getTicketsFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ticketCount"), c.getTicketsFrom()));
            }
            if (c.getTicketsTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ticketCount"), c.getTicketsTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static String likePattern(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }
}
