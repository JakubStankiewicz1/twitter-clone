package com.myxapp.backend.controller;

import com.myxapp.backend.model.Follow;
import com.myxapp.backend.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follow")
@CrossOrigin(origins = "*")
public class FollowController {

    @Autowired
    private FollowService followService;

    // Toggle follow (obserwuj/przestań obserwować)
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleFollow(@RequestBody Map<String, Object> request) {
        try {
            Long followerId = Long.valueOf(request.get("followerId").toString());
            Long followingId = Long.valueOf(request.get("followingId").toString());

            boolean isFollowing = followService.toggleFollow(followerId, followingId);

            Map<String, Object> response = Map.of(
                "isFollowing", isFollowing,
                "message", isFollowing ? "Zacząłeś obserwować!" : "Przestałeś obserwować!"
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = Map.of(
                "error", e.getMessage()
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Sprawdź czy użytkownik obserwuje innego
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkIfFollowing(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {

        boolean isFollowing = followService.isFollowing(followerId, followingId);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }

    // Pobierz obserwujących użytkownika
    @GetMapping("/{userId}/followers")
    public List<Follow> getFollowers(@PathVariable Long userId) {
        return followService.getFollowers(userId);
    }

    // Pobierz obserwowanych przez użytkownika
    @GetMapping("/{userId}/following")
    public List<Follow> getFollowing(@PathVariable Long userId) {
        return followService.getFollowing(userId);
    }

    // Policz obserwujących
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Map<String, Long>> countFollowers(@PathVariable Long userId) {
        long count = followService.countFollowers(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Policz obserwowanych
    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Map<String, Long>> countFollowing(@PathVariable Long userId) {
        long count = followService.countFollowing(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
