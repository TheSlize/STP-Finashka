package ru.warehouse.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.warehouse.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("form") RegistrationForm form) {
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult result,
                           RedirectAttributes redirect) {
        if (!result.hasFieldErrors("username") && userService.exists(form.getUsername())) {
            result.rejectValue("username", "taken", "Такой логин уже занят");
        }
        if (form.getPassword() != null && !form.getPassword().equals(form.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "mismatch", "Пароли не совпадают");
        }
        if (result.hasErrors()) {
            return "register";
        }
        userService.register(form.getUsername(), form.getPassword());
        redirect.addFlashAttribute("message", "Пользователь " + form.getUsername().trim() + " зарегистрирован, войдите");
        return "redirect:/login";
    }
}
