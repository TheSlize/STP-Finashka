package ru.autopark.model;

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

import java.time.LocalDate;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Введите марку")
    @Size(max = 255, message = "не более 255 символов")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Укажите год выпуска")
    @Min(value = 1900, message = "Год выпуска не раньше 1900")
    @Max(value = 2100, message = "Некорректный год выпуска")
    @Column(name = "manufacture_year", nullable = false)
    private Integer manufactureYear;

    @NotNull(message = "Укажите дату постановки на учёт")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @NotBlank(message = "Введите ФИО владельца")
    @Size(max = 255, message = "не более 255 символов")
    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    public Car() {
    }

    public Car(String model, Integer manufactureYear, LocalDate registrationDate, String ownerName) {
        this.model = model;
        this.manufactureYear = manufactureYear;
        this.registrationDate = registrationDate;
        this.ownerName = ownerName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
