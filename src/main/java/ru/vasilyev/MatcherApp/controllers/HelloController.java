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
import ru.vasilyev.MatcherApp.models.User;
import ru.vasilyev.MatcherApp.models.UserPhoto;
import ru.vasilyev.MatcherApp.services.UserPhotoService;
import ru.vasilyev.MatcherApp.services.UserService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/photos")
public class HelloController {
    private final UserPhotoService userPhotoService;
    private final UserService userService;

    public HelloController(UserPhotoService userPhotoService, UserService userService) {
        this.userPhotoService = userPhotoService;
        this.userService = userService;
    }

    @GetMapping()
    public String helloPage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            List<UserPhoto> userPhotos = userPhotoService.getUserPhotos(user.getId());

            model.addAttribute("user", user);
            model.addAttribute("userPhotos", userPhotos);

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке фотографий");
        }
        return "hello";
    }


    @GetMapping("/{photoId}")
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

        }

        return ResponseEntity.notFound().build();
    }
}
