package com.example.backend.message;

import com.example.backend.user.User;
import com.example.backend.user.UserService;
import com.example.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private UserService userService;

    private String getCurrentUsername() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof String) {
            return (String) principal;
        }
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<ConversationFrontendDto>> getUserConversations(@AuthenticationPrincipal String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        List<ConversationFrontendDto> dtos = conversationService.getUserConversations(user).stream()
                .map(c -> {
                    User other = c.getUser1().getId().equals(user.getId()) ? c.getUser2() : c.getUser1();
                    return new ConversationFrontendDto(
                        c.getId(),
                        new ConversationFrontendDto.UserDto(
                            other.getId(),
                            other.getUsername(),
                            other.getAvatar(),
                            other.getBio()
                        )
                    );
                })
                .toList();
        return ResponseEntity.ok(dtos);
    }

    public static class ConversationFrontendDto {
        private Long id;
        private UserDto otherUser;

        public ConversationFrontendDto(Long id, UserDto otherUser) {
            this.id = id;
            this.otherUser = otherUser;
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public UserDto getOtherUser() { return otherUser; }
        public void setOtherUser(UserDto otherUser) { this.otherUser = otherUser; }

        public static class UserDto {
            private Long id;
            private String username;
            private String avatar;
            private String bio;
            public UserDto(Long id, String username, String avatar, String bio) {
                this.id = id;
                this.username = username;
                this.avatar = avatar;
                this.bio = bio;
            }
            public Long getId() { return id; }
            public void setId(Long id) { this.id = id; }
            public String getUsername() { return username; }
            public void setUsername(String username) { this.username = username; }
            public String getAvatar() { return avatar; }
            public void setAvatar(String avatar) { this.avatar = avatar; }
            public String getBio() { return bio; }
            public void setBio(String bio) { this.bio = bio; }
        }
    }

    @PostMapping("/with/{otherUsername}")
    public ResponseEntity<ConversationDto> createConversation(@PathVariable String otherUsername, @AuthenticationPrincipal String username) {
        User user1 = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        User user2 = userService.findByUsername(otherUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + otherUsername));
        Conversation c = conversationService.getOrCreateConversation(user1, user2);
        ConversationDto dto = new ConversationDto(c.getId(), c.getUser1().getUsername(), c.getUser2().getUsername());
        return ResponseEntity.ok(dto);
    }
} 