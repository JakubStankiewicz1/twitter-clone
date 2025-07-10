package com.myxapp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 280)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User author;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Column(name = "retweets_count")
    private Integer retweetsCount = 0;

    @Column(name = "replies_count")
    private Integer repliesCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("post")
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("post")
    private List<Like> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("post")
    private List<Media> mediaFiles;

    public Post() {
        this.createdAt = LocalDateTime.now();
    }

    // Gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getLikesCount() { return likesCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }

    public Integer getRetweetsCount() { return retweetsCount; }
    public void setRetweetsCount(Integer retweetsCount) { this.retweetsCount = retweetsCount; }

    public Integer getRepliesCount() { return repliesCount; }
    public void setRepliesCount(Integer repliesCount) { this.repliesCount = repliesCount; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public List<Like> getLikes() { return likes; }
    public void setLikes(List<Like> likes) { this.likes = likes; }

    public List<Media> getMediaFiles() { return mediaFiles; }
    public void setMediaFiles(List<Media> mediaFiles) { this.mediaFiles = mediaFiles; }
}