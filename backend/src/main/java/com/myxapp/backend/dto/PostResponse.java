package com.myxapp.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {
    public Long id;
    public Long userId;
    public String content;
    public List<MediaDto> mediaUrls;
    public LocalDateTime createdAt;
    public String displayName;
    public String username;

    public static class MediaDto {
        public String url;
        public String type;
        public String altText;
    }
} 