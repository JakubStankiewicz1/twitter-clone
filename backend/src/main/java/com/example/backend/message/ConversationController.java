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
    public ResponseEntity<List<ConversationDto>> getUserConversations(@AuthenticationPrincipal String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        List<ConversationDto> dtos = conversationService.getUserConversations(user).stream()
                .map(c -> new ConversationDto(c.getId(), c.getUser1().getUsername(), c.getUser2().getUsername()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
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