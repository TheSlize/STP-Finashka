package ru.cinema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "film_sessions")
public class FilmSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Введите название фильма")
    @Size(max = 255, message = "не более 255 символов")
    @Column(name = "film_title", nullable = false)
    private String filmTitle;

    @NotBlank(message = "Введите киностудию")
    @Size(max = 255, message = "не более 255 символов")
    @Column(name = "film_studio", nullable = false)
    private String filmStudio;

    // формат как у поля datetime-local в браузере
    @NotNull(message = "Укажите дату и время сеанса")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "session_time", nullable = false)
    private LocalDateTime sessionTime;

    @NotNull(message = "Укажите количество билетов")
    @Min(value = 1, message = "Не меньше 1 билета")
    @Max(value = 1000, message = "Не больше 1000 билетов")
    @Column(name = "ticket_count", nullable = false)
    private Integer ticketCount;

    public FilmSession() {
    }

    public FilmSession(String filmTitle, String filmStudio, LocalDateTime sessionTime, Integer ticketCount) {
        this.filmTitle = filmTitle;
        this.filmStudio = filmStudio;
        this.sessionTime = sessionTime;
        this.ticketCount = ticketCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilmTitle() {
        return filmTitle;
    }

    public void setFilmTitle(String filmTitle) {
        this.filmTitle = filmTitle;
    }

    public String getFilmStudio() {
        return filmStudio;
    }

    public void setFilmStudio(String filmStudio) {
        this.filmStudio = filmStudio;
    }

    public LocalDateTime getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(LocalDateTime sessionTime) {
        this.sessionTime = sessionTime;
    }

    public Integer getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Integer ticketCount) {
        this.ticketCount = ticketCount;
    }
}
