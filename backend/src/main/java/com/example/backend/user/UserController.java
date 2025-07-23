package com.example.backend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.backend.user.FollowService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    @Autowired
    private FollowService followService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.status(201).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, @Autowired com.example.backend.security.JwtUtil jwtUtil) {
        return userService.findByUsername(request.getUsername())
                .filter(user -> userService.getPasswordEncoder().matches(request.getPassword(), user.getPassword()))
                .<ResponseEntity<?>>map(user -> {
                    String token = jwtUtil.generateToken(user.getUsername());
                    return ResponseEntity.ok().body(java.util.Map.of("token", token));
                })
                .orElse(ResponseEntity.status(401).body("Nieprawidłowy login lub hasło"));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader("Authorization") String authHeader, @Autowired com.example.backend.security.JwtUtil jwtUtil) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Brak tokenu JWT");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Nieprawidłowy token");
        }
        String username = jwtUtil.getUsernameFromToken(token);
        return userService.findByUsername(username)
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.status(404).build());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(@RequestHeader("Authorization") String authHeader, @Autowired com.example.backend.security.JwtUtil jwtUtil) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Brak tokenu JWT");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Nieprawidłowy token");
        }
        String username = jwtUtil.getUsernameFromToken(token);
        return userService.findByUsername(username)
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.status(404).build());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> editProfile(@RequestHeader("Authorization") String authHeader, @RequestBody EditProfileRequest request, @Autowired com.example.backend.security.JwtUtil jwtUtil) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Brak tokenu JWT");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Nieprawidłowy token");
        }
        String username = jwtUtil.getUsernameFromToken(token);
        return userService.findByUsername(username)
                .map(user -> {
                    if (request.getBio() != null) user.setBio(request.getBio());
                    if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
                    if (request.getPassword() != null && !request.getPassword().isBlank()) {
                        user.setPassword(userService.getPasswordEncoder().encode(request.getPassword()));
                    }
                    userService.save(user);
                    return ResponseEntity.ok().body(user);
                })
                .orElse(ResponseEntity.status(404).build());
    }

    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteProfile(@RequestHeader("Authorization") String authHeader, @Autowired com.example.backend.security.JwtUtil jwtUtil) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Brak tokenu JWT");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Nieprawidłowy token");
        }
        String username = jwtUtil.getUsernameFromToken(token);
        return userService.findByUsername(username)
                .map(user -> {
                    userService.delete(user);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.status(404).build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam("query") String query) {
        try {
            var users = userService.searchByUsername(query);
            var result = users.stream().map(user -> {
                var map = new java.util.HashMap<String, Object>();
                map.put("id", user.getId());
                map.put("username", user.getUsername());
                map.put("avatar", user.getAvatar() != null ? user.getAvatar() : "");
                map.put("bio", user.getBio() != null ? user.getBio() : "");
                return map;
            }).toList();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/{usernameOrEmail}")
    public ResponseEntity<?> getUserByUsernameOrEmail(@PathVariable String usernameOrEmail) {
        var userOpt = userService.findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = userService.findByEmail(usernameOrEmail);
        }
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Nie znaleziono użytkownika");
        }
        var user = userOpt.get();
        var map = new java.util.HashMap<String, Object>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("avatar", user.getAvatar());
        map.put("bio", user.getBio());
        long followers = followService.countFollowers(user);
        long following = followService.countFollowing(user);
        map.put("followersCount", followers);
        map.put("followingCount", following);
        return ResponseEntity.ok(map);
    }

    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class EditProfileRequest {
        private String bio;
        private String avatar;
        private String password;
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}