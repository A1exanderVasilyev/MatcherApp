package ru.vasilyev.MatcherApp.services;

import org.springframework.stereotype.Service;
import ru.vasilyev.MatcherApp.models.User;
import ru.vasilyev.MatcherApp.repositoies.UserRepository;

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
        return userRepository.findUserByEmail(email).orElseThrow();
    }
}
