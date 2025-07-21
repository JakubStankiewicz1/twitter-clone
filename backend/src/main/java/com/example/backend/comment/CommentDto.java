package com.example.backend.comment;

import java.time.LocalDateTime;

public class CommentDto {

    private Long id;
    private String content;
    private String username;
    private LocalDateTime createdAt;

    public CommentDto(Long id, String content, String username, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 