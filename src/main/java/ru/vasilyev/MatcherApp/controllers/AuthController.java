package ru.vasilyev.MatcherApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vasilyev.MatcherApp.enums.Gender;
import ru.vasilyev.MatcherApp.models.User;
import ru.vasilyev.MatcherApp.services.RegistrationService;

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
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                System.out.println("Ошибка поля:");
                System.out.println("  Поле: " + fieldError.getField());
                System.out.println("  Отклоненное значение: '" + fieldError.getRejectedValue() + "'");
                System.out.println("  Сообщение: " + fieldError.getDefaultMessage());
                System.out.println("  Код ошибки: " + fieldError.getCode());
            } else {
                System.out.println("Общая ошибка объекта:");
                System.out.println("  Объект: " + error.getObjectName());
                System.out.println("  Сообщение: " + error.getDefaultMessage());
                System.out.println("  Код ошибки: " + error.getCode());
            }
            System.out.println("------------------------------------");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values());
            return "auth/registration";
        }
        registrationService.register(user);
        return "redirect:/auth/login";
    }

}
