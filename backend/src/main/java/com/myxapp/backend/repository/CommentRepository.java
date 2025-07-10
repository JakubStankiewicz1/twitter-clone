package com.myxapp.backend.repository;

import com.myxapp.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Znajdź komentarze do konkretnego posta, posortowane od najnowszych
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    // Znajdź komentarze napisane przez konkretnego użytkownika
    List<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    // Policz ile komentarzy ma dany post
    long countByPostId(Long postId);

    // Znajdź najnowsze komentarze ogólnie
    @Query("SELECT c FROM Comment c ORDER BY c.createdAt DESC")
    List<Comment> findAllOrderByCreatedAtDesc();
}
