package com.myxapp.backend.controller;

import com.myxapp.backend.model.Media;
import com.myxapp.backend.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "*")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    // Dodaj media do posta
    @PostMapping
    public ResponseEntity<Media> addMedia(@RequestBody Map<String, Object> request) {
        try {
            Long postId = Long.valueOf(request.get("postId").toString());
            String url = request.get("url").toString();
            String type = request.get("type").toString();
            String altText = request.getOrDefault("altText", "").toString();

            Media savedMedia = mediaService.addMediaToPost(postId, url, type, altText);
            return ResponseEntity.ok(savedMedia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Pobierz media dla posta
    @GetMapping("/post/{postId}")
    public List<Media> getMediaByPost(@PathVariable Long postId) {
        return mediaService.getMediaByPostId(postId);
    }

    // Pobierz media według typu
    @GetMapping("/type/{type}")
    public List<Media> getMediaByType(@PathVariable String type) {
        return mediaService.getMediaByType(type);
    }

    // Usuń media
    @DeleteMapping("/{mediaId}")
    public ResponseEntity<?> deleteMedia(@PathVariable Long mediaId) {
        try {
            mediaService.deleteMedia(mediaId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Pobierz media po ID
    @GetMapping("/{mediaId}")
    public ResponseEntity<Media> getMediaById(@PathVariable Long mediaId) {
        return mediaService.getMediaById(mediaId)
                .map(media -> ResponseEntity.ok().body(media))
                .orElse(ResponseEntity.notFound().build());
    }

    // Policz media dla posta
    @GetMapping("/count/{postId}")
    public ResponseEntity<Map<String, Long>> countMedia(@PathVariable Long postId) {
        long count = mediaService.countMediaByPostId(postId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Zaktualizuj alt text
    @PutMapping("/{mediaId}/alt-text")
    public ResponseEntity<Media> updateAltText(@PathVariable Long mediaId, @RequestBody Map<String, String> request) {
        try {
            String newAltText = request.get("altText");
            Media updatedMedia = mediaService.updateAltText(mediaId, newAltText);
            return ResponseEntity.ok(updatedMedia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
