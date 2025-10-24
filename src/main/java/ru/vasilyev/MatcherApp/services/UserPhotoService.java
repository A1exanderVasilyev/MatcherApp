package ru.vasilyev.MatcherApp.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vasilyev.MatcherApp.models.User;
import ru.vasilyev.MatcherApp.models.UserPhoto;
import ru.vasilyev.MatcherApp.repositoies.UserPhotoRepository;

import java.util.List;

@Service
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
}
