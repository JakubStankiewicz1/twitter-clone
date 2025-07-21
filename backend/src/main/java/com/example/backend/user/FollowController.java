package com.example.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follow")
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;

    private String getCurrentUsername() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof String) {
            return (String) principal;
        }
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        }
        return null;
    }

    // Follow user
    @PostMapping("/{username}")
    public ResponseEntity<?> follow(@AuthenticationPrincipal String currentUsername, @PathVariable String username) {
        if (currentUsername.equals(username)) return ResponseEntity.badRequest().body("Nie można obserwować samego siebie");
        User follower = userService.findByUsername(currentUsername).orElse(null);
        User following = userService.findByUsername(username).orElse(null);
        if (follower == null || following == null) return ResponseEntity.notFound().build();
        boolean ok = followService.follow(follower, following);
        if (!ok) return ResponseEntity.badRequest().body("Już obserwujesz lub błąd");
        return ResponseEntity.ok().body("Obserwujesz użytkownika " + username);
    }

    // Unfollow user
    @DeleteMapping("/{username}")
    public ResponseEntity<?> unfollow(@AuthenticationPrincipal String currentUsername, @PathVariable String username) {
        User follower = userService.findByUsername(currentUsername).orElse(null);
        User following = userService.findByUsername(username).orElse(null);
        if (follower == null || following == null) return ResponseEntity.notFound().build();
        boolean ok = followService.unfollow(follower, following);
        if (!ok) return ResponseEntity.badRequest().body("Nie obserwujesz tego użytkownika");
        return ResponseEntity.ok().body("Przestałeś obserwować użytkownika " + username);
    }

    // Get followers
    @GetMapping("/{username}/followers")
    public ResponseEntity<List<String>> getFollowers(@PathVariable String username) {
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        List<String> followers = followService.getFollowers(user).stream().map(User::getUsername).collect(Collectors.toList());
        return ResponseEntity.ok(followers);
    }

    // Get following
    @GetMapping("/{username}/following")
    public ResponseEntity<List<String>> getFollowing(@PathVariable String username) {
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        List<String> following = followService.getFollowing(user).stream().map(User::getUsername).collect(Collectors.toList());
        return ResponseEntity.ok(following);
    }

    // Get counts
    @GetMapping("/{username}/counts")
    public ResponseEntity<?> getCounts(@PathVariable String username) {
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        long followers = followService.countFollowers(user);
        long following = followService.countFollowing(user);
        return ResponseEntity.ok(java.util.Map.of("followers", followers, "following", following));
    }
} 