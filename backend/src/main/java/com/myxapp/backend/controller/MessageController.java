package com.myxapp.backend.controller;

import com.myxapp.backend.model.Message;
import com.myxapp.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/conversation")
    public List<Message> getConversation(@RequestParam Long user1, @RequestParam Long user2) {
        return messageService.getConversation(user1, user2);
    }

    @GetMapping("/user/{userId}")
    public List<Message> getAllUserMessages(@PathVariable Long userId) {
        return messageService.getAllUserMessages(userId);
    }

    @PostMapping
    public Message sendMessage(@RequestBody Map<String, Object> req) {
        Long senderId = Long.valueOf(req.get("senderId").toString());
        Long recipientId = Long.valueOf(req.get("recipientId").toString());
        String content = req.get("content").toString();
        return messageService.sendMessage(senderId, recipientId, content);
    }
} 