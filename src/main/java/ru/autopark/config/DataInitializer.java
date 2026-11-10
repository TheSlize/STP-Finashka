package ru.autopark.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.autopark.model.Car;
import ru.autopark.repository.CarRepository;

import java.time.LocalDate;
import java.util.List;

// Заполняет пустую таблицу примерами; даты считаются от дня первого запуска
// оно сделано просто чтобы не было пустой БД без ничего, а было хоть что-то сразу вбито
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedCars(CarRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            LocalDate t = LocalDate.now();
            List<Car> cars = List.of(
                    new Car("Lada", 2015, t.minusDays(20), "Иванов Иван Иванович"),
                    new Car("Toyota", 2019, t.minusDays(17), "Петрова Анна Сергеевна"),
                    new Car("Kia", 2021, t.minusDays(13), "Сидоров Пётр Алексеевич"),
                    new Car("Hyundai", 2018, t.minusDays(12), "Кузнецова Мария Олеговна"),
                    new Car("Volkswagen", 2012, t.minusDays(10), "Смирнов Алексей Викторович"),
                    new Car("Skoda", 2020, t.minusDays(10), "Попова Екатерина Дмитриевна"),
                    new Car("Lada", 2023, t.minusDays(8), "Васильев Дмитрий Николаевич"),
                    new Car("BMW", 2016, t.minusDays(7), "Новикова Ольга Игоревна"),
                    new Car("Renault", 2014, t.minusDays(7), "Морозов Сергей Павлович"),
                    new Car("Toyota", 2022, t.minusDays(7), "Волкова Наталья Андреевна"),
                    new Car("Mercedes-Benz", 2017, t.minusDays(5), "Лебедев Андрей Юрьевич"),
                    new Car("Haval", 2024, t.minusDays(4), "Козлова Ирина Владимировна"),
                    new Car("Kia", 2010, t.minusDays(3), "Иванов Иван Иванович"),
                    new Car("Chery", 2025, t.minusDays(2), "Соколов Максим Евгеньевич"),
                    new Car("Geely", 2024, t.minusDays(1), "Павлова Дарья Романовна"),
                    new Car("Nissan", 2011, t, "Федоров Николай Сергеевич")
            );
            repository.saveAll(cars);
        };
    }
}
