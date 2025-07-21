package com.example.backend.like;

import com.example.backend.user.User;
import com.example.backend.post.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public boolean hasUserLikedPost(User user, Post post) {
        return likeRepository.findByUserAndPost(user, post).isPresent();
    }

    public boolean likePost(User user, Post post) {
        if (hasUserLikedPost(user, post)) return false;
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);
        return true;
    }

    public boolean unlikePost(User user, Post post) {
        Optional<Like> likeOpt = likeRepository.findByUserAndPost(user, post);
        if (likeOpt.isPresent()) {
            likeRepository.delete(likeOpt.get());
            return true;
        }
        return false;
    }

    public long countLikes(Post post) {
        return likeRepository.countByPost(post);
    }
} 