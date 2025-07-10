package com.myxapp.backend.service;

import com.myxapp.backend.dto.AuthResponse;
import com.myxapp.backend.dto.LoginRequest;
import com.myxapp.backend.dto.RegisterRequest;
import com.myxapp.backend.dto.UserResponse;
import com.myxapp.backend.model.User;
import com.myxapp.backend.repository.UserRepository;
import com.myxapp.backend.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse login(LoginRequest loginRequest) {
        String emailOrUsername = loginRequest.getEmailOrUsername();
        String password = loginRequest.getPassword();
        
        // Znajdź użytkownika po username lub email
        Optional<User> userOpt = userRepository.findByUsername(emailOrUsername);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(emailOrUsername);
        }
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Nieprawidłowe dane logowania");
        }
        
        User user = userOpt.get();
        
        // Sprawdź hasło
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Nieprawidłowe dane logowania");
        }
        
        // Wygeneruj JWT token
        String token = jwtUtils.generateJwtToken(user.getUsername());
        
        return new AuthResponse(token, user.getId(), user.getUsername(), 
                               user.getEmail(), user.getDisplayName());
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername().trim();
        String email = registerRequest.getEmail().trim().toLowerCase();
        String password = registerRequest.getPassword();
        String displayName = registerRequest.getDisplayName();
        
        // Walidacja danych
        if (username.isEmpty()) {
            throw new RuntimeException("Username nie może być pusty");
        }
        if (email.isEmpty()) {
            throw new RuntimeException("Email nie może być pusty");
        }
        if (password.length() < 6) {
            throw new RuntimeException("Hasło musi mieć co najmniej 6 znaków");
        }

        // Sprawdź czy username już istnieje
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Nazwa użytkownika już istnieje");
        }

        // Sprawdź czy email już istnieje
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email już istnieje");
        }

        // Utwórz nowego użytkownika
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Zahashuj hasło
        user.setDisplayName(displayName != null && !displayName.trim().isEmpty() 
                           ? displayName.trim() : username);

        user = userRepository.save(user);
        
        // Wygeneruj JWT token
        String token = jwtUtils.generateJwtToken(user.getUsername());
        
        return new AuthResponse(token, user.getId(), user.getUsername(), 
                               user.getEmail(), user.getDisplayName());
    }
    
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
                
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
                               user.getDisplayName(), user.getBio(), user.getFollowersCount(),
                               user.getFollowingCount(), user.getPostsCount());
    }

    // Sprawdź czy użytkownik o danym ID istnieje
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    // Pobierz użytkownika po username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Pobierz użytkownika po email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
