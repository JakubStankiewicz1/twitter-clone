package com.myxapp.backend.service;

import com.myxapp.backend.model.Media;
import com.myxapp.backend.model.Post;
import com.myxapp.backend.repository.MediaRepository;
import com.myxapp.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private PostRepository postRepository;

    // Dodaj media do posta
    @Transactional
    public Media addMediaToPost(Long postId, String url, String type, String altText) {
        // Sprawdź czy post istnieje
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post nie znaleziony"));

        // Walidacja typu
        if (!type.equals("image") && !type.equals("video")) {
            throw new RuntimeException("Typ musi być 'image' lub 'video'!");
        }

        // Walidacja URL (podstawowa)
        if (url == null || url.trim().isEmpty()) {
            throw new RuntimeException("URL nie może być pusty!");
        }

        // Sprawdź limit mediów na post (max 4 jak na prawdziwym X)
        long mediaCount = mediaRepository.countByPostId(postId);
        if (mediaCount >= 4) {
            throw new RuntimeException("Post może mieć maksymalnie 4 media!");
        }

        // Utwórz nowe media
        Media media = new Media();
        media.setUrl(url);
        media.setType(type);
        media.setAltText(altText);
        media.setPost(post);

        return mediaRepository.save(media);
    }

    // Pobierz media dla danego posta
    public List<Media> getMediaByPostId(Long postId) {
        return mediaRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    // Pobierz media według typu
    public List<Media> getMediaByType(String type) {
        return mediaRepository.findByTypeOrderByCreatedAtDesc(type);
    }

    // Usuń media
    @Transactional
    public void deleteMedia(Long mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media nie znalezione"));

        mediaRepository.delete(media);
    }

    // Pobierz media po ID
    public Optional<Media> getMediaById(Long mediaId) {
        return mediaRepository.findById(mediaId);
    }

    // Usuń wszystkie media dla posta
    @Transactional
    public void deleteAllMediaForPost(Long postId) {
        mediaRepository.deleteByPostId(postId);
    }

    // Policz media dla posta
    public long countMediaByPostId(Long postId) {
        return mediaRepository.countByPostId(postId);
    }

    // Zaktualizuj alt text
    @Transactional
    public Media updateAltText(Long mediaId, String newAltText) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media nie znalezione"));

        media.setAltText(newAltText);
        return mediaRepository.save(media);
    }
}
