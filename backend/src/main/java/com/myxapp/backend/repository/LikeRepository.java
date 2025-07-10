package com.myxapp.backend.repository;

import com.myxapp.backend.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Sprawdź czy użytkownik już polubił dany post
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // Znajdź konkretne polubienie (żeby móc je usunąć)
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    // Znajdź wszystkie polubienia danego posta
    List<Like> findByPostId(Long postId);

    // Znajdź wszystkie polubienia danego użytkownika
    List<Like> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Policz ile polubień ma dany post
    long countByPostId(Long postId);

    // Usuń polubienie na podstawie userId i postId
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
