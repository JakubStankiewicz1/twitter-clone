package com.myxapp.backend.service;

import com.myxapp.backend.model.Post;
import com.myxapp.backend.model.User;
import com.myxapp.backend.repository.PostRepository;
import com.myxapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAllOrderByCreatedAtDesc();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(userId);
    }

    public Post createPost(Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User nie znaleziony"));

        if (content.length() > 280) {
            throw new RuntimeException("Post nie może mieć więcej niż 280 znaków!");
        }

        Post post = new Post();
        post.setContent(content);
        post.setAuthor(user);

        return postRepository.save(post);
    }

    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post nie znaleziony"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Nie możesz usunąć cudzego posta!");
        }

        postRepository.delete(post);
    }

    public Post likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post nie znaleziony"));

        post.setLikesCount(post.getLikesCount() + 1);
        return postRepository.save(post);
    }

    public List<Post> searchPosts(String keyword) {
        return postRepository.findByContentContainingOrderByCreatedAtDesc(keyword);
    }
}