package ru.vasilyev.MatcherApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.vasilyev.MatcherApp.dto.UserRegistrationDTO;
import ru.vasilyev.MatcherApp.enums.Gender;
import ru.vasilyev.MatcherApp.services.RegistrationService;
import ru.vasilyev.MatcherApp.util.exceptions.UserExistsException;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final RegistrationService registrationService;

    @Autowired
    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("user") UserRegistrationDTO userRegistrationDTO,
                                   Model model) {
        model.addAttribute("genders", Gender.values());
        return "auth/registration";
    }

    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String processRegistration(@ModelAttribute("user") @Valid UserRegistrationDTO userRegistrationDTO,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (!userRegistrationDTO.hasProfilePhoto()) {
            bindingResult.rejectValue("profilePhoto", "error.user", "Выберите фото профиля");
        } else {
            photoValidation(userRegistrationDTO.getProfilePhoto(), bindingResult);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values());
            return "auth/registration";
        }

        try {
            registrationService.register(userRegistrationDTO);
            redirectAttributes.addFlashAttribute("success", "Регистрация прошла успешно! Теперь вы можете войти.");
            return "redirect:/auth/login";
        } catch (UserExistsException e) {
            bindingResult.rejectValue("email", "error.user", "Email уже занят");
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при регистрации");
        }
        model.addAttribute("genders", Gender.values());
        return "/auth/registration";
    }

    @GetMapping("/login")
    public String loginPage(Model model,
                            @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Неверный email или пароль");
        }

        return "auth/login";
    }

    private void photoValidation(MultipartFile photo, BindingResult result) {
        if (photo.getSize() > 5 * 1024 * 1024) {
            result.rejectValue("profilePhoto", "error.user", "Размер файла не должен превышать 5MB");
        }

        String contentType = photo.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            result.rejectValue("profilePhoto", "error.user", "Файл должен быть изображением");
        }
    }
}
