package ru.vasilyev.MatcherApp.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vasilyev.MatcherApp.dto.UserUpdateDTO;
import ru.vasilyev.MatcherApp.enums.Gender;
import ru.vasilyev.MatcherApp.models.User;
import ru.vasilyev.MatcherApp.models.UserPhoto;
import ru.vasilyev.MatcherApp.services.UserPhotoService;
import ru.vasilyev.MatcherApp.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final UserPhotoService userPhotoService;

    public ProfileController(UserService userService, UserPhotoService userPhotoService) {
        this.userService = userService;
        this.userPhotoService = userPhotoService;
    }

    @GetMapping
    public String showProfilePage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            List<UserPhoto> userPhotos = userPhotoService.getUserPhotos(user.getId());

            model.addAttribute("user", convertToUserUpdateDTO(user));
            model.addAttribute("userPhotos", userPhotos);
            model.addAttribute("genders", Gender.values());
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке профиля");
        }
        return "profile/edit";
    }

    private UserUpdateDTO convertToUserUpdateDTO(User user) {
        UserUpdateDTO userDTO = new UserUpdateDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setGender(user.getGender());
        userDTO.setCountry(user.getCountry());
        userDTO.setCity(user.getCity());

        return userDTO;
    }

}
