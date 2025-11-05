package ru.vasilyev.MatcherApp.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.vasilyev.MatcherApp.dto.UserUpdateDTO;
import ru.vasilyev.MatcherApp.models.User;
import ru.vasilyev.MatcherApp.models.UserPhoto;
import ru.vasilyev.MatcherApp.services.UserPhotoService;
import ru.vasilyev.MatcherApp.services.UserService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserPhotoService userPhotoService;
    private final UserService userService;

    public ProfileController(UserPhotoService userPhotoService, UserService userService) {
        this.userPhotoService = userPhotoService;
        this.userService = userService;
    }

    @GetMapping()
    public String profilePage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            List<UserPhoto> userPhotos = userPhotoService.getUserPhotos(user.getId());

            model.addAttribute("user", convertToUserUpdateDTO(user));
            model.addAttribute("userPhotos", userPhotos);
            System.out.println(userPhotos);

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке фотографий");
        }
        return "profile/edit";
    }


    @GetMapping("/photos/{photoId}")
    @ResponseBody
    public ResponseEntity<Resource> servePhoto(@PathVariable Long photoId) {
        try {
            UserPhoto photo = userPhotoService.getPhoto(photoId);
            Path filePath = Paths.get(photo.getPhotoUrl());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath))
                        .body(resource);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при отдаче фото ID " + photoId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.notFound().build();
    }

    private UserUpdateDTO convertToUserUpdateDTO(User user) {
        UserUpdateDTO userDTO = new UserUpdateDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setGender(user.getGender());
        userDTO.setCountry(user.getCountry());
        userDTO.setCity(user.getCity());

        return userDTO;
    }
}
