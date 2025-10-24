package ru.vasilyev.MatcherApp.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vasilyev.MatcherApp.dto.UserRegistrationDTO;
import ru.vasilyev.MatcherApp.models.User;
import ru.vasilyev.MatcherApp.models.UserPhoto;
import ru.vasilyev.MatcherApp.repositoies.UserRepository;
import ru.vasilyev.MatcherApp.util.exceptions.UserExistsException;

@Service
@Slf4j
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPhotoService userPhotoService;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserPhotoService userPhotoService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userPhotoService = userPhotoService;
    }

    @Transactional
    public void register(UserRegistrationDTO userRegistrationDTO) throws UserExistsException {
        if (userRepository.existsByEmail(userRegistrationDTO.getEmail())) {
            throw new UserExistsException("Пользователь с таким email уже существует");
        }

        userRegistrationDTO.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        User user = new User(userRegistrationDTO);
        User savedUser = userRepository.save(user);

        if (userRegistrationDTO.hasProfilePhoto()) {
            try {
                UserPhoto userPhoto = userPhotoService.uploadPhoto(savedUser.getId(), userRegistrationDTO.getProfilePhoto(), true);
                log.info("Profile photo uploaded for user {}: {}", savedUser.getId(), userPhoto.getId());

            } catch (Exception e) {
                log.warn("Failed to upload profile photo for user {}: {}", savedUser.getId(), e.getMessage());
            }
        }

    }
}
