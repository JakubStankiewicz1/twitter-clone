package com.myxapp.backend.controller;

import com.myxapp.backend.model.Comment;
import com.myxapp.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // Pobierz komentarze do posta
    @GetMapping("/post/{postId}")
    public List<Comment> getCommentsByPost(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    // Pobierz komentarze użytkownika
    @GetMapping("/user/{userId}")
    public List<Comment> getCommentsByUser(@PathVariable Long userId) {
        return commentService.getCommentsByUserId(userId);
    }

    // Dodaj komentarz
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Map<String, Object> request) {
        try {
            Long postId = Long.valueOf(request.get("postId").toString());
            Long userId = Long.valueOf(request.get("userId").toString());
            String content = request.get("content").toString();

            Comment savedComment = commentService.createComment(postId, userId, content);
            return ResponseEntity.ok(savedComment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Usuń komentarz
    @DeleteMapping("/{commentId}/user/{userId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {
        try {
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Polub komentarz
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Comment> likeComment(@PathVariable Long commentId) {
        try {
            Comment likedComment = commentService.likeComment(commentId);
            return ResponseEntity.ok(likedComment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Pobierz komentarz po ID
    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId)
                .map(comment -> ResponseEntity.ok().body(comment))
                .orElse(ResponseEntity.notFound().build());
    }
}
