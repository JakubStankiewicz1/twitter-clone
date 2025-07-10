package com.myxapp.backend.controller;

import com.myxapp.backend.model.Like;
import com.myxapp.backend.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "*")
public class LikeController {

    @Autowired
    private LikeService likeService;

    // Toggle like (polub/usuń polubienie)
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleLike(@RequestBody Map<String, Object> request) {
        try {
            Long postId = Long.valueOf(request.get("postId").toString());
            Long userId = Long.valueOf(request.get("userId").toString());

            boolean isLiked = likeService.toggleLike(postId, userId);

            Map<String, Object> response = Map.of(
                "isLiked", isLiked,
                "message", isLiked ? "Post polubiony!" : "Polubienie usunięte!"
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Sprawdź czy użytkownik polubił post
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkIfLiked(
            @RequestParam Long postId,
            @RequestParam Long userId) {

        boolean isLiked = likeService.isPostLikedByUser(postId, userId);
        return ResponseEntity.ok(Map.of("isLiked", isLiked));
    }

    // Pobierz polubienia posta
    @GetMapping("/post/{postId}")
    public List<Like> getLikesByPost(@PathVariable Long postId) {
        return likeService.getLikesByPostId(postId);
    }

    // Pobierz polubienia użytkownika
    @GetMapping("/user/{userId}")
    public List<Like> getLikesByUser(@PathVariable Long userId) {
        return likeService.getLikesByUserId(userId);
    }

    // Policz polubienia posta
    @GetMapping("/count/{postId}")
    public ResponseEntity<Map<String, Long>> countLikes(@PathVariable Long postId) {
        long count = likeService.countLikesByPostId(postId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
