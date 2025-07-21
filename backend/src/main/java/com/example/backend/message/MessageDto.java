package com.example.backend.message;

import java.time.LocalDateTime;

public class MessageDto {
    private Long id;
    private String content;
    private String sender;
    private String receiver;
    private LocalDateTime sentAt;
    private boolean read;

    public MessageDto(Long id, String content, String sender, String receiver, LocalDateTime sentAt, boolean read) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.sentAt = sentAt;
        this.read = read;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
} 