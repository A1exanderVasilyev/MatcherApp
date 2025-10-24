package ru.vasilyev.MatcherApp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import ru.vasilyev.MatcherApp.enums.Gender;

import java.time.LocalDate;

@Data
public class UserRegistrationDTO {
    @NotBlank(message = "Укажите ваше имя")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String username;

    @NotBlank(message = "Укажите пароль")
    private String password;

    @NotNull(message = "Укажите дату")
    @Past(message = "Введите корректную дату рождения")
    private LocalDate dateOfBirth;

    @NotNull(message = "Укажите пол")
    private Gender gender;

    @NotBlank(message = "Укажите Email")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Укажите вашу страну")
    private String country;

    @NotBlank(message = "Укажите ваш город")
    private String city;

    private MultipartFile profilePhoto;

    public boolean hasProfilePhoto() {
        return profilePhoto != null && !profilePhoto.isEmpty();
    }
}
