package ru.library.model;

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
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Введите название книги")
    @Size(max = 255, message = "не более 255 символов")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Введите издательство")
    @Size(max = 255, message = "не более 255 символов")
    @Column(name = "publishing_house", nullable = false)
    private String publishingHouse;

    @NotNull(message = "Укажите дату выдачи")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @NotBlank(message = "Введите ФИО студента")
    @Size(max = 255, message = "не более 255 символов")
    @Column(name = "student_name", nullable = false)
    private String studentName;

    // null, пока книга на руках
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "return_date")
    private LocalDate returnDate;

    public Book() {
    }

    public Book(String title, String publishingHouse, LocalDate issueDate,
                String studentName, LocalDate returnDate) {
        this.title = title;
        this.publishingHouse = publishingHouse;
        this.issueDate = issueDate;
        this.studentName = studentName;
        this.returnDate = returnDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishingHouse() {
        return publishingHouse;
    }

    public void setPublishingHouse(String publishingHouse) {
        this.publishingHouse = publishingHouse;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
