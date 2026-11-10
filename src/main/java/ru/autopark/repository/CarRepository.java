package ru.autopark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.autopark.model.Car;

import java.time.LocalDate;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    List<Car> findByRegistrationDateBetween(LocalDate from, LocalDate to);

    @Query("select count(distinct lower(c.model)) from Car c")
    long countModels();
}
