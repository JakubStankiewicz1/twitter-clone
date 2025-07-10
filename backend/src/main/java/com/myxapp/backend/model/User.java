package com.myxapp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(length = 500)
    private String bio;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "followers_count")
    private Integer followersCount = 0;

    @Column(name = "following_count")
    private Integer followingCount = 0;

    @Column(name = "posts_count")
    private Integer postsCount = 0;

    // Kto mnie obserwuje - UKRYJ W JSON
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Follow> followers;

    // Kogo ja obserwuję - UKRYJ W JSON
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Follow> following;

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    // Gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Nowe gettery i settery
    public Integer getFollowersCount() { return followersCount; }
    public void setFollowersCount(Integer followersCount) { this.followersCount = followersCount; }

    public Integer getFollowingCount() { return followingCount; }
    public void setFollowingCount(Integer followingCount) { this.followingCount = followingCount; }

    public Integer getPostsCount() { return postsCount; }
    public void setPostsCount(Integer postsCount) { this.postsCount = postsCount; }

    public List<Follow> getFollowers() { return followers; }
    public void setFollowers(List<Follow> followers) { this.followers = followers; }

    public List<Follow> getFollowing() { return following; }
    public void setFollowing(List<Follow> following) { this.following = following; }
}