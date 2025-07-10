package com.myxapp.backend.repository;

import com.myxapp.backend.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    // Znajdź wszystkie media dla konkretnego posta
    List<Media> findByPostIdOrderByCreatedAtAsc(Long postId);

    // Znajdź media według typu (image/video)
    List<Media> findByTypeOrderByCreatedAtDesc(String type);

    // Znajdź wszystkie media posortowane według daty utworzenia
    List<Media> findAllByOrderByCreatedAtDesc();

    // Policz ile media ma dany post
    long countByPostId(Long postId);

    // Usuń wszystkie media dla danego posta
    void deleteByPostId(Long postId);
}
