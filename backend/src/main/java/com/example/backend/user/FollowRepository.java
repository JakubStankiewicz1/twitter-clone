package com.example.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowing(User following);
    long countByFollower(User follower);
    long countByFollowing(User following);
    void deleteByFollowerAndFollowing(User follower, User following);
} 