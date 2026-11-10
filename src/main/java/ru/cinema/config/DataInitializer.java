package ru.cinema.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.cinema.model.FilmSession;
import ru.cinema.repository.FilmSessionRepository;
import ru.cinema.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

// Заполняет пустые таблицы примерами; даты считаются от дня первого запуска
// оно сделано просто чтобы не было пустой БД без ничего, а было хоть что-то сразу вбито
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(UserService userService) {
        return args -> {
            if (!userService.exists("admin")) {
                userService.register("admin", "admin");
            }
        };
    }

    @Bean
    CommandLineRunner seedSessions(FilmSessionRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            LocalDate t = LocalDate.now();
            List<FilmSession> sessions = List.of(
                    new FilmSession("Брат", "СТВ", at(t.minusDays(20), 18, 0), 120),
                    new FilmSession("Ирония судьбы", "Мосфильм", at(t.minusDays(17), 20, 30), 150),
                    new FilmSession("Интерстеллар", "Warner Bros.", at(t.minusDays(13), 19, 0), 200),
                    new FilmSession("Москва слезам не верит", "Мосфильм", at(t.minusDays(12), 16, 15), 90),
                    new FilmSession("Начало", "Warner Bros.", at(t.minusDays(10), 21, 0), 180),
                    new FilmSession("Холоп", "Yellow, Black and White", at(t.minusDays(10), 13, 40), 110),
                    new FilmSession("Брат 2", "СТВ", at(t.minusDays(8), 18, 30), 130),
                    new FilmSession("Король Лев", "Walt Disney Pictures", at(t.minusDays(7), 11, 0), 160),
                    new FilmSession("Собачье сердце", "Ленфильм", at(t.minusDays(7), 15, 20), 70),
                    new FilmSession("Аватар", "20th Century Studios", at(t.minusDays(7), 20, 0), 220),
                    new FilmSession("Чебурашка", "Yellow, Black and White", at(t.minusDays(5), 12, 0), 140),
                    new FilmSession("Бриллиантовая рука", "Мосфильм", at(t.minusDays(4), 17, 45), 100),
                    new FilmSession("Оппенгеймер", "Universal Pictures", at(t.minusDays(3), 19, 30), 190),
                    new FilmSession("Движение вверх", "ТРИТЭ", at(t.minusDays(2), 18, 10), 150),
                    new FilmSession("Покровские ворота", "Мосфильм", at(t.minusDays(1), 14, 0), 80),
                    new FilmSession("Дюна", "Legendary Pictures", at(t, 21, 15), 210)
            );
            repository.saveAll(sessions);
        };
    }

    private static LocalDateTime at(LocalDate date, int hour, int minute) {
        return LocalDateTime.of(date, LocalTime.of(hour, minute));
    }
}
