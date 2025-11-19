package ru.vasilyev.MatcherApp.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vasilyev.MatcherApp.models.User;
import ru.vasilyev.MatcherApp.models.UserPhoto;
import ru.vasilyev.MatcherApp.repositoies.UserPhotoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserPhotoService {
    private final UserService userService;
    private final UserPhotoRepository userPhotoRepository;
    private final FileStorageService fileStorageService;

    public UserPhotoService(UserService userService, UserPhotoRepository userPhotoRepository, FileStorageService fileStorageService) {
        this.userService = userService;
        this.userPhotoRepository = userPhotoRepository;
        this.fileStorageService = fileStorageService;
    }

    public UserPhoto uploadPhoto(Long userId, MultipartFile multipartFile, Boolean isPrimary) {
        try {
            User user = userService.findById(userId);
            if (isPrimary) {
                clearPrimaryPhoto(userId);
            }
            String filePath = fileStorageService.storeFile(multipartFile, userId);

            UserPhoto userPhoto = new UserPhoto(
                    filePath,
                    multipartFile.getOriginalFilename(),
                    multipartFile.getSize(),
                    user,
                    isPrimary
            );

            UserPhoto savedPhoto = userPhotoRepository.save(userPhoto);
            log.info("Photo uploaded for user {}: {}", userId, savedPhoto.getId());
            return savedPhoto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<UserPhoto> getUserPhotos(Long userId) {
        return userPhotoRepository.findByUserIdOrderByUploadedAtDesc(userId);
    }

    public UserPhoto getPhoto(Long photoId) {
        return userPhotoRepository.findById(photoId).orElseThrow();
    }

    public void clearPrimaryPhoto(Long userId) {
        try {
            UserPhoto currentPrimary = userPhotoRepository.findByUserIdAndIsPrimaryTrue(userId);
            if (currentPrimary != null) {
                currentPrimary.setIsPrimary(false);
                userPhotoRepository.save(currentPrimary);
                userPhotoRepository.flush();
            }
        } catch (Exception e) {
            log.error("Error clearing primary photo for user {}", userId, e);
            throw new RuntimeException("Failed to clear primary photo", e);
        }
    }

    public List<String> validatePhotoFile(MultipartFile file) {
        List<String> errors = new ArrayList<>();

        if (file.isEmpty()) {
            errors.add("Файл не выбран");
            return errors;
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            errors.add("Файл должен быть изображением");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            errors.add("Размер файла не должен превышать 5MB");
        }

        return errors;
    }
}
