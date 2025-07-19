package com.myxapp.backend.service;

import com.myxapp.backend.model.User;
import com.myxapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username już istnieje!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email już istnieje!");
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User nie znaleziony"));

        user.setDisplayName(userDetails.getDisplayName());
        user.setBio(userDetails.getBio());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameIgnoreCaseContainingOrDisplayNameIgnoreCaseContaining(query, query);
    }
}