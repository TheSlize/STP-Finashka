package ru.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.warehouse.model.Equipment;

import java.time.LocalDate;
import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long>, JpaSpecificationExecutor<Equipment> {

    List<Equipment> findByDepartureDateBetween(LocalDate from, LocalDate to);

    long countByDepartureDateIsNull();
}
