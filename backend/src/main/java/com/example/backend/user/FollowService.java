package com.example.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Autowired
    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    public boolean follow(User follower, User following) {
        if (follower.getId().equals(following.getId())) return false;
        if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) return false;
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);
        return true;
    }

    public boolean unfollow(User follower, User following) {
        Optional<Follow> followOpt = followRepository.findByFollowerAndFollowing(follower, following);
        if (followOpt.isPresent()) {
            followRepository.delete(followOpt.get());
            return true;
        }
        return false;
    }

    public List<User> getFollowers(User user) {
        return followRepository.findByFollowing(user).stream().map(Follow::getFollower).toList();
    }

    public List<User> getFollowing(User user) {
        return followRepository.findByFollower(user).stream().map(Follow::getFollowing).toList();
    }

    public long countFollowers(User user) {
        return followRepository.countByFollowing(user);
    }

    public long countFollowing(User user) {
        return followRepository.countByFollower(user);
    }
} 