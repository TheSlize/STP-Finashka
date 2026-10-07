package ru.library.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.library.model.Book;
import ru.library.repository.BookRepository;

import java.time.LocalDate;
import java.util.List;

// Заполняет пустую таблицу примерами; даты считаются от дня первого запуска
// оно сделано просто чтобы не было пустой БД без ничего, а было хоть что-то сразу вбито
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedBooks(BookRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            LocalDate t = LocalDate.now();
            List<Book> books = List.of(
                    new Book("Война и мир", "Эксмо", t.minusDays(20), "Иванов Иван Иванович", null),
                    new Book("Преступление и наказание", "АСТ", t.minusDays(17), "Петрова Анна Сергеевна", null),
                    new Book("Философия Java", "Питер", t.minusDays(13), "Сидоров Пётр Алексеевич", t.minusDays(5)),
                    new Book("Чистый код", "Питер", t.minusDays(12), "Кузнецова Мария Олеговна", null),
                    new Book("Мастер и Маргарита", "АСТ", t.minusDays(10), "Смирнов Алексей Викторович", t.minusDays(2)),
                    new Book("Алгоритмы: построение и анализ", "Вильямс", t.minusDays(10), "Попова Екатерина Дмитриевна", null),
                    new Book("Евгений Онегин", "Просвещение", t.minusDays(8), "Васильев Дмитрий Николаевич", t.minusDays(1)),
                    new Book("Spring в действии", "ДМК Пресс", t.minusDays(7), "Новикова Ольга Игоревна", null),
                    new Book("Высшая математика", "Наука", t.minusDays(7), "Морозов Сергей Павлович", null),
                    new Book("Мёртвые души", "Эксмо", t.minusDays(7), "Волкова Наталья Андреевна", t.minusDays(3)),
                    new Book("Базы данных. Проектирование", "БХВ-Петербург", t.minusDays(5), "Лебедев Андрей Юрьевич", null),
                    new Book("Отцы и дети", "Просвещение", t.minusDays(4), "Козлова Ирина Владимировна", null),
                    new Book("Java. Библиотека профессионала", "Вильямс", t.minusDays(3), "Иванов Иван Иванович", null),
                    new Book("Герой нашего времени", "АСТ", t.minusDays(2), "Соколов Максим Евгеньевич", null),
                    new Book("Физика. Курс лекций", "Наука", t.minusDays(1), "Павлова Дарья Романовна", null),
                    new Book("Грокаем алгоритмы", "Питер", t, "Федоров Николай Сергеевич", null)
            );
            repository.saveAll(books);
        };
    }
}
