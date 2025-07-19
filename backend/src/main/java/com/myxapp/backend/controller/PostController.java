package com.myxapp.backend.controller;

import com.myxapp.backend.model.Post;
import com.myxapp.backend.service.PostService;
import com.myxapp.backend.service.LikeService;
import com.myxapp.backend.service.MediaService;
import com.myxapp.backend.dto.PostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private MediaService mediaService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(post -> ResponseEntity.ok().body(post))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUser(@PathVariable Long userId) {
        return postService.getPostsByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String content = request.get("content").toString();

            Post savedPost = postService.createPost(userId, content);

            // Jeśli są media do dodania
            if (request.containsKey("mediaUrls")) {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> mediaList = (List<Map<String, String>>) request.get("mediaUrls");

                for (Map<String, String> media : mediaList) {
                    String url = media.get("url");
                    String type = media.get("type");
                    String altText = media.getOrDefault("altText", "");

                    mediaService.addMediaToPost(savedPost.getId(), url, type, altText);
                }
            }

            return ResponseEntity.ok(savedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{postId}/user/{userId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, @PathVariable Long userId) {
        try {
            postService.deletePost(postId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Nowy endpoint - polub post z użytkownikiem
    @PostMapping("/{postId}/like/{userId}")
    public ResponseEntity<Map<String, Object>> likePostByUser(@PathVariable Long postId, @PathVariable Long userId) {
        try {
            boolean isLiked = likeService.toggleLike(postId, userId);
            Map<String, Object> response = Map.of(
                "success", true,
                "isLiked", isLiked,
                "message", isLiked ? "Post polubiony!" : "Polubienie usunięte!"
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public List<Post> searchPosts(@RequestParam String q) {
        return postService.searchPosts(q);
    }

    @GetMapping("/all")
    public List<Post> getAllPostsRaw() {
        return postService.getAllPosts();
    }

    @GetMapping("/foryou")
    public List<PostResponse> getForYouPosts() {
        return postService.getAllPosts().stream().map(this::toDto).toList();
    }

    @GetMapping("/following")
    public List<PostResponse> getFollowingPosts(@RequestParam("userId") Long userId) {
        return postService.getPostsForFollowing(userId).stream().map(this::toDto).toList();
    }

    private PostResponse toDto(Post post) {
        PostResponse dto = new PostResponse();
        dto.id = post.getId();
        dto.userId = post.getAuthor().getId();
        dto.content = post.getContent();
        dto.createdAt = post.getCreatedAt();
        dto.displayName = post.getAuthor().getDisplayName();
        dto.username = post.getAuthor().getUsername();
        if (post.getMedia() != null) {
            dto.mediaUrls = post.getMedia().stream().map(media -> {
                PostResponse.MediaDto m = new PostResponse.MediaDto();
                m.url = media.getUrl();
                m.type = media.getType();
                m.altText = media.getAltText();
                return m;
            }).toList();
        }
        return dto;
    }
}