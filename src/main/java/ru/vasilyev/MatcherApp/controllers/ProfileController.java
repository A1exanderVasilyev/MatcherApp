package ru.vasilyev.MatcherApp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.vasilyev.MatcherApp.dto.UserUpdateDTO;
import ru.vasilyev.MatcherApp.models.User;
import ru.vasilyev.MatcherApp.models.UserPhoto;
import ru.vasilyev.MatcherApp.services.UserDetailsServiceImpl;
import ru.vasilyev.MatcherApp.services.UserPhotoService;
import ru.vasilyev.MatcherApp.services.UserService;
import ru.vasilyev.MatcherApp.util.constants.Constants;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/profile")
public class ProfileController {
    private final UserPhotoService userPhotoService;
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public ProfileController(UserPhotoService userPhotoService, UserService userService, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userPhotoService = userPhotoService;
        this.userService = userService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
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

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке данных");
        }
        return "profile/edit";
    }

    // TODO вынести изменение email в отдельную страницу связанную с авторизацией и тп
    @PostMapping("/update")
    public String updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("user") @Valid UserUpdateDTO userUpdateDTO,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request
    ) {
        try {
            String oldEmail = userDetails.getUsername();
            User user = userService.findByEmail(oldEmail);
            List<UserPhoto> userPhotos = userPhotoService.getUserPhotos(user.getId());
            if (bindingResult.hasErrors()) {
                model.addAttribute("userPhotos", userPhotos);
                return "profile/edit";
            }

            User updatedUser = userService.updateUser(user.getId(), userUpdateDTO);
            if (!oldEmail.equals(updatedUser.getEmail())) {
                updateSecurityContext(updatedUser, request);
            }

            return "redirect:/profile?success";
        } catch (Exception e) {
            return "redirect:/profile?error";
        }
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

    @PostMapping("/photos/upload")
    public String uploadPhotos(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("photo") MultipartFile file,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            List<String> errors = userPhotoService.validatePhotoFile(file);
            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:/profile";
            }

            int userPhotosCount = user.getPhotos().size();
            if (userPhotosCount >= Constants.MAX_PHOTO_PER_USER_COUNT) {
                redirectAttributes.addFlashAttribute("error", "Достигнут лимит фотографий (максимум " +
                        Constants.MAX_PHOTO_PER_USER_COUNT + ")");
                return "redirect:/profile";
            }
            UserPhoto uploadPhoto = userPhotoService.uploadPhoto(user.getId(), file, isPrimary);
            String message = "Фотография успешно загружена";
            redirectAttributes.addFlashAttribute("success", message);

        } catch (Exception e) {
            log.error("Ошибка загрузки фотографии для пользователя {}", userDetails.getUsername(), e);
            redirectAttributes.addFlashAttribute("error", "Ошибка загрузки: " + e.getMessage());
        }

        return "redirect:/profile";
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

    private void updateSecurityContext(User updatedUser, HttpServletRequest request) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(updatedUser.getEmail());

        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        newAuth.setDetails(SecurityContextHolder.getContext().getAuthentication().getDetails());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        }

    }
}
