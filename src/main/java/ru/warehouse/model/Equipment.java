package ru.warehouse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Введите вид техники")
    @Size(max = 255, message = "не более 255 символов")
    @Column(nullable = false)
    private String type;

    // "group" - зарезервированное слово в SQL, поэтому поле называется groupName
    @NotBlank(message = "Введите группу техники")
    @Size(max = 255, message = "не более 255 символов")
    @Column(name = "group_name", nullable = false)
    private String groupName;

    @NotNull(message = "Укажите дату ввоза")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    // null, пока техника на складе
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "departure_date")
    private LocalDate departureDate;

    @NotBlank(message = "Введите ФИО водителя")
    @Size(max = 255, message = "не более 255 символов")
    @Column(name = "driver_name", nullable = false)
    private String driverName;

    public Equipment() {
    }

    public Equipment(String type, String groupName, LocalDate arrivalDate,
                     LocalDate departureDate, String driverName) {
        this.type = type;
        this.groupName = groupName;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.driverName = driverName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
