package ru.library.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.library.model.Book;
import ru.library.repository.BookRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Книга с ID " + id + " не найдена"));
    }

    @Transactional
    public Book create(Book book) {
        book.setId(null);
        normalize(book);
        return repository.save(book);
    }

    @Transactional
    public Book update(Long id, Book data) {
        Book book = findById(id);
        book.setTitle(data.getTitle());
        book.setPublishingHouse(data.getPublishingHouse());
        book.setIssueDate(data.getIssueDate());
        book.setStudentName(data.getStudentName());
        book.setReturnDate(data.getReturnDate());
        normalize(book);
        return repository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    @Transactional(readOnly = true)
    public List<Book> search(BookSearchCriteria criteria) {
        return repository.findAll(BookSpecifications.fromCriteria(criteria), criteria.toSort());
    }

    @Transactional(readOnly = true)
    public LibraryStats statistics() {
        return new LibraryStats(repository.count(), repository.countByReturnDateIsNull());
    }

    @Transactional(readOnly = true)
    public List<DayStat> dailyIssues(int days) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(days - 1L);

        Map<LocalDate, Long> counts = new TreeMap<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            counts.put(d, 0L);
        }
        for (Book book : repository.findByIssueDateBetween(from, to)) {
            counts.merge(book.getIssueDate(), 1L, Long::sum);
        }

        long max = 0;
        for (long c : counts.values()) {
            max = Math.max(max, c);
        }

        List<DayStat> result = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> e : counts.entrySet()) {
            int height = max == 0 ? 0 : (int) Math.round(e.getValue() * 100.0 / max);
            result.add(new DayStat(e.getKey(), e.getValue(), height));
        }
        return result;
    }

    private static void normalize(Book book) {
        book.setTitle(trim(book.getTitle()));
        book.setPublishingHouse(trim(book.getPublishingHouse()));
        book.setStudentName(trim(book.getStudentName()));
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
