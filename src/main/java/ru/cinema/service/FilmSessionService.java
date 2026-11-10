package ru.cinema.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.cinema.model.FilmSession;
import ru.cinema.repository.FilmSessionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class FilmSessionService {

    private final FilmSessionRepository repository;

    public FilmSessionService(FilmSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public FilmSession findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Сеанс с ID " + id + " не найден"));
    }

    @Transactional
    public FilmSession create(FilmSession session) {
        session.setId(null);
        normalize(session);
        return repository.save(session);
    }

    @Transactional
    public FilmSession update(Long id, FilmSession data) {
        FilmSession session = findById(id);
        session.setFilmTitle(data.getFilmTitle());
        session.setFilmStudio(data.getFilmStudio());
        session.setSessionTime(data.getSessionTime());
        session.setTicketCount(data.getTicketCount());
        normalize(session);
        return repository.save(session);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    @Transactional(readOnly = true)
    public List<FilmSession> search(FilmSessionSearchCriteria criteria) {
        return repository.findAll(FilmSessionSpecifications.fromCriteria(criteria), criteria.toSort());
    }

    @Transactional(readOnly = true)
    public CinemaStats statistics() {
        return new CinemaStats(repository.count(), repository.countStudios(), repository.sumTickets());
    }

    @Transactional(readOnly = true)
    public List<DayStat> dailySessions(int days) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(days - 1L);

        Map<LocalDate, Long> counts = new TreeMap<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            counts.put(d, 0L);
        }
        List<FilmSession> sessions = repository.findBySessionTimeGreaterThanEqualAndSessionTimeLessThan(
                from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        for (FilmSession session : sessions) {
            counts.merge(session.getSessionTime().toLocalDate(), 1L, Long::sum);
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

    private static void normalize(FilmSession session) {
        session.setFilmTitle(trim(session.getFilmTitle()));
        session.setFilmStudio(trim(session.getFilmStudio()));
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
