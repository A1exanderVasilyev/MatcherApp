package ru.vasilyev.MatcherApp.services;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vasilyev.MatcherApp.dto.UserUpdateDTO;
import ru.vasilyev.MatcherApp.models.User;
import ru.vasilyev.MatcherApp.repositoies.UserRepository;
import ru.vasilyev.MatcherApp.util.exceptions.UserExistsException;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("Пользователь не найден"));
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateDTO userUpdateDTO) throws UserExistsException {
        User user = findById(userId);
        String dtoEmail = userUpdateDTO.getEmail();
        if (!user.getEmail().equals(dtoEmail) && userRepository.existsByEmail(dtoEmail)) {
            throw new UserExistsException("Email " + userUpdateDTO.getEmail() + " уже используется");
        }
        user.setUsername(userUpdateDTO.getUsername());
        user.setEmail(dtoEmail);
        user.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        user.setCountry(userUpdateDTO.getCountry());
        user.setCity(userUpdateDTO.getCity());
//        user.setLongitude(userUpdateDTO.getLongitude());
//        user.setLatitude(userUpdateDTO.getLatitude());
        user.setGender(userUpdateDTO.getGender());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
