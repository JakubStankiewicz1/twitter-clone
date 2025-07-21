package com.example.backend.message;

import com.example.backend.user.User;
import com.example.backend.user.UserService;
import com.example.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@PathVariable Long conversationId, @RequestBody SendMessageRequest request, @AuthenticationPrincipal String username) {
        System.out.println("[DEBUG] sendMessage: username=" + username);
        User sender = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Conversation c = conversationService.getByIdAndParticipant(conversationId, sender.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found or access denied"));

        User receiver = c.getUser1().getId().equals(sender.getId()) ? c.getUser2() : c.getUser1();
        Message m = messageService.sendMessage(c, sender, receiver, request.getContent());
        MessageDto dto = new MessageDto(m.getId(), m.getContent(), sender.getUsername(), receiver.getUsername(), m.getSentAt(), m.isRead());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long conversationId, @AuthenticationPrincipal String username) {
        System.out.println("[DEBUG] getMessages: username=" + username);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Conversation c = conversationService.getByIdAndParticipant(conversationId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found or access denied"));

        List<MessageDto> dtos = messageService.getMessages(conversationId).stream()
                .map(m -> new MessageDto(m.getId(), m.getContent(), m.getSender().getUsername(), m.getReceiver().getUsername(), m.getSentAt(), m.isRead()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId, @AuthenticationPrincipal String username) {
        System.out.println("[DEBUG] markAsRead: username=" + username);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Message m = messageService.getById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found: " + messageId));
        if (!m.getReceiver().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        messageService.markAsRead(m);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId, @AuthenticationPrincipal String username) {
        System.out.println("[DEBUG] deleteMessage: username=" + username);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Message m = messageService.getById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found: " + messageId));
        if (!m.getSender().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
} 