package com.example.backend.comment;

import com.example.backend.post.Post;
import com.example.backend.post.PostService;
import com.example.backend.user.User;
import com.example.backend.user.UserService;
import com.example.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

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

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@PathVariable Long postId, @RequestBody String content, @AuthenticationPrincipal String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Post post = postService.getPost(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        Comment comment = commentService.createComment(content, user, post);
        CommentDto commentDto = new CommentDto(comment.getId(), comment.getContent(), user.getUsername(), comment.getCreatedAt());

        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentDto> comments = commentService.getCommentsByPost(postId).stream()
                .map(comment -> new CommentDto(comment.getId(), comment.getContent(), comment.getUser().getUsername(), comment.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> editComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody String content, @AuthenticationPrincipal String username) {
        // Pobierz komentarz i sprawdź właściciela
        Comment comment = commentService.getCommentsByPost(postId).stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));
        if (!comment.getUser().getUsername().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        Comment updated = commentService.editComment(commentId, content);
        CommentDto dto = new CommentDto(updated.getId(), updated.getContent(), updated.getUser().getUsername(), updated.getCreatedAt());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/comments/user/{username}")
    @ResponseBody
    public ResponseEntity<List<CommentDto>> getCommentsByUser(@PathVariable("username") String username) {
        List<CommentDto> comments = commentService.getCommentsByUsername(username).stream()
                .map(comment -> new CommentDto(comment.getId(), comment.getContent(), comment.getUser().getUsername(), comment.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }
} 