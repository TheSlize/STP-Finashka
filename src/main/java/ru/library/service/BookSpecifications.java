package ru.library.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.library.model.Book;
import ru.library.model.LoanStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class BookSpecifications {

    private BookSpecifications() {
    }

    public static Specification<Book> fromCriteria(BookSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (BookSearchCriteria.hasText(c.getTitle())) {
                predicates.add(cb.like(cb.lower(root.get("title")), likePattern(c.getTitle())));
            }
            if (BookSearchCriteria.hasText(c.getPublishingHouse())) {
                predicates.add(cb.like(cb.lower(root.get("publishingHouse")),
                        likePattern(c.getPublishingHouse())));
            }
            if (BookSearchCriteria.hasText(c.getStudentName())) {
                predicates.add(cb.like(cb.lower(root.get("studentName")),
                        likePattern(c.getStudentName())));
            }

            if (c.getIssueFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("issueDate"), c.getIssueFrom()));
            }
            if (c.getIssueTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("issueDate"), c.getIssueTo()));
            }

            if (c.getStatus() == LoanStatus.RETURNED) {
                predicates.add(cb.isNotNull(root.get("returnDate")));
            } else if (c.getStatus() == LoanStatus.ON_HAND) {
                predicates.add(cb.isNull(root.get("returnDate")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static String likePattern(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }
}
