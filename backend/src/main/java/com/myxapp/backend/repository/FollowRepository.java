package com.myxapp.backend.repository;

import com.myxapp.backend.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // Sprawdź czy użytkownik już obserwuje innego użytkownika
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // Znajdź konkretne obserwowanie (żeby móc je usunąć)
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // Znajdź wszystkich obserwujących danego użytkownika
    List<Follow> findByFollowingIdOrderByCreatedAtDesc(Long followingId);

    // Znajdź wszystkich obserwowanych przez danego użytkownika
    List<Follow> findByFollowerIdOrderByCreatedAtDesc(Long followerId);

    // Policz ile obserwujących ma użytkownik
    long countByFollowingId(Long followingId);

    // Policz ilu użytkowników obserwuje dany użytkownik
    long countByFollowerId(Long followerId);

    // Usuń obserwowanie
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
