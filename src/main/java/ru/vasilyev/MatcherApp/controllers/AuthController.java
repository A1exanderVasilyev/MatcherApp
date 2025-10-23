package ru.vasilyev.MatcherApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vasilyev.MatcherApp.enums.Gender;
import ru.vasilyev.MatcherApp.models.User;
import ru.vasilyev.MatcherApp.services.RegistrationService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final RegistrationService registrationService;

    @Autowired
    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("user") User user,
                                   Model model) {
        model.addAttribute("genders", Gender.values());
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String processRegistration(@ModelAttribute("user") @Valid User user,
                                      BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values());
            return "auth/registration";
        }
        user.setRole("ROLE_USER");
        LocalDateTime currentDate = LocalDateTime.now();
        user.setCreatedAt(currentDate);
        user.setUpdatedAt(currentDate);
        registrationService.register(user);
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model,
                            @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Неверный email или пароль");
        }

        return "auth/login";
    }
}
