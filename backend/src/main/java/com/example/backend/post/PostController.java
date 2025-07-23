package com.example.backend.post;

import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.example.backend.security.JwtUtil;
import com.example.backend.like.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final LikeService likeService;

    @Autowired
    public PostController(PostService postService, UserRepository userRepository, JwtUtil jwtUtil, LikeService likeService) {
        this.postService = postService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.likeService = likeService;
    }

    // Tworzenie postu
    @PostMapping
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String authHeader, @RequestBody PostRequest request) {
        String username = getUsernameFromHeader(authHeader);
        if (username == null) return ResponseEntity.status(401).body("Brak tokenu");
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("Nie znaleziono użytkownika");
        Post post = new Post();
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        post.setUser(userOpt.get());
        return ResponseEntity.status(201).body(postService.createPost(post));
    }

    // Edycja postu
    @PutMapping("/{id}")
    public ResponseEntity<?> editPost(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody PostRequest request) {
        String username = getUsernameFromHeader(authHeader);
        if (username == null) return ResponseEntity.status(401).body("Brak tokenu");
        Optional<Post> postOpt = postService.getPost(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body("Nie znaleziono postu");
        Post post = postOpt.get();
        if (!post.getUser().getUsername().equals(username)) return ResponseEntity.status(403).body("Brak uprawnień");
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        Post updated = postService.updatePost(post);
        return ResponseEntity.ok().body(java.util.Map.of("message", "Post zaktualizowany", "post", updated));
    }

    // Usuwanie postu
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String username = getUsernameFromHeader(authHeader);
        if (username == null) return ResponseEntity.status(401).body("Brak tokenu");
        Optional<Post> postOpt = postService.getPost(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body("Nie znaleziono postu");
        Post post = postOpt.get();
        if (!post.getUser().getUsername().equals(username)) return ResponseEntity.status(403).body("Brak uprawnień");
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // Feed główny (wszystkie posty)
    @GetMapping("/feed")
    public List<Post> getFeed() {
        return postService.getAllPosts();
    }

    // Pobieranie postów użytkownika
    @GetMapping("/user/{userId}")
    public List<Post> getUserPosts(@PathVariable Long userId) {
        return postService.getPostsByUser(userId);
    }

    // Lajkowanie/odlajkowanie postu
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String username = getUsernameFromHeader(authHeader);
        if (username == null) return ResponseEntity.status(401).body("Brak tokenu");
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("Nie znaleziono użytkownika");
        Optional<Post> postOpt = postService.getPost(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body("Nie znaleziono postu");
        User user = userOpt.get();
        Post post = postOpt.get();
        if (likeService.hasUserLikedPost(user, post)) {
            return ResponseEntity.status(409).body("Już polubiłeś ten post");
        }
        likeService.likePost(user, post);
        long likeCount = likeService.countLikes(post);
        return ResponseEntity.ok(java.util.Map.of("likeCount", likeCount));
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<?> unlikePost(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String username = getUsernameFromHeader(authHeader);
        if (username == null) return ResponseEntity.status(401).body("Brak tokenu");
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("Nie znaleziono użytkownika");
        Optional<Post> postOpt = postService.getPost(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body("Nie znaleziono postu");
        User user = userOpt.get();
        Post post = postOpt.get();
        if (!likeService.hasUserLikedPost(user, post)) {
            return ResponseEntity.status(409).body("Nie polubiłeś tego postu");
        }
        likeService.unlikePost(user, post);
        long likeCount = likeService.countLikes(post);
        return ResponseEntity.ok(java.util.Map.of("likeCount", likeCount));
    }

    // Wyszukiwanie postów po słowach kluczowych
    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(@RequestParam("query") String query) {
        List<Post> posts = postService.searchPostsByKeyword(query);
        return ResponseEntity.ok(posts);
    }

    // Pobieranie pojedynczego posta po ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        Optional<Post> postOpt = postService.getPost(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body("Nie znaleziono posta");
        return ResponseEntity.ok(postOpt.get());
    }

    private String getUsernameFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) return null;
        return jwtUtil.getUsernameFromToken(token);
    }

    public static class PostRequest {
        private String content;
        private String imageUrl;
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
