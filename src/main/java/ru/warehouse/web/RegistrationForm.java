package ru.warehouse.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Введите логин")
    @Size(min = 3, max = 50, message = "от 3 до 50 символов")
    @Pattern(regexp = "[A-Za-z0-9_.-]*", message = "только латиница, цифры и символы _ . -")
    private String username;

    @NotBlank(message = "Введите пароль")
    @Size(min = 4, max = 100, message = "от 4 до 100 символов")
    private String password;

    @NotBlank(message = "Повторите пароль")
    private String passwordConfirm;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
