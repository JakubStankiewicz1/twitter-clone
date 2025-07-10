package com.myxapp.backend.repository;

import com.myxapp.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllOrderByCreatedAtDesc();

    @Query("SELECT p FROM Post p WHERE p.content LIKE %:keyword% ORDER BY p.createdAt DESC")
    List<Post> findByContentContainingOrderByCreatedAtDesc(@Param("keyword") String keyword);
}