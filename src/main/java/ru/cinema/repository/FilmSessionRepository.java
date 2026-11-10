package ru.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.cinema.model.FilmSession;

import java.time.LocalDateTime;
import java.util.List;

public interface FilmSessionRepository extends JpaRepository<FilmSession, Long>, JpaSpecificationExecutor<FilmSession> {

    List<FilmSession> findBySessionTimeGreaterThanEqualAndSessionTimeLessThan(LocalDateTime from, LocalDateTime to);

    @Query("select count(distinct lower(s.filmStudio)) from FilmSession s")
    long countStudios();

    @Query("select coalesce(sum(s.ticketCount), 0) from FilmSession s")
    long sumTickets();
}
