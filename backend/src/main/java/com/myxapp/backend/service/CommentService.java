package com.myxapp.backend.service;

import com.myxapp.backend.model.Comment;
import com.myxapp.backend.model.Post;
import com.myxapp.backend.model.User;
import com.myxapp.backend.repository.CommentRepository;
import com.myxapp.backend.repository.PostRepository;
import com.myxapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    // Pobierz wszystkie komentarze do danego posta
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    // Pobierz komentarze napisane przez danego użytkownika
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.findByAuthorIdOrderByCreatedAtDesc(userId);
    }

    // Dodaj nowy komentarz
    @Transactional
    public Comment createComment(Long postId, Long userId, String content) {
        // Sprawdź czy post istnieje
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post nie znaleziony"));

        // Sprawdź czy użytkownik istnieje
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // Sprawdź długość komentarza
        if (content.length() > 280) {
            throw new RuntimeException("Komentarz nie może mieć więcej niż 280 znaków!");
        }

        // Utwórz nowy komentarz
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setAuthor(user);

        // Zapisz komentarz
        Comment savedComment = commentRepository.save(comment);

        // Zwiększ licznik komentarzy w poście
        post.setRepliesCount(post.getRepliesCount() + 1);
        postRepository.save(post);

        return savedComment;
    }

    // Usuń komentarz
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Komentarz nie znaleziony"));

        // Sprawdź czy użytkownik jest autorem komentarza
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Nie możesz usunąć cudzego komentarza!");
        }

        // Zmniejsz licznik komentarzy w poście
        Post post = comment.getPost();
        post.setRepliesCount(Math.max(0, post.getRepliesCount() - 1));
        postRepository.save(post);

        // Usuń komentarz
        commentRepository.delete(comment);
    }

    // Polub komentarz
    @Transactional
    public Comment likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Komentarz nie znaleziony"));

        comment.setLikesCount(comment.getLikesCount() + 1);
        return commentRepository.save(comment);
    }

    // Pobierz komentarz po ID
    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }
}
