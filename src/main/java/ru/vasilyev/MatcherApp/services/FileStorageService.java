package ru.vasilyev.MatcherApp.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    private final List<String> allowedContentTypes = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    public String storeFile(MultipartFile file, Long userId) throws Exception {
        validateFile(file);

        String fileName = generateFileName(file, userId);

        Path userUploadDir = Paths.get(uploadDir, "users", userId.toString());
        createDirectories(userUploadDir);

        Path targetLocation = userUploadDir.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved: {}", targetLocation);
        } catch (IOException e) {
            throw new Exception("Could not store file " + fileName, e);
        }

        return targetLocation.toString();
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (!allowedContentTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("File type not allowed: " + file.getContentType());
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new IllegalArgumentException("File size exceeds 5MB");
        }
    }

    private String generateFileName(MultipartFile file, Long userId) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);
        String timestamp = String.valueOf(System.currentTimeMillis());

        return userId + "_" + timestamp + "." + fileExtension;
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not create upload directory", e);
        }
    }
}