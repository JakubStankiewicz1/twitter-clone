package com.myxapp.backend.service;

import com.myxapp.backend.model.Follow;
import com.myxapp.backend.model.User;
import com.myxapp.backend.repository.FollowRepository;
import com.myxapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    // Obserwuj użytkownika
    @Transactional
    public Follow followUser(Long followerId, Long followingId) {
        // Sprawdź czy nie próbuje obserwować samego siebie
        if (followerId.equals(followingId)) {
            throw new RuntimeException("Nie możesz obserwować samego siebie!");
        }

        // Sprawdź czy użytkownicy istnieją
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Obserwujący nie znaleziony"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("Obserwowany nie znaleziony"));

        // Sprawdź czy już obserwuje
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new RuntimeException("Już obserwujesz tego użytkownika!");
        }

        // Utwórz nowe obserwowanie
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);

        // Zapisz obserwowanie
        Follow savedFollow = followRepository.save(follow);

        // Zwiększ liczniki
        follower.setFollowingCount(follower.getFollowingCount() + 1);
        following.setFollowersCount(following.getFollowersCount() + 1);

        userRepository.save(follower);
        userRepository.save(following);

        return savedFollow;
    }

    // Przestań obserwować użytkownika
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        // Znajdź obserwowanie
        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new RuntimeException("Nie obserwujesz tego użytkownika!"));

        // Zmniejsz liczniki
        User follower = follow.getFollower();
        User following = follow.getFollowing();

        follower.setFollowingCount(Math.max(0, follower.getFollowingCount() - 1));
        following.setFollowersCount(Math.max(0, following.getFollowersCount() - 1));

        userRepository.save(follower);
        userRepository.save(following);

        // Usuń obserwowanie
        followRepository.delete(follow);
    }

    // Sprawdź czy użytkownik obserwuje innego
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    // Pobierz obserwujących danego użytkownika
    public List<Follow> getFollowers(Long userId) {
        return followRepository.findByFollowingIdOrderByCreatedAtDesc(userId);
    }

    // Pobierz obserwowanych przez danego użytkownika
    public List<Follow> getFollowing(Long userId) {
        return followRepository.findByFollowerIdOrderByCreatedAtDesc(userId);
    }

    // Policz obserwujących
    public long countFollowers(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    // Policz obserwowanych
    public long countFollowing(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    // Toggle follow (obserwuj jeśli nie obserwuje, przestań jeśli obserwuje)
    @Transactional
    public boolean toggleFollow(Long followerId, Long followingId) {
        if (isFollowing(followerId, followingId)) {
            unfollowUser(followerId, followingId);
            return false; // Przestał obserwować
        } else {
            followUser(followerId, followingId);
            return true; // Zaczął obserwować
        }
    }
}
