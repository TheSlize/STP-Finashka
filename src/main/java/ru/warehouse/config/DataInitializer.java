package ru.warehouse.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.warehouse.model.Equipment;
import ru.warehouse.repository.EquipmentRepository;
import ru.warehouse.service.UserService;

import java.time.LocalDate;
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
    CommandLineRunner seedEquipment(EquipmentRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            LocalDate t = LocalDate.now();
            List<Equipment> items = List.of(
                    new Equipment("Холодильник", "Крупная бытовая техника", t.minusDays(25), t.minusDays(13), "Иванов Иван Иванович"),
                    new Equipment("Стиральная машина", "Крупная бытовая техника", t.minusDays(22), t.minusDays(12), "Петров Сергей Николаевич"),
                    new Equipment("Ноутбук", "Компьютерная техника", t.minusDays(20), t.minusDays(10), "Сидоров Пётр Алексеевич"),
                    new Equipment("Телевизор", "Аудио- и видеотехника", t.minusDays(18), t.minusDays(10), "Кузнецов Олег Викторович"),
                    new Equipment("Микроволновая печь", "Мелкая бытовая техника", t.minusDays(16), t.minusDays(7), "Смирнов Алексей Викторович"),
                    new Equipment("Монитор", "Компьютерная техника", t.minusDays(15), t.minusDays(7), "Попов Дмитрий Андреевич"),
                    new Equipment("Пылесос", "Мелкая бытовая техника", t.minusDays(14), t.minusDays(7), "Васильев Дмитрий Николаевич"),
                    new Equipment("Смартфон", "Мобильные устройства", t.minusDays(12), t.minusDays(4), "Новиков Игорь Олегович"),
                    new Equipment("Посудомоечная машина", "Крупная бытовая техника", t.minusDays(11), t.minusDays(2), "Морозов Сергей Павлович"),
                    new Equipment("Принтер", "Компьютерная техника", t.minusDays(9), t.minusDays(1), "Волков Андрей Юрьевич"),
                    new Equipment("Саундбар", "Аудио- и видеотехника", t.minusDays(7), t, "Лебедев Андрей Юрьевич"),
                    new Equipment("Планшет", "Мобильные устройства", t.minusDays(6), null, "Козлов Илья Владимирович"),
                    new Equipment("Электрочайник", "Мелкая бытовая техника", t.minusDays(4), null, "Иванов Иван Иванович"),
                    new Equipment("Кондиционер", "Климатическая техника", t.minusDays(3), null, "Соколов Максим Евгеньевич"),
                    new Equipment("Игровая приставка", "Аудио- и видеотехника", t.minusDays(1), null, "Павлов Роман Сергеевич"),
                    new Equipment("Духовой шкаф", "Крупная бытовая техника", t, null, "Федоров Николай Сергеевич")
            );
            repository.saveAll(items);
        };
    }
}
