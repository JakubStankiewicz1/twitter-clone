package com.example.backend.like;

import com.example.backend.user.User;
import com.example.backend.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    long countByPost(Post post);
    void deleteByUserAndPost(User user, Post post);
} 