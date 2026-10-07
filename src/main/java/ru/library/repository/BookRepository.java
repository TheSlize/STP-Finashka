package ru.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.library.model.Book;

import java.time.LocalDate;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findByIssueDateBetween(LocalDate from, LocalDate to);

    long countByReturnDateIsNull();
}
