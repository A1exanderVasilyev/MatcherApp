package ru.vasilyev.MatcherApp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.vasilyev.MatcherApp.enums.Gender;

import java.time.LocalDate;

@Data
public class UserUpdateDTO {
    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String username;

    @NotBlank(message = "Укажите Email")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Дата рождения обязательна")
    @Past(message = "Введите корректную дату рождения")
    private LocalDate dateOfBirth;

    @NotNull(message = "Пол обязателен")
    private Gender gender;

    @NotBlank(message = "Страна обязательна")
    private String country;

    @NotBlank(message = "Город обязателен")
    private String city;

    private Double latitude;
    private Double longitude;
}
