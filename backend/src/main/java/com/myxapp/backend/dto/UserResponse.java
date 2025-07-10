package com.myxapp.backend.dto;

public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private String bio;
    private Integer followersCount;
    private Integer followingCount;
    private Integer postsCount;
    
    public UserResponse() {}
    
    public UserResponse(Long id, String username, String email, String displayName, String bio, 
                       Integer followersCount, Integer followingCount, Integer postsCount) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.bio = bio;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.postsCount = postsCount;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public Integer getFollowersCount() {
        return followersCount;
    }
    
    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }
    
    public Integer getFollowingCount() {
        return followingCount;
    }
    
    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }
    
    public Integer getPostsCount() {
        return postsCount;
    }
    
    public void setPostsCount(Integer postsCount) {
        this.postsCount = postsCount;
    }
}
