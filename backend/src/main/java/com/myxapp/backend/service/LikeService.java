package com.myxapp.backend.service;

import com.myxapp.backend.model.Like;
import com.myxapp.backend.model.Post;
import com.myxapp.backend.model.User;
import com.myxapp.backend.repository.LikeRepository;
import com.myxapp.backend.repository.PostRepository;
import com.myxapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    // Polub post
    @Transactional
    public Like likePost(Long postId, Long userId) {
        // Sprawdź czy post istnieje
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post nie znaleziony"));

        // Sprawdź czy użytkownik istnieje
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // Sprawdź czy użytkownik już polubił ten post
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new RuntimeException("Post już został polubiony przez tego użytkownika!");
        }

        // Utwórz nowe polubienie
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        // Zapisz polubienie
        Like savedLike = likeRepository.save(like);

        // Zwiększ licznik polubień w poście
        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);

        return savedLike;
    }

    // Usuń polubienie (unlike)
    @Transactional
    public void unlikePost(Long postId, Long userId) {
        // Znajdź polubienie
        Like like = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new RuntimeException("Polubienie nie znalezione"));

        // Zmniejsz licznik polubień w poście
        Post post = like.getPost();
        post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        postRepository.save(post);

        // Usuń polubienie
        likeRepository.delete(like);
    }

    // Sprawdź czy użytkownik polubił post
    public boolean isPostLikedByUser(Long postId, Long userId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    // Pobierz wszystkie polubienia danego posta
    public List<Like> getLikesByPostId(Long postId) {
        return likeRepository.findByPostId(postId);
    }

    // Pobierz wszystkie polubienia danego użytkownika
    public List<Like> getLikesByUserId(Long userId) {
        return likeRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Policz polubienia posta
    public long countLikesByPostId(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    // Toggle like (polub jeśli nie polubione, usuń jeśli polubione)
    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        if (isPostLikedByUser(postId, userId)) {
            unlikePost(postId, userId);
            return false; // Usunięto polubienie
        } else {
            likePost(postId, userId);
            return true; // Dodano polubienie
        }
    }
}
